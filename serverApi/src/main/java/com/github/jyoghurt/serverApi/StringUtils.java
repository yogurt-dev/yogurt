package com.github.jyoghurt.serverApi;

/**
 * Created by jtwu on 2016/3/31.
 */
public class StringUtils {
    /**
     * 去除首位/n/t空格
     *
     * @param str 字符串
     * @return 过滤后的字符串
     */
    public static String trim(String str) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(str)) {
            return str;
        }
        while (str.startsWith(" ")) {
            str = str.substring(1, str.length()).trim();
        }
        while (str.endsWith(" ")) {
            str = str.substring(0, str.length() - 1).trim();
        }
        return str;
    }
}
