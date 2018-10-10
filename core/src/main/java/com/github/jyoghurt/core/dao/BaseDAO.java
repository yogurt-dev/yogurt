package com.github.jyoghurt.core.dao;


import com.github.jyoghurt.core.po.BaseEntity;

import java.io.Serializable;

public interface BaseDAO<T extends BaseEntity> {

    void save(T entity);

    void update(T entity);

    void delete(T entity);

    <F extends Serializable > T findById(F id);


}
