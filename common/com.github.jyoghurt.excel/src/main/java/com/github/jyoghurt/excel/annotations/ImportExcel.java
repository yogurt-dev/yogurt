package com.github.jyoghurt.excel.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ImportExcel {

    int order();

    String dateFormat() default "yyyy-MM-dd";

    String javaScriptBody() default "";

}