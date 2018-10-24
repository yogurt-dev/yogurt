package com.github.jyoghurt.core.dao;


import com.github.jyoghurt.core.exception.DaoException;
import com.github.jyoghurt.core.po.BasePO;

import java.io.Serializable;

public interface BaseDAO<T extends BasePO> {

    void save(T entity) throws DaoException;

    void update(T entity);

    void delete(T entity);

    void logicDelete(Serializable id) throws DaoException;

     T findById(Serializable id);




}
