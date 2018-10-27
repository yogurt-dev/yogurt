package com.github.jyoghurt.core.service.impl;

import com.github.jyoghurt.core.dao.BaseDAO;
import com.github.jyoghurt.core.exception.DaoException;
import com.github.jyoghurt.core.exception.ServiceException;
import com.github.jyoghurt.core.po.BasePO;
import com.github.jyoghurt.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;


public class BaseServiceImpl<T extends BasePO> implements BaseService<T> {

    @Autowired
    protected BaseDAO<T> baseDAO;

    @Override
    public void save(T entity) throws ServiceException {
        try {
            baseDAO.save(entity);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void update(T entity) {
        baseDAO.update(entity);
    }

    @Override
    public void delete(T entity) {
        baseDAO.delete(entity);
    }

    @Override
    public void logicDelete(Serializable id) throws ServiceException {
        try {
            baseDAO.logicDelete(id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }


    @Override
    public <F extends Serializable> T findById(F id) {
        return baseDAO.findById(id);
    }

    @Override
    public List<T> findAll() {
        return baseDAO.findAll();
    }

    @Override
    public Page<T> list(T po, Pageable pageable) {
        return baseDAO.list(po,pageable);
    }

    //
//    @Override
//    public void save(Object entity, String tableName) {
//
//    }
//
//    @Override
//    public void saveForSelective(Object entity) {
//
//    }
//
//    @Override
//    public void update(Object entity) {
//
//    }
//
//    @Override
//    public void updateForSelective(Object entity) {
//
//    }
//
//    @Override
//    public void delete(Class clazz, Serializable id) {
//
//    }
//
//    @Override
//    public void logicDelete(Class clazz, Serializable id) {
//
//    }
//
//    @Override
//    public Object findById(Class clazz, Serializable id) {
//        return null;
//    }
//
//    @Override
//    public QueryResult getData(Object entity, QueryHandle queryHandle) {
//        return null;
//    }
//
//    @Override
//    public Long count(Object entity, QueryHandle queryHandle) {
//        return null;
//    }
//
//    @Override
//    public List findAll(Object entity, QueryHandle queryHandle) {
//        return null;
//    }
//

//
//    @Override
//    public QueryResult getData(String mapperMethodName, Map param, QueryHandle queryHandle) {
//        return null;
//    }
}
