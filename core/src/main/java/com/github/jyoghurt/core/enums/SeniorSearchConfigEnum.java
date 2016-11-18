package com.github.jyoghurt.core.enums;


public enum SeniorSearchConfigEnum {

    /**
     * 联合查询配置
     */
    JOIN_SEARCH,

    /**
     * 时间范围查询配置
     */
    DATE_BETWEEN_SEARCH,

    /**
     * 其他类型字段查询配置，注：如果字段已经拼在JOIN_SEARCH或者DATE_BETWEEN_SEARCH中时，不会在OTHER_SEARCH中再次出现
     */
    OTHER_SEARCH
}
