package com.github.yogurt.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Project: 标注历史版本字段，并指定历史表名称
 * @Package: com.github.yogurt.core.annotations
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-12-19 14:37
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface SnapshotEntity {
    Class hisEntity();
}
