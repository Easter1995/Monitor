package com.glimmer.controller.interactFront;

import com.glimmer.constant.*;
import com.glimmer.dto.UserDeleteDTO;
import com.glimmer.dto.UserInfoChangeDTO;
import com.glimmer.dto.UserLoginDTO;
import com.glimmer.dto.UserRegisterDTO;
import com.glimmer.entity.User;
import com.glimmer.properties.JwtProperties;
import com.glimmer.result.Result;
import com.glimmer.service.UserTransactionService;
import com.glimmer.utils.JwtUtil;
import com.glimmer.vo.StatusVO;
import com.glimmer.vo.UserGetVO;
import com.glimmer.vo.UserLoginVO;
import com.glimmer.webSocket.AlertWebsocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * UserTransactionController 控制层
 *用户相关的交互
 * 	包括：
 * 		登录	UserLogin
 * 		注册	UserRegister
 * 		用户获取 UserGet 返回当前系统的所有用户信息。
 * 		用户修改 UserInfoChange 传入想要修改的用户信息，返回修改成功与否。
 * 		用户删除 UserDelete 输入想要删除的用户名称，返回删除与否的消息。
 */
@RestController//处理HTTP请求
@RequestMapping("/usr")//默认为GET方法的路由/usr
@Slf4j//使用log日志的功能
public class UserTransactionController {
    /**
     * 依赖注入UserTransactionService，可用于调用其相关方法
     */
    @Autowired//根据属性类型，spring自动将匹配到的属性值进行注入
    private UserTransactionService userTransactionService;

    /**
     * 依赖注入AlertWebsocketServer，可用于建立长连接
     */
    @Autowired
    private AlertWebsocketServer alertWebsocketServer;

