package com.github.jyoghurt.dataDict.util;

/**
 * Created by limiao on 2016/2/4.
 */
public class NumberUtils {

    /**
     * Integer值为null时转换为0
     *
     * @param i Integer类型数字
     * @return Integer
     */
    public static Integer nullToZero(Integer i) {
        if (i == null) {
            i = 0;
        }
        return i;
    }
}
