package com.toryu.iims.ai.rag.enums;

import lombok.Getter;

@Getter
public enum ModelTypeEnum {
    OPENAI(0),
    OLLAMA(1);

    private final Integer code;

    ModelTypeEnum(Integer code) {
        this.code = code;
    }

    public static ModelTypeEnum fromCode(Integer code) {
        for (ModelTypeEnum type : ModelTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown model type: " + code);
    }
}