    /**
     * 依赖注入JwtProperties，可用于后续读取相关配置
     */
    @Autowired
    private JwtProperties jwtProperties;

/*
UserLogin 登录接口

	输入：用户名和密码
	输出：登录成功与否

效果详细描述：

 1. 从前端获取用户名和密码：json格式为：
    {
    "username": "liamY",
    "passwd": "123456"
    }

 2. 从数据库中查询是否存在该用户，如果存在，返回登录成功，否则返回登录失败。返回json格式为：

    {
    "code": 200,
    "message": "success",
    "data":
    {
    "message": "success",
    "status": 0,
    "authorization": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJIdWQiOjEsIlJvbGUiOjAsImV4cCI6MTYxMjUwNjQyMCwiaWF0"
    }
    }

这里status的含义为：任务状态  0：登录成功  1：登录失败，但还有机会尝试  2：登录失败超过3次，账户冻结  9：发生错误

3.对于失败用户，要记录失败次数，更新数据库中用户表对应的记录次数，超过三次后有一个冻结操作，冻结时间为一天。后续管理员用户可以修改这个时间
*/
    @PostMapping//@PostMapping是一个组合注解，是@RequestMapping(method = RequestMethod.POST)的缩写
    //@RequestBody主要是用来接收前端传递给后端的json字符串中的数据的(请求体中的数据的)
    public Result<UserLoginVO> UserLogin(@RequestBody UserLoginDTO userLoginDTO){
        //在这里输出日志，便于后续排查错误，解决问题
        log.info("用户登录:{}",userLoginDTO);
        //调用service层的接口，返回查询到的user对象
        User user = userTransactionService.login(userLoginDTO);

        //登录成功后，为用户生成jwt令牌
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        claims.put(JwtClaimsConstant.USER_ROLE,user.getRole());
        String token = JwtUtil.createJWT(jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims);

        //链式构造可供返回给前端的视图模型对象,为其设置相关属性
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .message(MessageConstant.LOGIN_SUCCESS)
                .status(LoginStatusConstant.LOGIN_SUCCESS)
                .authorization(token)
                .build();

        //连接WebSocketServer


        //返回响应结果
        return Result.success(userLoginVO,MessageConstant.LOGIN_SUCCESS);
    }
/*
UserRegister 注册接口，这个接口只能是管理员用户使用
关于请求参数介绍：
| 参数名           | 数据类型 | 说明                                                   |
| ---------------- | -------- | ------------------------------------------------------ |
| username         | string   | 用户名字段用来查找密码以及冻结期等字段                 |
| passwd            | string   | 密码字段采用SHA256加密的字符串，用于匹配用户名         |
| role             | int      | 用户角色，用于区分用户权限，1为管理员用户，2为普通用户 |
| deadlineTime    | string   | 有效期字段，用于限制用户的使用时长                     |
| loginStart | string   | 开始登录时间，该字段用于限定该用户每日开始登录的时间   |
| loginEnd   | string   | 结束登录时间，该字段用于限定该用户每日结束登录的时间   |
对于返回参数
| 参数名  | 数据类型 | 说明                                                         |
| ------- | -------- | ------------------------------------------------------------ |
| status  | int      | 任务状态  0：注册成功  1：注册失败，详细原因在message字段描述  9：发生错误，详细错误在message字段描述 |
| message | String   | 状态描述                                                     |
*/
    @PostMapping("/register")
    public Result<StatusVO> UserRegister(@RequestBody UserRegisterDTO userRegisterDTO){
        //在这里输出日志，便于后续排查错误，解决问题
        log.info("实现用户注册：{}",userRegisterDTO);
        //调用Service层的注册接口
        userTransactionService.register(userRegisterDTO);
        //构建注册的返回数据模型对象statusVO
        StatusVO statusVO = StatusVO.builder()
                .message(MessageConstant.REGISTER_SUCCESS)
                .status(RegisterStatusConstant.REGISTER_SUCCESS)
                .build();
        //将模型对象成功返回
        return Result.success(statusVO,MessageConstant.REGISTER_SUCCESS);
    }

/*
	UserGet 获取用户信息,返回当前系统的所有用户信息，管理员用户使用
	没有请求体
	返回值：
		对于dataResponse部分，是有status int，message string，userInfo []string 三个部分
		userInfo是一个字符串数组，每个字符串是一个用户的信息，格式为 "name,role,loginStart(为时分格式),loginEnd(为时分格式),deadlineTime(从Unix转为年月日格式),releaseTime(从Unix时间戳转为年月日格式)"
*/
    @GetMapping
    public Result<UserGetVO> UserGet() {
        //调用Service层的方法，返回userInfo的用户信息
        String[] userInfo = userTransactionService.getUser();
        log.info("返回用户数据:{}", userInfo);
        //链式构造返回给前端的视图模型UserGetVO
        UserGetVO userGetVO = UserGetVO.builder()
                .userInfo(userInfo)
                .status(GetStatusConstant.SUCCESS)
                .message(MessageConstant.USER_GET_SUCCESS)
                .build();
        //返回封装的用户数据
        return Result.success(userGetVO,MessageConstant.USER_GET_SUCCESS);
    }

/*
UserInfoChange 修改用户信息
这个功能只能由管理员使用
请求体：

	{
	    "name": "string",
	    "passwd": "string",
	    "role": 0,
	    "deadlineTime": "string",
	    "loginStart": "string",
	    "loginEnd": "string",
	    "releaseTime": "string"
	}

返回值：
在data字段中，有status int，message string两个字段
status为0表示修改成功，为1表示修改失败，9表示服务器内部错误
message为对status的解释
*/

    @PutMapping
    public Result<StatusVO> UserInfoChange(@RequestBody UserInfoChangeDTO userInfoChangeDTO) {
        log.info("用户信息修改：{}",userInfoChangeDTO);
        userTransactionService.userInfoChange(userInfoChangeDTO);
        StatusVO statusVO = StatusVO.builder()
                .status(ChangeStatusConstant.SUCCESS)
                .message(MessageConstant.CHANGE_SUCCESS)
                .build();
        return Result.success(statusVO,MessageConstant.CHANGE_SUCCESS);
    }

/*
UserDelete	删除用户
该功能需要管理员权限
请注意，使用 DELETE 方法删除资源时需要谨慎，因为一旦删除后就无法恢复。
另外，删除资源时应该遵循安全和幂等的原则，即相同的请求多次执行应该产生相同的结果，并且不会对系统状态产生影响。
| 请求参数 |          |                                                              |
| -------- | -------- | ------------------------------------------------------------ |
| 参数名   | 数据类型 | 说明                                                         |
| username | []string    | 用户名可以唯一确定用户，删除数据库数据  ，是一个名称的字符串数组 |
*/
    @DeleteMapping
    public Result<StatusVO> UserDelete(@RequestBody UserDeleteDTO userDeleteDTO) {
        log.info("删除用户，用户名：{}",userDeleteDTO);
        userTransactionService.delete(userDeleteDTO);
        StatusVO statusVO = StatusVO.builder()
                .status(DeleteStatusConstant.SUCCESS)
                .message(MessageConstant.DELETE_SUCCESS)
                .build();
        return Result.success(statusVO,MessageConstant.DELETE_SUCCESS);

    }



}
