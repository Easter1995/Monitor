package com.glimmer.exception;

/**
 * 报警信息不存在异常
 */
public class AlertNotFoundException extends BaseException {

    public AlertNotFoundException() {
    }

    public AlertNotFoundException(String msg) {
        super(msg);
    }

}
