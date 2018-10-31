package com.github.yogurt.core.service.impl;

import com.github.yogurt.core.Configuration;
import com.github.yogurt.core.dao.BaseDAO;
import com.github.yogurt.core.exception.DaoException;
import com.github.yogurt.core.exception.ServiceException;
import com.github.yogurt.core.po.BasePO;
import com.github.yogurt.core.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
public class BaseServiceImpl<T extends BasePO> implements BaseService<T> {

    @Autowired
    private Configuration configuration;

    @Autowired
    protected BaseDAO<T> baseDAO;

    @Override
    public void save(T entity) throws ServiceException {
        try {
            processCreateColumns(entity);
            baseDAO.save(entity);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void update(T po) {
        processModifyColumns(po);
        baseDAO.update(po);
    }

    @Override
    public void logicDelete(Serializable id) throws ServiceException {
        try {
            baseDAO.logicDelete(id, (String) getSessionAttr(configuration.getUserId()));
        } catch (Exception e) {
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
        return baseDAO.list(po, pageable);
    }

    /**
     * 获取request
     *
     * @return HttpServletRequest
     */
    private Object getSessionAttr(String attr) {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getSession()
                .getAttribute(attr);
    }

    private void processCreateColumns(T entity) {
        if (entity == null) {
            return;
        }
        if (entity.getGmtCreate() == null) {
            entity.setGmtCreate(LocalDateTime.now());
        }
        setFounder(entity);
    }

    private void processModifyColumns(T entity) {
        if (entity == null) {
            return;
        }
        if (entity.getGmtModified() == null) {
            entity.setGmtModified(LocalDateTime.now());
        }
        this.setModifyFounder(entity);
    }

    private void setFounder(BasePO po) {
        try {
            if (null == po.getCreatorId()) {
                po.setCreatorId(null == getSessionAttr(configuration.getUserId()) ? -1L :
                        (Long) getSessionAttr(configuration.getUserId()));
            }
        } catch (Exception e) {
            log.debug("setFounder时session获取失败!");
            po.setCreatorId(-1L);
        }
    }

    private void setModifyFounder(BasePO po) {
        try {
            if (null == po.getModifierId()) {
                po.setModifierId(null == getSessionAttr(configuration.getUserId()) ? -1L :
                        (Long) getSessionAttr(configuration.getUserId()));
            }
        } catch (Exception e) {
            log.debug("update时session获取失败!");
            po.setModifierId(-1L);
        }
    }

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
