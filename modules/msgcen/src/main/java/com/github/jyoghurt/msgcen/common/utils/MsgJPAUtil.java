package com.github.jyoghurt.msgcen.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * user:zjl
 * date: 2016/11/22.
 */
public class MsgJPAUtil {
    private static String toUpperCaseFirstOne(String var) {
        if (Character.isUpperCase(var.charAt(0)))
            return var;
        else
            return (new StringBuilder()).append(Character.toUpperCase(var.charAt(0))).append(var.substring(1)).toString();
    }

    public static void setValue(Object source, String fieldName, Object value) {
        Class tClass = source.getClass();
        //获得set方法
        Method method = null;
        try {
            method = tClass.getMethod("set"+toUpperCaseFirstOne(fieldName), String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            method.invoke(source, value.toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

