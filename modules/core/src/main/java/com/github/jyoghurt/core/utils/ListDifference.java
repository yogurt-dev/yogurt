package com.github.jyoghurt.core.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 对比两个list的差异结果对象
 * Created by 刘少年 on 2017/6/9.
 */
public class ListDifference<T> {
    final List<T> onlyOnLeft = new ArrayList<>();
    final List<T> onlyOnRight = new ArrayList<>();
    final List<T> onBoth = new ArrayList<>();

    /**
     * 所有对象均相同
     */
    boolean areEqual(){
        return CollectionUtils.isEmpty(onlyOnLeft) && CollectionUtils.isEmpty(onlyOnRight);
    }

    /**
     * 只左边存在的对象集合
     */
    List<T> entriesOnlyOnLeft(){
        return onlyOnLeft;
    }

    /**
     * 只右边存在的对象集合
     */
    List<T> entriesOnlyOnRight(){
        return onlyOnRight;
    }

    /**
     * 两边相同的对象集合
     */
    List<T> entriesInCommon(){
        return onBoth;
    }


    @Override
    public String toString() {
        if (areEqual()) {
            return "equal";
        }
        StringBuilder result = new StringBuilder("not equal");
        if (!onlyOnLeft.isEmpty()) {
            result.append(": only on left=").append(onlyOnLeft);
        }
        if (!onlyOnRight.isEmpty()) {
            result.append(": only on right=").append(onlyOnRight);
        }
        return result.toString();
    }
}
