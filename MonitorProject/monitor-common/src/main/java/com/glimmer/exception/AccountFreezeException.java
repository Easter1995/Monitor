package com.glimmer.exception;

/**
 * 账号被冻结异常
 */
public class AccountFreezeException extends BaseException {

    public AccountFreezeException() {
    }

    public AccountFreezeException(String msg) {
        super(msg);
    }

}
