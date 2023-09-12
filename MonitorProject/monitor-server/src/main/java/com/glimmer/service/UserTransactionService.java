package com.glimmer.service;

import com.glimmer.dto.UserDeleteDTO;
import com.glimmer.dto.UserInfoChangeDTO;
import com.glimmer.dto.UserLoginDTO;
import com.glimmer.dto.UserRegisterDTO;
import com.glimmer.entity.User;

//用户service层接口,在这里定义相关的业务接口
public interface UserTransactionService {
    /**
     * 用户登录接口
     * @param userLoginDTO
     * @return
     */
    User login(UserLoginDTO userLoginDTO);

    /**
     * 用户注册接口
     * @param userRegisterDTO
     */
    void register(UserRegisterDTO userRegisterDTO);

    /**
     * 获取用户信息
     * @return
     */
    String[] getUser();

    /**
     * 修改用户信息
     * @param userInfoChangeDTO
     */
    void userInfoChange(UserInfoChangeDTO userInfoChangeDTO);

    /**
     * 根据用户名删除用户
     * @param userDeleteDTO
     */
    void delete(UserDeleteDTO userDeleteDTO);
}
