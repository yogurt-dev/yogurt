package com.github.jyoghurt.core.dao.impl;

import org.jooq.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public abstract class PageHandle<T> {
    private DSLContext dsl;
    private Pageable pageable;
    private Class<T> type;

    protected PageHandle(DSLContext dsl, Pageable pageable, Class<T> type) {
        this.dsl = dsl;
        this.pageable = pageable;
        this.type = type;
    }

    public abstract TableField[] fields();

    public abstract SelectConditionStep<? extends Record> sql(SelectSelectStep<? extends Record> query);

    public Page<T> fetch() {
        int total = sql(dsl.selectCount()).fetchOneInto(int.class);
        int pageNumber = pageable.getPageNumber()>1? pageable.getPageNumber() - 1:0;
        List<T> list = sql(dsl.select(fields()==null?new TableField[] {}:fields()))
                .limit(pageable.getPageSize())
                .offset(pageNumber * pageable.getPageSize()).fetchInto(type);
        return new PageImpl<>(list, pageable, total);
    }
}