package com.github.jyoghurt.core.dao;


import com.github.jyoghurt.core.exception.DaoException;
import com.github.jyoghurt.core.po.BasePO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

public interface BaseDAO<T extends BasePO> {

    void save(T entity) throws DaoException;

    void update(T entity);

    void delete(T entity);

    void logicDelete(Serializable id) throws DaoException;

    T findById(Serializable id);

    List<T> findAll();

    Page<T> list(T po, Pageable pageable);
}
