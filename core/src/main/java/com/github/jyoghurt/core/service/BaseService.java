package com.github.jyoghurt.core.service;


import com.github.jyoghurt.core.exception.ServiceException;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.QueryResult;

import java.io.Serializable;
import java.util.List;

/**
 * 服务接口的基类
 *
 * @param <T>此服务接口服务的数据模型，即model
 */
public interface BaseService<T> {
    /**
     * 保存实体
     *
     * @param entity 待保存的实体
     * @throws ServiceException {@inheritDoc}
     */
    void save(T entity) throws ServiceException;

    /**
     * 批量保存
     *
     * @param entities 待保存实体列表
     * @throws ServiceException {@inheritDoc}
     */
    void saveBatch(List<T> entities) throws ServiceException;

    /**
     * 根据选择的保存实体
     *
     * @param entity 待保存的实体
     * @throws ServiceException {@inheritDoc}
     */
    void saveForSelective(T entity) throws ServiceException;

    /**
     * 更新实体
     *
     * @param entity 业务实体
     * @throws ServiceException {@inheritDoc}
     */
    void update(T entity) throws ServiceException;

    /**
     * 根据选择的更新实体
     *
     * @param entity 业务实体
     * @throws ServiceException {@inheritDoc}
     */
    void updateForSelective(T entity) throws ServiceException;

    /**
     * 删除实体
     *
     * @param id 业务实体ID
     * @throws ServiceException {@inheritDoc}
     */
    void delete(Serializable id) throws ServiceException;

    /**
     * 根据条件删除
     *
     * @param entity 根据非空字段当做删除条件
     * @throws ServiceException {@inheritDoc}
     */
    void deleteByCondition(T entity) throws ServiceException;

    /**
     * 根据条件删除
     *
     * @param entity 根据非空字段当做删除条件
     * @throws ServiceException {@inheritDoc}
     */
    public void deleteByCondition(T entity, QueryHandle queryHandle) throws ServiceException;

    /**
     * 根据ID获取实体
     *
     * @param id 业务实体ID
     * @return 业务实体
     * @throws ServiceException {@inheritDoc}
     */
    T find(Serializable id) throws ServiceException;

    /**
     * 获取数据
     *
     * @param entity      查询业务实体
     * @param queryHandle 查询辅助类
     * @return 根据查询条件查询的查询结果集
     * @throws ServiceException {@inheritDoc}
     */
    QueryResult<T> getData(T entity, QueryHandle queryHandle) throws ServiceException;

    /**
     * 按条件查询记录总数
     *
     * @param entity      查询业务实体
     * @param queryHandle 查询辅助类
     * @throws ServiceException {@inheritDoc}
     */
    Long count(T entity, QueryHandle queryHandle) throws ServiceException;

    /**
     * 按条件查询记录集合
     *
     * @param entity      业务实体类或业务查询实体类
     * @param queryHandle 查询辅助类
     * @return 业务实体集合
     * @throws ServiceException {@inheritDoc}
     */
    List<T> findAll(T entity, QueryHandle queryHandle) throws ServiceException;

    /**
     * 按条件查询记录集合
     *
     * @param entity 业务实体类或业务查询实体类
     * @return 业务实体集合
     * @throws ServiceException {@inheritDoc}
     */
    List<T> findAll(T entity) throws ServiceException;

}
