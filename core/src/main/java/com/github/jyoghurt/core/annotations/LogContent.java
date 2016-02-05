package com.github.jyoghurt.core.annotations;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA. User: jietianwu Date: 13-9-6 Time: 下午3:54
 * 操作日志内容，配在每个需要记录操作的controller方法或类上，
 * 类上定义该注解的目的是共享module值，方法中可不再定义module，如果定义了，优先级高于类定义
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface LogContent {
    //模块名称,方法上定义优先于类上定义
    String module() default StringUtils.EMPTY;

    //日志内容
    String value() default StringUtils.EMPTY;

}
