package com.toryu.iims.common.enums;

public enum IntegralDictType {
    CATEGORY(0L),
    TAG(1L);

    private final Long dictId;

    IntegralDictType(Long dictId) {
        this.dictId = dictId;
    }

    public Long getDictId() {
        return dictId;
    }
}
