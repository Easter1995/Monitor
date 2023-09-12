package com.glimmer.exception;

/**
 * 业务异常，所有的业务异常都继承于这个父类
 */
public class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}
