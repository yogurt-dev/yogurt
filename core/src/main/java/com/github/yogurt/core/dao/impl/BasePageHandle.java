package com.github.yogurt.core.dao.impl;

import org.jooq.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;


/**
 * 分页功能实现
 *
 * 分页sql分以下两部分
 * 查询结果集：select id, name from Test where ....
 * 查询记录数：select count(*) from Test where ....
 *
 * 两句sql只有返回列不同，所以拼装思路就是提供如下两个方法：
 *
 * fields(): 返回“查询列”的列表
 * beginWithFormSql() :此方法中继续编写from之后的sql
 *
 * @author Administrator
 */
public abstract class BasePageHandle<T> {
    private DSLContext dsl;
    private Pageable pageable;
    private Class<T> type;

    protected BasePageHandle(DSLContext dsl, Pageable pageable, Class<T> type) {
        this.dsl = dsl;
        this.pageable = pageable;
        this.type = type;
    }

    /**
     * sql查询的列集合
     *
     * @return sql查询的列集合
     */
    public abstract Field[] fields();

    /**
     * form及之后的sql
     *
     * @param selectColumns 查询语句的select部分
     * @return 完整的sql
     */
    public abstract SelectConditionStep<? extends Record> beginWithFormSql(SelectSelectStep selectColumns);

    public Page<T> fetch() {
        int total = beginWithFormSql(dsl.selectCount()).fetchOneInto(int.class);
        int pageNumber = pageable.getPageNumber() > 1 ? pageable.getPageNumber() - 1 : 0;
        List<T> list;
        if (total != 0) {
            list = beginWithFormSql(dsl.select(fields() == null ? new TableField[]{} : fields()))
                    .limit(pageable.getPageSize())
                    .offset(pageNumber * pageable.getPageSize()).fetchInto(type);
        } else {
            list = new ArrayList<>();
        }
        return new PageImpl<>(list, pageable, total);
    }
}