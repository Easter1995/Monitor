package com.glimmer.exception;

/**
 * 用户名不存在异常
 */
public class UserNameNotFoundException extends BaseException {

    public UserNameNotFoundException() {
    }

    public UserNameNotFoundException(String msg) {
        super(msg);
    }

}
