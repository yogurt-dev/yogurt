package com.github.jyoghurt.wechatbasic.common.util;

import java.util.ResourceBundle;


public class PropUtil {
    private static ResourceBundle res;

    static {
        res = ResourceBundle.getBundle("environment-config");
    }

    public static String prop(String key) {
        return res.getString(key);
    }

}
