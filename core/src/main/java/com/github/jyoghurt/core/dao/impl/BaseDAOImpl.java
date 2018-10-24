package com.github.jyoghurt.core.dao.impl;

import com.github.jyoghurt.core.dao.BaseDAO;
import com.github.jyoghurt.core.exception.DaoException;
import com.github.jyoghurt.core.po.BasePO;
import org.apache.commons.beanutils.BeanUtils;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.github.jyoghurt.core.po.BasePO.DELETE_FLAG;


public abstract class BaseDAOImpl<T extends BasePO, R extends UpdatableRecord<R>> implements BaseDAO<T> {

    @Autowired
    private DSLContext dsl;

    public abstract Table<R> getTable();

    public abstract TableField getId();

    public abstract Class<T> getType();

    @Override
    @SuppressWarnings("unchecked")
    public void save(T entity) throws DaoException {
        List<Object> list = new ArrayList<>();
        for(Field field:getTable().fields()){
            list.add(com.github.jyoghurt.core.utils.BeanUtils.getProperty(entity,field.getName()));
        }
        Object id =  dsl.insertInto(getTable()).columns(getTable().fields()).values(list).returning(getId()).fetchOne().get(getId());
        try {
            BeanUtils.setProperty(entity,getId().getName(),id);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }


    @Override
    public void update(T entity) {
        dsl.executeUpdate(getRecord(entity.setDeleted(false).setModifiedDateTime(LocalDateTime.now())));
    }

    @Override
    public void delete(T entity) {
        dsl.executeDelete(getRecord(entity));
    }

    @SuppressWarnings("unchecked")
    public void logicDelete(Serializable id) throws DaoException {
        try {
            dsl.update(getTable()).set((Field<Boolean>) getTable().field(DELETE_FLAG),true).where(getId().eq(id)).execute();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    private R getRecord(BasePO entity) {
        return dsl.newRecord(getTable(), entity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T findById(Serializable id) {
        return dsl.selectFrom(getTable()).where(getId().equal(id)).fetchOneInto(getType());
    }


}
