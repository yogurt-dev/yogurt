package com.github.jyoghurt.core.dao.impl;

import com.github.jyoghurt.core.dao.BaseDAO;
import com.github.jyoghurt.core.po.BaseEntity;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;


public abstract class BaseDAOImpl<T extends BaseEntity, R extends UpdatableRecord<R>> implements BaseDAO<T> {

    @Autowired
    private DSLContext dsl;

    public abstract Table<R> getTable();

    public abstract TableField<R, ? extends Serializable> getId();

    public abstract Class<T> getType();

    @Override
    public void save(T entity) {
        (getRecord(entity)).insert();
    }

    @Override
    public void update(T entity) {
        dsl.executeUpdate(getRecord(entity));
    }

    @Override
    public void delete(T entity) {
        dsl.executeDelete(getRecord(entity));
    }

    private R getRecord(BaseEntity entity) {
        return dsl.newRecord(getTable(), entity);
    }

    @Override
    public <F extends Serializable > T findById(F id) {
        return dsl.selectFrom(getTable()).where(getId().equal(id)).fetchOneInto(getType());
    }


//
//    @Override
//    public Long pageTotalRecord(Class entityClass, Map data) {
//        return null;
//    }
//
//    @Override
//    public List<T> pageData(Class entityClass, Map data) {
//        return null;
//    }
}
