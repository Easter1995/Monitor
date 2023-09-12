package com.glimmer.exception;

/**
 * 账户权限不足异常
 */
public class AuthorityLimitException extends BaseException {

    public AuthorityLimitException() {
    }

    public AuthorityLimitException(String msg) {
        super(msg);
    }

}
