package com.github.jyoghurt.core.utils;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.util.*;

/**
 * list工具类
 * Created by 刘少年 on 2017/6/9.
 */
public final class Lists {
    /**
     * 对比两个list集合对象
     * @param leftList
     * @param rightList
     * @param <T>
     * @return
     */
    public static <T> ListDifference<T> difference(List<? extends T> leftList, List<? extends T> rightList) {
        ListDifference<T> listDifference = new ListDifference<>();
        Map<T, T> leftMap = new LinkedHashMap<>();
        Map<T, T> rightMap = new LinkedHashMap<>();
        for (T t : leftList) {
            leftMap.put(t, t);
        }
        for (T t : rightList) {
            rightMap.put(t, t);
        }
        MapDifference<T, T> mapDifference = Maps.difference(leftMap, rightMap);
        listDifference.onlyOnLeft.addAll(mapDifference.entriesOnlyOnLeft().keySet());
        listDifference.onlyOnRight.addAll(mapDifference.entriesOnlyOnRight().keySet());
        listDifference.onBoth.addAll(mapDifference.entriesInCommon().keySet());
        return listDifference;
    }

}
