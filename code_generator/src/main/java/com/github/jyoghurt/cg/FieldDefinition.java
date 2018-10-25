package com.github.jyoghurt.cg;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class FieldDefinition  {
    /**
     * 数据库列名
     */
    private String columnName;
    /**
     * 数据库类型
     */
    private String columnType;
    /**
     * 字段名
     */
    private String codeName;

    /**
     * 字段类型名
     */
    private String className;

    /**
     * 字段类型名，包含包名
     */
    private String classFullName;
    /**
     * 注释
     */
    private String comment;
    /**
     * 是否为主键
     */
    private Boolean isPriKey;
    /**
     * 字段长度
     */
    private Integer columnLength;
    /**
     * 是否允许为空
     */
    private Boolean nullable;
    /**
     * 枚举类名
     */
    private String enumClassName;
    /**
     * 枚举值列表
     */
    private String[] enumValues;


}
