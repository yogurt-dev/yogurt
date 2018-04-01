package com.github.jyoghurt.core.annotationResolver;


import java.lang.reflect.Field;

/**
 * Created by Administrator on 2015/6/3.
 * 注解解析器接口，用于实体转换时，通过根据注解转换属性值。
 * 每个注解有且仅有一个注解转换器
 */
public interface BaseResolver {
    /**
     * 解析注解，并进行数据转换。
     *
     * @param object 业务实体
     * @param field  属性
     */
    void resolve(Object object, Field field) ;
}
