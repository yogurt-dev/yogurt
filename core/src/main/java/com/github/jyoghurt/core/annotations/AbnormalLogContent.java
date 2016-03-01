package com.github.jyoghurt.core.annotations;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.github.jyoghurt.core.annotations
 * @Description:监控异常访问
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-02-23 10:38
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AbnormalLogContent{
    String value() default StringUtils.EMPTY;
}
