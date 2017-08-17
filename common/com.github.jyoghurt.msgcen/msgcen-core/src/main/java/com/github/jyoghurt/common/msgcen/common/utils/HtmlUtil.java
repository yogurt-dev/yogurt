package com.github.jyoghurt.common.msgcen.common.utils;

/**
 * user:zjl
 * date: 2016/11/22.
 */
public class HtmlUtil {

    public static Object convertEmpty(Object value) {
        if (null == value) {
            return "null";
        }
        if ("" == value || "".equals(value)) {
            return "null";
        }
        return value;
    }
}
