package com.github.jyoghurt.core.service.impl;

import com.github.jyoghurt.core.annotations.SnapshotEntity;
import com.github.jyoghurt.core.configuration.impl.PageConfiguration;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.domain.BaseEntity;
import com.github.jyoghurt.core.domain.BaseSnapshotEntity;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.exception.DaoException;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.EasyUIResult;
import com.github.jyoghurt.core.result.QueryResult;
import com.github.jyoghurt.core.service.BaseService;
import com.github.jyoghurt.core.utils.ChainMap;
import com.github.jyoghurt.core.utils.JPAUtils;
import com.github.jyoghurt.core.utils.beanUtils.BeanUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.github.jyoghurt.core.utils.beanUtils.BeanUtils.getValueMap;

/**
 * 服务支持
 * <p>
 * 为服务组件的基类，必须继承
 *
 * @param <T> 该服务组件服务的数据模型，即model;
 * @author jtwu
 */
public abstract class ServiceSupport<T, M extends BaseMapper<T>> implements BaseService<T> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public abstract M getMapper();


    @Value("${tableJsLib}")
    private String tableJsLib;

    private static final Integer VERSIONSTART = 1;

    private static final String COUNTSUFFIX = "Count";

    @Override
    public void save(T entity) {
        if (entity instanceof BaseEntity) {
            ((BaseEntity) entity).setCreateDateTime(new Date());
            ((BaseEntity) entity).setModifyDateTime(((BaseEntity) entity).getCreateDateTime());
            setFounder((BaseEntity) entity);
        }

        //modify by baoxiaobing@lvyushequ.com 增加历史版本处理
        if (entity instanceof BaseSnapshotEntity) {
            //设置版本
            ((BaseSnapshotEntity) entity).setVersion(VERSIONSTART);
            getMapper().save(entity);
            saveHis(entity);
            return;
        }
        getMapper().save(entity);
    }


    @Override
    public void save(T entity, String tableName) {
        if (entity instanceof BaseEntity) {
            ((BaseEntity) entity).setCreateDateTime(new Date());
            ((BaseEntity) entity).setModifyDateTime(((BaseEntity) entity).getCreateDateTime());
            setFounder((BaseEntity) entity);
        }
        getMapper().saveByTableName(entity, tableName);
    }


    /**
     * 处理包含历史注解的保存动作
     * add by baoxiaobing@lvyushequ.com
     *
     * @param entity
     * @Date 2016-12-20
     */
    private void saveHis(T entity) {
        //获取注解
        try {
            getMapper().save((T) generateHisObj(entity));
        } catch (Exception e) {
            throw new DaoException(StringUtils.join("历史对象生成失败 entityClass =", entity.getClass().getName()));
        }
    }

    /**
     * 批量保存历史数据
     * add by baoxiaobing@lvyushequ.com
     *
     * @param entities
     * @Date 2016-12-20
     */
    private void saveHisBatch(List<T> entities) {
        List<T> saveTarget = new ArrayList<>();
        try {
            for (T entity : entities) {
                saveTarget.add((T) generateHisObj(entity));
            }
            getMapper().saveBatch(saveTarget);
        } catch (Exception e) {
            throw new DaoException(StringUtils.join("历史对象生成失败"));
        }
    }

    /**
     * 更新历史数据
     * add by baoxiaobing@lvyushequ.com
     *
     * @param entity
     * @Date 2016-12-20
     */
    private void updateHis(T entity) {
        Integer version = getEntityVersion(entity);
        try {
            int record = checkConcurrency(entity, version);
            if (record == 0) {
                throw new DaoException(StringUtils.join("当前提交数据已过期，请重新编辑 entityClass =", entity.getClass().getName()));
            }
            ((BaseSnapshotEntity) entity).setVersion(version + 1);
            getMapper().updateForSelective(entity);
            saveHis(entity);
        } catch (IllegalAccessException e) {
            throw new DaoException(StringUtils.join("检测并发失败 entityClass =", entity.getClass().getName()));
        }
    }

    /**
     * 检测是否存在并发
     *
     * @param entity
     * @return
     */
    private int checkConcurrency(T entity, Integer version) throws IllegalAccessException {
        //更新version字段
        Field field = JPAUtils.getIdField(entity.getClass());
        field.setAccessible(true);
        return getMapper().updateBySql((Class<T>) entity.getClass(), "t.version = t.version+1", new
                ChainMap<String, Object>().chainPut("version", version).chainPut(field.getName(), field.get(entity)));
    }

    /**
     * 生成历史数据对象
     *
     * @param entity
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private Object generateHisObj(T entity) throws IllegalAccessException, InstantiationException {
        //获取注解
        Annotation his = entity.getClass().getAnnotation(SnapshotEntity.class);
        if (null == his) {
            throw new DaoException(StringUtils.join("实体未配置VersionTable注解 entityClass =", entity.getClass().getName()));
        }
        //获取类
        Class hisEntity = ((SnapshotEntity) his).hisEntity();
        Object obj = hisEntity.newInstance();
        BeanUtils.copyPropertiesJ(entity, obj);
        return obj;
    }


    @Override
    public void saveBatch(List<T> entities) {
        boolean isHisEntity = false;
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        if (BaseEntity.class.isAssignableFrom(entities.get(0).getClass()) || BaseSnapshotEntity.class.isAssignableFrom
                (entities.get(0).getClass())) {
            for (T entity : entities) {
                ((BaseEntity) entity).setCreateDateTime(new Date());
                ((BaseEntity) entity).setModifyDateTime(((BaseEntity) entity).getCreateDateTime());
                setFounder((BaseEntity) entity);
                if (entity instanceof BaseSnapshotEntity) {
                    //设置版本
                    ((BaseSnapshotEntity) entity).setVersion(VERSIONSTART);
                    isHisEntity = true;
                }

            }
        }
        getMapper().saveBatch(entities);
        if (isHisEntity) {
            saveHisBatch(entities);
        }
    }

    @Override
    public void saveForSelective(T entity) {
        if (entity instanceof BaseEntity) {
            ((BaseEntity) entity).setCreateDateTime(new Date());
            ((BaseEntity) entity).setModifyDateTime(((BaseEntity) entity).getCreateDateTime());
            setFounder((BaseEntity) entity);
        }
        getMapper().saveForSelective(entity);
    }

    @Override
    public void update(T entity) {
        if (null == JPAUtils.gtIdValue(entity)) {
            return;
        }
        if (entity instanceof BaseEntity) {
            ((BaseEntity) entity).setModifyDateTime(new Date());
            //modify by limiao 20160811 处理修改的时候勿将创建人和创建时间更新成修改人和修改时间的问题
            this.setModifyFounder((BaseEntity) entity);
        }
        if (entity instanceof BaseSnapshotEntity) {
            updateHis(entity);
            return;
        }
        getMapper().update(entity);
    }

    @Override
    public void delete(Serializable id) {
        throw new BaseErrorException("You must override this method!");
    }

    @Override
    public void logicDelete(Serializable id) {
        throw new BaseErrorException("You must override this method!");
    }

    @Override
    public void logicDeleteByCondition(T entity) {
        getMapper().logicDeleteByCondition((Class<T>) entity.getClass(), getValueMap(entity));
    }

    @Override
    public void logicDeleteByCondition(T entity, QueryHandle queryHandle) {
        getMapper().logicDeleteByCondition((Class<T>) entity.getClass(), getValueMap(entity, queryHandle));
    }

    @Override
    public void deleteByCondition(T entity) {
        getMapper().deleteByCondition((Class<T>) entity.getClass(), getValueMap(entity));
    }

    @Override
    public void deleteByCondition(T entity, QueryHandle queryHandle) {
        getMapper().deleteByCondition((Class<T>) entity.getClass(), getValueMap(entity, queryHandle));
    }

    @Override
    public QueryResult<T> getData(T entity, QueryHandle queryHandle) {
        QueryResult<T> qr = newQueryResult();
        qr.setData(getMapper().pageData((Class<T>) entity.getClass(), getValueMap(queryHandle, entity).chainPutAll
                (queryHandle == null ? null : queryHandle.getExpandData())));
        qr.setRecordsTotal(getMapper().pageTotalRecord((Class<T>) entity.getClass(), getValueMap(queryHandle, entity)
                .chainPutAll(queryHandle == null ? null : queryHandle.getExpandData())));
        return qr;
    }

    @Override
    public QueryResult<T> findListBySql(String customSql, QueryHandle queryHandle) {
        QueryResult<T> qr = newQueryResult();
        qr.setData(getMapper().findListBySql(customSql, getValueMap(queryHandle).chainPutAll
                (queryHandle == null ? null : queryHandle.getExpandData())));
        qr.setRecordsTotal(getMapper().findListTotalRecordBySql(customSql, getValueMap(queryHandle).chainPutAll
                (queryHandle == null ? null : queryHandle.getExpandData())));
        return qr;
    }

    protected QueryResult newQueryResult() {
        if (StringUtils.isEmpty(tableJsLib)) {
            return new EasyUIResult();
        }
        return PageConfiguration.create().createQueryResult();
    }

    protected QueryResult<T> newQueryResult(List<T> list) {
        if (StringUtils.isEmpty(tableJsLib)) {
            return new EasyUIResult(list);
        }
        return PageConfiguration.create().createQueryResult(list);
    }

    @Override
    public void updateForSelective(T entity) {
        if (null == JPAUtils.gtIdValue(entity)) {
            return;
        }

        if (entity instanceof BaseEntity) {
            ((BaseEntity) entity).setModifyDateTime(new Date());
            //add by limiao 20160811 处理修改的时候勿将创建人和创建时间更新成修改人和修改时间的问题
            this.setModifyFounder((BaseEntity) entity);
        }

        if (entity instanceof BaseSnapshotEntity) {
            //获取主键
            updateHis(entity);
            return;
        }
        getMapper().updateForSelective(entity);
    }

    private Integer getEntityVersion(T entity) {
        Integer version = null;
        try {
            Field idField = JPAUtils.getIdField(entity.getClass());
            if (null == idField) {
                throw new DaoException(StringUtils.join(entity.getClass().getName(), "实体未配置@Id "));
            }
            idField.setAccessible(true);
            Object sourceObj = getMapper().selectById((Class<T>) entity.getClass(), (String) idField.get(entity));
            version = ((BaseSnapshotEntity) sourceObj).getVersion();
        } catch (IllegalAccessException e) {
            throw new DaoException(StringUtils.join(entity.getClass().getName(), "获取version信息失败 "));
        }
        return version;
    }

    @Override
    public Long count(T entity, QueryHandle queryHandle) {
        return getMapper().pageTotalRecord((Class<T>) entity.getClass(), getValueMap(queryHandle, entity)
                .chainPutAll(queryHandle == null ? null : queryHandle.getExpandData()));
    }

    //modify by limiao 20160222 .chainPutAll(queryHandle==null?null:queryHandle.getExpandData()
    @Override
    public List<T> findAll(T entity, QueryHandle queryHandle) {
        if (queryHandle == null) {
            return getMapper().findAll((Class<T>) entity.getClass(), getValueMap(queryHandle, entity));
        } else {
            return getMapper().findAll((Class<T>) entity.getClass(), getValueMap(queryHandle, entity).chainPutAll(queryHandle == null ? null : queryHandle.getExpandData()));
        }
    }

    @Override
    public int updateBySql(String customSql, T entity, QueryHandle queryHandle) {
        return getMapper().updateBySql((Class<T>) entity.getClass(), customSql, getValueMap(queryHandle, entity)
                .chainPutAll
                        (queryHandle == null ? null : queryHandle.getExpandData()));
    }

    @Override
    public List<T> findAll(T entity) {
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

    private void setFounder(BaseEntity entity) {
        try {

            entity.setFounderId(null == getSessionAttr(BaseEntity.OPERATOR_ID) ? BaseEntity.DEFAULT_OPERATOR :
                    (String) getSessionAttr(BaseEntity.OPERATOR_ID));
            entity.setFounderName(null == getSessionAttr(BaseEntity.OPERATOR_NAME) ? BaseEntity.DEFAULT_OPERATOR :
                    (String) getSessionAttr(BaseEntity.OPERATOR_NAME));
        } catch (Exception e) {
            logger.debug("setFounder时session获取失败!");
            entity.setFounderId(BaseEntity.DEFAULT_OPERATOR);
            entity.setFounderName(BaseEntity.DEFAULT_OPERATOR);
        }
    }

    //add by limiao 20160811 处理修改的时候勿将创建人和创建时间更新成修改人和修改时间的问题
    private void setModifyFounder(BaseEntity entity) {
        try {
            entity.setModifierId((String) getSessionAttr(BaseEntity.OPERATOR_ID));
            entity.setModifierName((String) getSessionAttr(BaseEntity.OPERATOR_NAME));
        } catch (Exception e) {
            logger.info("update时session获取失败!");
            entity.setModifierId(BaseEntity.DEFAULT_OPERATOR);
            entity.setModifierName(BaseEntity.DEFAULT_OPERATOR);
        }
    }


    @Override
    public QueryResult getData(String mapperQueryMethodName, Map<String, Object> param, QueryHandle queryHandle) {
        if (param == null) {
            param = new HashedMap();
        }
        if (StringUtils.isBlank(mapperQueryMethodName)) {
            throw new BaseErrorException("未指定xml查询ID");
        }
        String countMethodName = mapperQueryMethodName + COUNTSUFFIX;
        //获取分页信息
        int pageRows = queryHandle.configPage().getRows();
        int pageStart = (queryHandle.getPage() - 1) * queryHandle.getRows();
        param.put("limit", pageRows);
        param.put("start", pageStart);
        QueryResult queryResult = this.newQueryResult();
        try {
            Class z = getMapper().getClass();
            Method queryMethod = z.getDeclaredMethod(mapperQueryMethodName, Map.class);
            Method countMethod = z.getDeclaredMethod(countMethodName, Map.class);
            List data = (List) queryMethod.invoke(getMapper(), param);
            int totalNumber = ((Integer) countMethod.invoke(getMapper(), param)).intValue();
            queryResult.setData(data);
            queryResult.setRecordsTotal(totalNumber);
        } catch (NoSuchMethodException e) {
            throw new BaseErrorException("未定义方法：" + mapperQueryMethodName + "或" + countMethodName);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("异常",e);
            throw new BaseErrorException("调用方法异常");
        }
        return queryResult;
    }
}