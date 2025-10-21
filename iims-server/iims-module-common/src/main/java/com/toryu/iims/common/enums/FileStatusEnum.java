package com.toryu.iims.common.enums;

import lombok.Getter;

@Getter
public enum FileStatusEnum {

    DELETE(-1),
    NOT_USED(0),
    USED(1);

    private final Integer code;

    FileStatusEnum(Integer code) {
        this.code = code;
    }

}
