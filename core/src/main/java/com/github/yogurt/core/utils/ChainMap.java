package com.github.yogurt.core.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by jtwu on 2015/1/4.
 * 链表式map实现
 * 主要功能是为开发方便，put之后返回对象为map本身，可以继续put
 * eg：map.chainPut("a",a).chainPut("b",b)
 */
public class ChainMap<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = -8256232046919043447L;

    /**
     * 链表式赋值，返回值为map对象本身
     *
     * @param key   键
     * @param value 值
     * @return 返回map本身
     */
    public ChainMap<K, V> chainPut(K key, V value) {
        super.put(key, value);
        return this;
    }

    public ChainMap<K,V> chainPutAll(Map map){
        if(map!=null) {
            super.putAll(map);
        }
        return this;
    }


}
