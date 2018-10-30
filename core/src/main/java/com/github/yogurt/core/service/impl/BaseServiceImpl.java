package com.github.yogurt.core.service.impl;

import com.github.yogurt.core.dao.BaseDAO;
import com.github.yogurt.core.exception.DaoException;
import com.github.yogurt.core.exception.ServiceException;
import com.github.yogurt.core.po.BasePO;
import com.github.yogurt.core.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.util.List;

@Slf4j
public class BaseServiceImpl<T extends BasePO> implements BaseService<T> {

    @Value("${yogurt.userId}")
    private String userId;
    @Value("${yogurt.userName}")
    private String userName;

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

    /**
     * 获取request
     *
     * @return HttpServletRequest
     */
    private Object getSessionAttr(String attr) {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession()
                .getAttribute(attr);
    }


    private void setFounder(BasePO entity) {
        try {
            if (StringUtils.isEmpty(entity.getFounderId())) {
                entity.setFounderId(null == getSessionAttr(userId) ? "system" :
                        (String) getSessionAttr(userId));
            }
            if (StringUtils.isEmpty(entity.getFounderName())) {
                entity.setFounderName(null == getSessionAttr(userName) ? "system" :
                        (String) getSessionAttr(userName));
            }
        } catch (Exception e) {
            log.debug("setFounder时session获取失败!");
            entity.setFounderId("system");
            entity.setFounderName("system");
        }
    }

    //add by limiao 20160811 处理修改的时候勿将创建人和创建时间更新成修改人和修改时间的问题
    private void setModifyFounder(BasePO entity) {
        try {
            if (StringUtils.isEmpty(entity.getModifierId())) {
                entity.setModifierId((String) getSessionAttr(userId));
            }
            if (StringUtils.isEmpty(entity.getModifierName())) {
                entity.setModifierName((String) getSessionAttr(userName));
            }
        } catch (Exception e) {
            log.debug("update时session获取失败!");
            entity.setModifierId("system");
            entity.setModifierName("system");
        }
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
