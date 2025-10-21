package com.toryu.iims.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    /**
     * 判断输入的邮箱格式是否正确
     * @param email 输入的邮箱地址
     * @return 返回邮箱地址是否正确
     */
    public static boolean isMail(String email) {
        boolean flag = false;
        String regEx = "^[a-zA-Z\\d._%+-]+@[a-zA-Z\\d.-]+\\.[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx);
        m = p.matcher(email);
        if(m.matches())
            flag = true;
        else
            System.out.println("输入邮箱格式错误......");
        return flag;
    }

    /**
     * 验证是否是正确合法的手机号码
     *
     * @param telephone 手机号码
     * @return 合法返回true，不合法返回false
     */
    public static boolean isCellPhoneNo(String telephone) {
        if (telephone.length() != 11) {
            return false;
        }
        Pattern pattern = Pattern.compile("^1[3-9]\\d{9}$");
        Matcher matcher = pattern.matcher(telephone);
        return matcher.matches();
    }

    /**
     * 判断是否为有效身份证号
     * @param idNumber 身份证号
     * @return boolean
     */
    public static boolean isCheckIdNumber(String idNumber) {
        String pattern;

        if(idNumber.length() == 15)
            pattern="^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$";
        else if(idNumber.length() == 18)
            pattern="^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[\\dXx]$";
        else
            return false;

        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(idNumber);
        return matcher.find();
    }

}
