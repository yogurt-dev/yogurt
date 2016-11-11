package com.github.jyoghurt.core.utils;

/**
 * Created by limiao on 2016/11/11.
 */
public class StringUtils extends org.springframework.util.StringUtils {

    /**
     * 将null转成空字符串.
     *
     * @param str 字符串
     * @return String
     */
    public static String nullToEmpty(String str) {
        if (str == null) {
            str = "";
        }
        return str;
    }

    /**
     * 替换字符串，匹配的都replace成空字符串.
     *
     * @param str      需要替换的源字符串
     * @param replaces 需要replace的字符串
     * @return String
     */
    public static String replaceAllToEmpty(String str, String... replaces) {
        str = StringUtils.nullToEmpty(str);
        for (String s : replaces) {
            if (s == null) {
                continue;
            }
            str = str.replaceAll(s, "");
        }
        return str;
    }
}
