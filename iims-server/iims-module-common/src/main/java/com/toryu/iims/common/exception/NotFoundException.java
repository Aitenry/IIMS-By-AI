package com.toryu.iims.common.exception;

/**
 * 数据不存在异常
 */
public class NotFoundException extends BaseException {

    public NotFoundException() {
    }

    public NotFoundException(String msg) {
        super(msg);
    }

}
