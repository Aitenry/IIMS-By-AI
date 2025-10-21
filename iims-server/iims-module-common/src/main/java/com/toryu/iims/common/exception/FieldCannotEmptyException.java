package com.toryu.iims.common.exception;

/**
 * 字段不能为空异常
 */
public class FieldCannotEmptyException extends BaseException {

    public FieldCannotEmptyException(String msg) {
        super(msg);
    }

}
