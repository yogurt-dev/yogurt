package com.github.jyoghurt.core.service;


import com.github.jyoghurt.core.exception.ServiceException;
import com.github.jyoghurt.core.po.BasePO;

import java.io.Serializable;

/**
 * 服务接口的基类
 *
 * @param <T>此服务接口服务的数据模型，即model
 */
public interface BaseService<T extends BasePO> {
    /**
     * 保存实体
     *
     * @param entity 待保存的实体
     */
    void save(T entity) throws ServiceException;

    /**
     * 更新实体
     *
     * @param entity 业务实体
     */
    void update(T entity);

    /**
     * 删除实体
     */
    void delete(T entity);

    /**
     * 逻辑删除
     *
     * @param id 主键
     */
    void logicDelete( Serializable id) throws ServiceException;

    /**
     * 根据ID获取实体
     *
     * @param id 业务实体ID
     * @return 业务实体
     */
    <F extends Serializable> T findById(F id);

//
////    /**
////     * 批量保存
////     *
////     * @param entities 待保存实体列表
////     *
////     */
////    void saveBatch(List<T> entities) ;
//
//    /**
//     * 根据选择的保存实体
//     *
//     * @param entity 待保存的实体
//     *
//     */
//    void saveForSelective(T entity) ;
//

//
//    /**
//     * 根据选择的更新实体
//     *
//     * @param entity 业务实体
//     *
//     */
//    void updateForSelective(T entity) ;
//
//    /**
//     * 删除实体
//     *
//     *
//     */
//    void delete(Class<T> clazz, Serializable id) ;

//
////    /**
////     * 根据条件逻辑删除
////     * 实体不需要设置deleteFlag=true，否则会将deleteFlag=true拼到条件中
////     *
////     * @param entity 根据非空字段当做删除条件
////     *
////     */
////    void logicDeleteByCondition(T entity) ;
//
////
////    /**
////     * add by limiao 20160825
////     * 根据条件逻辑删除
////     * 实体不需要设置deleteFlag=true，否则会将deleteFlag=true拼到条件中
////     *
////     * @param entity      根据非空字段当做删除条件
////     * @param queryHandle queryHandle
////     *
////     */
////    void logicDeleteByCondition(T entity, QueryHandle queryHandle) ;
//
////    /**
////     * 根据条件删除
////     *
////     * @param entity 根据非空字段当做删除条件
////     *
////     */
////    void deleteByCondition(T entity) ;
////
////    /**
////     * 根据条件删除
////     *
////     * @param entity 根据非空字段当做删除条件
////     *
////     */
////    public void deleteByCondition(T entity, QueryHandle queryHandle) ;
//

//
//    /**
//     * 获取数据
//     *
//     * @param entity      查询业务实体
//     * @param queryHandle 查询辅助类
//     * @return 根据查询条件查询的查询结果集
//     *
//     */
//    QueryResult<T> getData(T entity, QueryHandle queryHandle) ;
//
//    /**
//     * 按条件查询记录总数
//     *
//     * @param entity      查询业务实体
//     * @param queryHandle 查询辅助类
//     *
//     */
//    Long count(T entity, QueryHandle queryHandle) ;
//
//    /**
//     * 按条件查询记录集合
//     *
//     * @param entity      业务实体类或业务查询实体类
//     * @param queryHandle 查询辅助类
//     * @return 业务实体集合
//     *
//     */
//    List<T> findAll(T entity, QueryHandle queryHandle) ;
//

//
//    /**
//     * @param mapperMethodName mapperId
//     * @param param 查询参数
//     * @param queryHandle 分页信息
//     * @return
//     */
//    QueryResult getData(String mapperMethodName, Map<String,Object> param, QueryHandle queryHandle);
//    /**
//     * 按条件查询记录集合
//     *
//     * @param entity 业务实体类或业务查询实体类
//     * @return 业务实体集合
//     */
//    List<T> findAll(T entity);
}
