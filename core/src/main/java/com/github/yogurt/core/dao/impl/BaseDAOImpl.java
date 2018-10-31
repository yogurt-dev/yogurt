package com.github.yogurt.core.dao.impl;

import com.github.yogurt.core.dao.BaseDAO;
import com.github.yogurt.core.exception.DaoException;
import com.github.yogurt.core.po.BasePO;
import com.github.yogurt.core.utils.JPAUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public abstract class BaseDAOImpl<T extends BasePO, R extends UpdatableRecord<R>> implements BaseDAO<T> {

    private static final String IS_DELETED = "is_deleted";
    private static final String MODIFIER_ID = "modifier_id";
    @Autowired
    protected DSLContext dsl;

    public abstract TableField getId();

    public abstract Class<T> getType();

    @SuppressWarnings("unchecked")
    private Table<R> getTable() {
        return getId().getTable();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void save(T po) throws DaoException {
        List<Object> list = new ArrayList<>();
        for (Field field : getTable().fields()) {
            list.add(JPAUtils.getValue(po, field.getName()));
        }
        Object id = dsl.insertInto(getTable()).columns(getTable().fields()).values(list).returning(getId()).fetchOne().get(getId());
        try {
            BeanUtils.setProperty(po, getId().getName(), id);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(T po) {
        dsl.executeUpdate(getRecord(po));
    }

    @SuppressWarnings("unchecked")
    public void logicDelete(Serializable id, Serializable userId) throws DaoException {
        try {
            dsl.update(getTable())
                    .set((Field<Boolean>) getTable().field(IS_DELETED), true)
                    .set((Field) getTable().field(MODIFIER_ID),userId)
                    .where(getId().eq(id)).execute();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    private R getRecord(BasePO po) {
        return dsl.newRecord(getTable(), po);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T findById(Serializable id) {
        return dsl.selectFrom(getTable()).where(getId().equal(id)).fetchOneInto(getType());
    }

    @Override
    public List<T> findAll() {
        return dsl.selectFrom(getTable()).fetchInto(getType());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<T> list(T po, Pageable pageable) {
        return new PageHandle<T>(dsl, pageable, getType()) {
            @Override
            public TableField[] fields() {
                List<TableField> list = new ArrayList<>();
                for (Field field : getTable().fields()) {
                    list.add((TableField) field);
                }
                return list.toArray(new TableField[0]);
            }

            @Override
            public SelectConditionStep<? extends Record> sql(SelectSelectStep query) {
                Map<String, Object> map = JPAUtils.getColumnNameValueMap(po);
                String sql = " ";
                List values = new ArrayList();
                for (String columnName : map.keySet()) {
                    Object property = map.get(columnName);
                    if (null == property) {
                        continue;
                    }
                    sql += StringUtils.join(columnName, "=? and ");
                    values.add(map.get(columnName));
                }
                if (sql.length() > 1) {
                    sql = StringUtils.removeEnd(sql, "and ");
                }
                if (sql.length() == 1) {
                    return query.from(getTable()).where();
                }
                return query.from(getTable()).where(sql, values.toArray());
            }
        }.fetch();
    }
}