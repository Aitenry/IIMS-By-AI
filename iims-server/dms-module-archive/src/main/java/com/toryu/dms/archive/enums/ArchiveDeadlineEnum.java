package com.toryu.dms.archive.enums;

import java.util.Objects;

public enum ArchiveDeadlineEnum {

    PERMANENT("999", "永久", "Y"),
    REGULARLY_FOR_TEN_YEARS("10", "定期10年", "D10"),
    REGULARLY_FOR_THIRTY_YEARS("30", "定期30年", "D30");

    private final String key;
    private final String info;
    private final String code;

    ArchiveDeadlineEnum(String key, String info, String code) {
        this.key = key;
        this.info = info;
        this.code = code;
    }

    public String getKey() {
        return key;
    }

    public String getInfo() {
        return info;
    }

    public String getCode() {
        return code;
    }

    public static ArchiveDeadlineEnum fromKey(String key) {
        for (ArchiveDeadlineEnum level : values()) {
            if (Objects.equals(level.getKey(), key)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid key: " + key);
    }

}
