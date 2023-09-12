package com.glimmer.exception;

/**
 * 各种数据已存在异常
 */
public class ExistException extends BaseException {
    public ExistException() {
    }

    public ExistException(String msg) {
        super(msg);
    }

}
