package com.github.yogurt.core.dao;


import com.github.yogurt.core.exception.DaoException;
import com.github.yogurt.core.po.BasePO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

public interface BaseDAO<T extends BasePO> {

    void save(T entity) throws DaoException;

    void update(T entity);

    void logicDelete(Serializable id, Serializable userId) throws DaoException;

    T findById(Serializable id);

    List<T> findAll();

    Page<T> list(T po, Pageable pageable);

    void batchSave(List<T> poList);

    void batchUpdate(List<T> poList);
}
