package com.toryu.dms.archive.enums;

import java.util.Objects;

public enum ArchivalLevelEnum {

    TOP_SECRET("1", "绝密"),
    CONFIDENTIAL("2", "机密"),
    SECRET("3", "秘密");

    private final String key;
    private final String info;

    ArchivalLevelEnum(String key, String info) {
        this.key = key;
        this.info = info;
    }

    public String getKey() {
        return key;
    }

    public String getInfo() {
        return info;
    }

    public static ArchivalLevelEnum fromKey(String key) {
        for (ArchivalLevelEnum level : values()) {
            if (Objects.equals(level.getKey(), key)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid key: " + key);
    }

}
