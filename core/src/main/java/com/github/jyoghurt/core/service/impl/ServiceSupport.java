package com.github.jyoghurt.core.service.impl;

import com.github.jyoghurt.core.configuration.PageConvert;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.domain.BaseEntity;
import com.github.jyoghurt.core.exception.ServiceException;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.EasyUIResult;
import com.github.jyoghurt.core.result.QueryResult;
import com.github.jyoghurt.core.service.BaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.github.jyoghurt.core.utils.beanUtils.BeanUtils.getValueMap;

/**
 * <p>
 * 服务支持
 * <p>
 * 为服务组件的基类，必须继承
 *
 * @param <T> 该服务组件服务的数据模型，即model;
 * @author jtwu
 */
@SuppressWarnings("unchecked")
public abstract class ServiceSupport<T, M extends BaseMapper<T>> implements BaseService<T> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public abstract M getMapper();

    @Value("${tableJsLib}")
    private String tableJsLib;

    @Resource(name = "pageConvert")
    private PageConvert pageConvert;

    @Override
    public void save(T entity) throws ServiceException {
        if (entity instanceof BaseEntity) {
            ((BaseEntity) entity).setCreateDateTime(new Date());
            ((BaseEntity) entity).setModifyDateTime(((BaseEntity) entity).getCreateDateTime());
            setFounder((BaseEntity) entity);
        }
        getMapper().save(entity);
    }

    @Override
    public void saveBatch(List<T> entities) throws ServiceException {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        if (BaseEntity.class.isAssignableFrom(entities.get(0).getClass())) {
            for (T entity : entities) {
                ((BaseEntity) entity).setCreateDateTime(new Date());
                ((BaseEntity) entity).setModifyDateTime(((BaseEntity) entity).getCreateDateTime());
                setFounder((BaseEntity) entity);
            }
        }
        getMapper().saveBatch(entities);


    }

    @Override
    public void saveForSelective(T entity) throws ServiceException {
        if (entity instanceof BaseEntity) {
            ((BaseEntity) entity).setCreateDateTime(new Date());
            ((BaseEntity) entity).setModifyDateTime(((BaseEntity) entity).getCreateDateTime());
            setFounder((BaseEntity) entity);
        }
        getMapper().saveForSelective(entity);
    }

    @Override
    public void update(T entity) throws ServiceException {
        if (entity instanceof BaseEntity) {
            ((BaseEntity) entity).setModifyDateTime(new Date());
            try {
                ((BaseEntity) entity).setModifierId((String) getSessionAttr(BaseEntity.OPERATOR_ID));
                ((BaseEntity) entity).setModifierName((String) getSessionAttr(BaseEntity.OPERATOR_NAME));
            } catch (Exception e) {
                logger.info("update时session获取失败!", e);
                ((BaseEntity) entity).setModifierId(BaseEntity.DEFAULT_OPERATOR);
                ((BaseEntity) entity).setModifierName(BaseEntity.DEFAULT_OPERATOR);
            }
        }
        getMapper().update(entity);
    }

    @Override
    public void deleteByCondition(T entity) throws ServiceException {
        getMapper().deleteByCondition((Class<T>) entity.getClass(), getValueMap(entity));
    }

    @Override
    public void deleteByCondition(T entity, QueryHandle queryHandle) throws ServiceException {
        getMapper().deleteByCondition((Class<T>) entity.getClass(), getValueMap(entity, queryHandle));
    }

    @Override
    public QueryResult<T> getData(T entity, QueryHandle queryHandle) throws ServiceException {
        QueryResult<T> qr = newQueryResult();
        qr.setData(getMapper().pageData((Class<T>) entity.getClass(), getValueMap(queryHandle, entity).chainPutAll
                (queryHandle.getExpandData())));
        qr.setRecordsTotal(getMapper().pageTotalRecord((Class<T>) entity.getClass(), getValueMap(queryHandle, entity)
                .chainPutAll(queryHandle.getExpandData())));
        return qr;
    }

    protected QueryResult newQueryResult() {
        if (StringUtils.isEmpty(tableJsLib)) {
            return new EasyUIResult();
        }
        return pageConvert.createQueryResult();
    }

    protected QueryResult<T> newQueryResult(List<T> list) {
        if (StringUtils.isEmpty(tableJsLib)) {
            return new EasyUIResult(list);
        }
        return pageConvert.createQueryResult(list);
    }

    @Override
    public void updateForSelective(T entity) throws ServiceException {
        if (entity instanceof BaseEntity) {
            ((BaseEntity) entity).setModifyDateTime(new Date());
            setFounder((BaseEntity) entity);
        }
        getMapper().updateForSelective(entity);
    }

    @Override
    public Long count(T entity, QueryHandle queryHandle) throws ServiceException {
        return getMapper().pageTotalRecord((Class<T>) entity.getClass(), getValueMap(queryHandle, entity)
                .chainPutAll(queryHandle.getExpandData()));
    }

    //modify by limiao 20160222 .chainPutAll(queryHandle.getExpandData()
    @Override
    public List<T> findAll(T entity, QueryHandle queryHandle) throws ServiceException {
        return getMapper().findAll((Class<T>) entity.getClass(), getValueMap(queryHandle, entity).chainPutAll(queryHandle.getExpandData()));
    }


    @Override
    public List<T> findAll(T entity) throws ServiceException {
        return findAll(entity, null);
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

    private void setFounder(BaseEntity entity) throws ServiceException {
        try {
            entity.setFounderId((String) getSessionAttr(BaseEntity.OPERATOR_ID));
            entity.setFounderName((String) getSessionAttr(BaseEntity.OPERATOR_NAME));
        } catch (Exception e) {
            logger.info("setFounder时session获取失败!", e);
            entity.setFounderId(BaseEntity.DEFAULT_OPERATOR);
            entity.setFounderName(BaseEntity.DEFAULT_OPERATOR);
        }
    }
}