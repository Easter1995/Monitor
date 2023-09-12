package com.glimmer.service.impl;

import com.glimmer.constant.MessageConstant;
import com.glimmer.constant.TimeConstant;
import com.glimmer.context.BaseContext;
import com.glimmer.dto.UserDeleteDTO;
import com.glimmer.dto.UserInfoChangeDTO;
import com.glimmer.dto.UserLoginDTO;
import com.glimmer.dto.UserRegisterDTO;
import com.glimmer.entity.User;
import com.glimmer.exception.*;
import com.glimmer.mapper.UserTransactionMapper;
import com.glimmer.service.UserTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * service层用户接口的实现类
 * 在这里完成用户相关的具体的业务功能
 */
@Service
@Slf4j
public class UserTransactionServiceImpl implements UserTransactionService {
    /**
     * 依赖注入userTransactionMapper，可用于调用数据库相关接口
     */
    @Autowired
    private UserTransactionMapper userTransactionMapper;

    /**
     * 用户登录功能的实现
     *
     * @param userLoginDTO
     * @return
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {

        String name = userLoginDTO.getName();
        String passwd = userLoginDTO.getPasswd();

        //根据用户名查询数据库中的数据
        User user = userTransactionMapper.getByName(name);

        /**
         *判断抛出各种异常情况（用户名不存在、密码不对、账号被锁定等）
         *并在handler包的异常处理器下处理
         */
        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);

        }
        /**
         * 判断账户是否冻结以及是否在有效期,即deadlineTime如果小于当前时间，说明已经过期
         * releaseTime如果大于当前时间，说明已经冻结
         *但是注意，我们的deadlineTime和releaseTime是用的unix时间戳形式，也就是他的大小是以秒为单位的
         */
        //注意这里的System.currentTimeMillis()获取到的时间戳是以毫秒为单位
        if ((user.getDeadlineTime() < System.currentTimeMillis() / TimeConstant.MS_BETWEEN_S) || (user.getReleaseTime() > System.currentTimeMillis() / TimeConstant.MS_BETWEEN_S)) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }
        //校验密码
        if (user.getPasswd().equals(passwd)) {
            /**
             * 判断处于loginStart和loginEnd之间
             * 思路是得到当前时间，从当前时间中分离出时分秒
             * 再将它们加和求得时间戳与start和end的两个时间戳比大小
             */
            int hour = LocalTime.now().getHour();
            int minute = LocalTime.now().getMinute();
            int second = LocalTime.now().getSecond();
            //比较nowTime是否在loginStart和loginEnd之间
            //注意nowTime、loginStart和loginEnd是以纳秒为单位的
            //在这里不把nowTime转为纳秒是因为要防止数值溢出
            long nowTime = hour * TimeConstant.TIME_HOUR + minute * TimeConstant.TIME_MINUTE + second * TimeConstant.TIME_SECOND;
            if ((nowTime < user.getLoginStart() / TimeConstant.NS_BETWEEN_S) || (nowTime > user.getLoginEnd() / TimeConstant.NS_BETWEEN_S)) {
                throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
            }


        } else {
            //密码不正确，失败次数的登记加一
            Integer failTimes = user.getFailTimes();
            if (++failTimes >= 3) {
                user.setFailTimes(0);
                user.setReleaseTime(System.currentTimeMillis() / TimeConstant.MS_BETWEEN_S + TimeConstant.TIME_DAY);
                userTransactionMapper.update(user);
                String msg = "登录失败次数超过三次，冻结账户";
                throw new AccountFreezeException(msg);
            }
            user.setFailTimes(failTimes);
            userTransactionMapper.update(user);
            Integer leaveTimes = 3 - failTimes;
            String msg = "登录失败，但还有" + leaveTimes + "次机会";
            throw new PasswordErrorException(msg);
        }

        return user;
    }

    /**
     * 用户注册功能的实现
     *
     * @param userRegisterDTO//这个注解是为SQL语句中参数赋值而服务的
     */
    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        Integer currentRole = BaseContext.getCurrentRole();
        //校验用户权限，只有管理员用户才能注册用户
        if (currentRole != 1) {
            throw new AuthorityLimitException(MessageConstant.AUTHORITY_LIMIT);
        }
        /*获取请求参数
          这里需要从请求参数中拿到deadlineTime参数的值，它是一个字符串，比如"2023-15-10"，没有时分秒，需要把它转化为unix时间戳
          同样对于loginStart参数和loginEnd参数，也需要转换为unix时间戳,他们的格式是 "8:00" "20:00"这种
        */
        String deadlineTimeStr = userRegisterDTO.getDeadlineTime();
        String loginStartStr = userRegisterDTO.getLoginStart();
        String loginEndStr = userRegisterDTO.getLoginEnd();
        //定义相应的日期格式转换对象
        DateFormat deadlineTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat loginTimeFormat = new SimpleDateFormat("HH:mm");

        Date deadlineTime;
        Date loginStartTime;
        Date loginEndTime;
        //这里捕获日期格式转换异常，并将业务异常抛给handler处理
        try {
            deadlineTime = deadlineTimeFormat.parse(deadlineTimeStr);
            loginStartTime = loginTimeFormat.parse(loginStartStr);
            loginEndTime = loginTimeFormat.parse(loginEndStr);
        } catch (ParseException e) {
            throw new FormatException(MessageConstant.FORMAT_ERROR);
        }
        //将日期对象转换为相应的时间戳，方便进行存储
        if (deadlineTime != null && loginStartTime != null && loginEndTime != null) {
            //getTime()方法获取的是自1970年1月1日以来的毫秒数 deadline是秒数 loginStart和lgoinEnd是纳秒数
            Long deadline = deadlineTime.getTime() / TimeConstant.MS_BETWEEN_S;
            Long loginStart = (loginStartTime.getHours() * TimeConstant.TIME_HOUR + loginStartTime.getMinutes() * TimeConstant.TIME_MINUTE) * TimeConstant.NS_BETWEEN_S;
            Long loginEnd = (loginEndTime.getHours() * TimeConstant.TIME_HOUR + loginEndTime.getMinutes() * TimeConstant.TIME_MINUTE) * TimeConstant.NS_BETWEEN_S;
            //链式构造用户对象
            User user = User.builder()
                    .loginStart(loginStart)
                    //loginEnd为0说明填入的时间为"24:00"
                    .loginEnd(loginEnd == 0 ? (TimeConstant.TIME_DAY * TimeConstant.NS_BETWEEN_S) : loginEnd)
                    .deadlineTime(deadline)
                    .name(userRegisterDTO.getName())
                    .passwd(userRegisterDTO.getPasswd())
                    .role(userRegisterDTO.getRole())
                    .build();
            //调用mapper接口，存入用户信息，在异常处理器中处理可能发生的数据库异常
            userTransactionMapper.insert(user);
        }
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @Override
    public String[] getUser() {
        //调用数据层查询所有用户数据
        List<User> users = userTransactionMapper.list();
        //根据用户数量创建用户信息表UserInfo
        String[] userInfo = new String[users.size()];
        //判断查询数据是否为空
        if (users != null && users.size() > 0) {
            //遍历用户，得到每一个用户数据并进行处理
            for (int i = 0; i < users.size(); i++) {
                //将用户角色转为字符串，1为管理员，2为普通用户
                String role = users.get(i).getRole() == 1 ? "管理员" : "普通用户";
                //把loginStart的时间（时间长度量，比如8:00被表示为一个8*TimeConstant.TIME_HOUR*TimeConstant.NS_BETWEEN_S的时间长度量），将这种表示方法转化为时分格式的字符串
                String loginStartStr = String.format("%02d:%02d", users.get(i).getLoginStart() / TimeConstant.NS_BETWEEN_S / TimeConstant.TIME_HOUR, users.get(i).getLoginStart() / TimeConstant.NS_BETWEEN_S % TimeConstant.TIME_HOUR / TimeConstant.TIME_MINUTE);
                String loginEndStr = String.format("%02d:%02d", users.get(i).getLoginEnd() / TimeConstant.NS_BETWEEN_S / TimeConstant.TIME_HOUR, users.get(i).getLoginEnd() / TimeConstant.NS_BETWEEN_S % TimeConstant.TIME_HOUR / TimeConstant.TIME_MINUTE);
                //把deadlineTime的时间标准格式化为"2025-04-19"的格式
                Date deadlineTime = new Date(users.get(i).getDeadlineTime() * TimeConstant.MS_BETWEEN_S > 0 ? users.get(i).getDeadlineTime() * TimeConstant.MS_BETWEEN_S : users.get(i).getDeadlineTime());
                // 创建SimpleDateFormat对象来定义日期格式
                SimpleDateFormat deadlineSdf = new SimpleDateFormat("yyyy-MM-dd");
                // 使用SimpleDateFormat对象将Date对象格式化为指定的日期字符串
                String DeadlineStr = deadlineSdf.format(deadlineTime);
                //把releaseTime的时间标准格式化为"1970-01-01 08:00:00"的格式
                Date releaseTime = new Date(users.get(i).getReleaseTime() * TimeConstant.MS_BETWEEN_S);
                SimpleDateFormat releaseSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String releaseStr = releaseSdf.format(releaseTime);
                //构造用户信息字符串
                userInfo[i] = users.get(i).getName() + "," + role + "," + loginStartStr + "," + loginEndStr + "," + DeadlineStr + "," + releaseStr;
            }
            return userInfo;
        }
        //无用户数据，返回空UserInfo
        return new String[0];
    }

    /**
     * 用户信息修改
     *
     * @param userInfoChangeDTO
     */
    @Override
    public void userInfoChange(UserInfoChangeDTO userInfoChangeDTO) {
        //先从BaseContext中得到身份信息，校验身份是否是管理员
        Integer currentRole = BaseContext.getCurrentRole();
        if (currentRole != 1) {
            throw new AuthorityLimitException(MessageConstant.AUTHORITY_LIMIT);
        }
        /*
         * 从请求体中获取请求参数
         *这里需要从请求参数中拿到deadlineTime参数的值，它是一个字符串，比如"2023-15-10"，没有时分秒，需要把它转化为unix时间戳
         *还有loginStart和loginEnd，它们是一个字符串，比如"8:00"，需要把它转化为一个类时间戳的量，表示为一个时间长度，比如8*TimeConstant.TIME_HOUR
         *还有releaseTime，它是一个字符串，比如"2023-15-10 8:00:00"，需要把它转化为unix时间戳
         */
        String deadlineTimeStr = userInfoChangeDTO.getDeadlineTime();
        String releaseTimeStr = userInfoChangeDTO.getReleaseTime();
        String loginStartStr = userInfoChangeDTO.getLoginStart();
        String loginEndStr = userInfoChangeDTO.getLoginEnd();
        //定义相应的日期格式转换对象
        SimpleDateFormat releaseTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat deadlineTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat loginTimeFormat = new SimpleDateFormat("kk:mm");

        Date releaseTime;
        Date deadlineTime;
        Date loginStartTime;
        Date loginEndTime;
        //这里捕获日期格式转换异常，并将业务异常抛给handler处理
        try {
            deadlineTime = deadlineTimeFormat.parse(deadlineTimeStr);
            releaseTime = releaseTimeFormat.parse(releaseTimeStr);
            loginStartTime = loginTimeFormat.parse(loginStartStr);
            loginEndTime = loginTimeFormat.parse(loginEndStr);
        } catch (ParseException e) {
            throw new FormatException(MessageConstant.FORMAT_ERROR);
        }
        if (releaseTime != null && deadlineTime != null && loginStartTime != null && loginEndTime != null) {
            //将日期对象转换为相应的时间戳，方便进行存储
            Long release = releaseTime.getTime() / TimeConstant.MS_BETWEEN_S;
            Long deadline = deadlineTime.getTime() / TimeConstant.MS_BETWEEN_S;
            Long loginStart = (loginStartTime.getHours() * TimeConstant.TIME_HOUR + loginStartTime.getMinutes() * TimeConstant.TIME_MINUTE) * TimeConstant.NS_BETWEEN_S;
            Long loginEnd = (loginEndTime.getHours() * TimeConstant.TIME_HOUR + loginEndTime.getMinutes() * TimeConstant.TIME_MINUTE) * TimeConstant.NS_BETWEEN_S;
            //判断用户名是否存在
            User userByName = userTransactionMapper.getByName(userInfoChangeDTO.getName());
            if(userByName == null) {
                throw new UserNameNotFoundException(MessageConstant.USERNAME_NOT_EXIST);
            }
            //构造用户对象，调用mapper层接口进行更新用户信息操作
            User user = User.builder()
                    .id(userByName.getId())
                    .deadlineTime(deadline)
                    .releaseTime(release)
                    .loginStart(loginStart)
                    .loginEnd(loginEnd)
                    .build();
            //判断密码是否为空
            if(userInfoChangeDTO.getPasswd() != null && userInfoChangeDTO.getPasswd() != "") {
                user.setPasswd(userInfoChangeDTO.getPasswd());
            }
            userTransactionMapper.update(user);
        }
    }

    /**
     * 根据用户名删除用户信息
     * @param userDeleteDTO
     */
    @Override
    public void delete(UserDeleteDTO userDeleteDTO) {
        //先从BaseContext中得到身份信息，校验身份是否是管理员
        Integer currentRole = BaseContext.getCurrentRole();
        if (currentRole != 1) {
            throw new AuthorityLimitException(MessageConstant.AUTHORITY_LIMIT);
        }
        //判断请求数据解析是否异常
        String[] username = userDeleteDTO.getUsername();
        if (username == null) {
            throw new FormatException(MessageConstant.FORMAT_ERROR);
        }
        //遍历用户名字符串数组得到每一个用户名字符串，并进行删除
        for (String name : username) {
            //同样，判断请求数据是否异常
            if(name == null || name == "") {
                throw new FormatException(MessageConstant.FORMAT_ERROR);
            }

            //调用mapper层接口进行数据的删除
            userTransactionMapper.deleteByName(name);

        }

    }
}
