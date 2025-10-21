package com.toryu.iims.common.constant;

/**
 * 密码常量
 */
public class PasswordConstant {

    private static final String DEFAULT_PASSWORD = "@iims.com";

    public static String resetPassword(String idNumber) {
        return String.format("%s%s", idNumber.substring(idNumber.length() - 6), DEFAULT_PASSWORD);
    }

}
