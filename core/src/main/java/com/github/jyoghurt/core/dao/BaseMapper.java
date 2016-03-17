package com.github.jyoghurt.core.dao;

import org.apache.ibatis.annotations.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by jtwu on 2015/4/21.
 * mybatis通用处理类
 */

public interface BaseMapper<T> {
    String ENTITY_CLASS = "entityClass";
    String ENTITY = "entity";
    String ENTITIES = "entities";
    String ID = "id";
    String DATA = "data";
    String CUSTOM_SQL = "customSql";

    /**
     * 插入一条记录
     *
     * @param entity 业务实体
     */
    @InsertProvider(type = BaseMapperProvider.class, method = "save")
    void save(@Param(ENTITY) T entity);

    /**
     * 批量插入
     *
     * @param entities 插入结果集
     */
    @InsertProvider(type = BaseMapperProvider.class, method = "saveBatch")
    void saveBatch(@Param(ENTITIES) List<T> entities);

    /**
     * 插入非空字段
     *
     * @param entity 业务实体
     */
    @InsertProvider(type = BaseMapperProvider.class, method = "saveForSelective")
    void saveForSelective(@Param(ENTITY) T entity);

    /**
     * 更新一条记录
     *
     * @param entity 业务实体
     */
    @UpdateProvider(type = BaseMapperProvider.class, method = "update")
    void update(@Param(ENTITY) T entity);

    /**
     * 更新非空字段
     *
     * @param entity 业务实体
     */
    @UpdateProvider(type = BaseMapperProvider.class, method = "updateForSelective")
    void updateForSelective(@Param(ENTITY) T entity);

    /**
     * 删除
     *
     * @param entityClass 实体类型
     * @param id          主键
     */
    @DeleteProvider(type = BaseMapperProvider.class, method = "delete")
    void delete(@Param(ENTITY_CLASS) Class<?> entityClass, @Param(ID) Serializable id);

    /**
     * 逻辑删除
     *
     * @param entityClass
     * @param id
     */
    @UpdateProvider(type = BaseMapperProvider.class, method = "logicDelete")
    void logicDelete(@Param(ENTITY_CLASS) Class<?> entityClass, @Param(ID) Serializable id);

    /**
     * 根据条件逻辑删除
     *
     * @param entityClass 业务实体类
     * @param data        删除条件
     */
    @UpdateProvider(type = BaseMapperProvider.class, method = "logicDeleteByCondition")
    void logicDeleteByCondition(@Param(ENTITY_CLASS) Class<T> entityClass, @Param(DATA) Map<String, Object> data);

    /**
     * 根据条件删除
     *
     * @param entityClass 业务实体类
     * @param data        删除条件
     */
    @DeleteProvider(type = BaseMapperProvider.class, method = "deleteByCondition")
    void deleteByCondition(@Param(ENTITY_CLASS) Class<T> entityClass, @Param(DATA) Map<String, Object> data);

    /**
     * 根据主键查询
     * 此处来写注解，在子类里面生效
     *
     * @param entityClass 业务实体
     * @param id          业务主键
     * @return 业务实体
     */
    T selectById(@Param(ENTITY_CLASS) Class<T> entityClass, @Param(ID) Serializable id);

    /**
     * 获取分页记录总数
     * 此处来写注解，在子类里面生效
     *
     * @param entityClass 业务实体类
     * @param data        查询条件
     * @return 实体列表
     */
    List<T> pageData(@Param(ENTITY_CLASS) Class<T> entityClass, @Param(DATA) Map<String, Object> data);

    /**
     * 统计分页记录总数
     *
     * @param entityClass 业务实体类
     * @param data        查询条件
     * @return 实体列表
     */
    @SelectProvider(type = BaseMapperProvider.class, method = "pageTotalRecord")
    Long pageTotalRecord(@Param(ENTITY_CLASS) Class<T> entityClass, @Param(DATA) Map<String, Object> data);

    /**
     * 按条件查询记录总数
     *
     * @param form 查询条件
     * @return 查询记录总数
     */
    Long count(Map<String, Object> form);

    /**
     * 按条件查询记录集合
     * 此处来写注解，在子类里面生效
     *
     * @param entityClass 实体类型
     * @param data        查询条件
     * @return 查询结果列表
     */
    List<T> findAll(@Param(ENTITY_CLASS) Class<T> entityClass, @Param(DATA) Map<String, Object> data);

    /**
     * 根据自定义sql查询list
     *
     * @param customSql 自定义sql
     * @return 查询结果列表
     */
    @SelectProvider(type = BaseMapperProvider.class, method = "findListBySql")
    List<T> findListBySql(@Param(CUSTOM_SQL) String customSql, @Param(DATA) Map<String, Object> data);

    /**
     * @param customSql
     * @param data
     * @return
     */
    @SelectProvider(type = BaseMapperProvider.class, method = "findListTotalRecordBySql")
    Long findListTotalRecordBySql(@Param(CUSTOM_SQL) String customSql, @Param(DATA) Map<String, Object> data);

    /**
     * @param customSql
     * @param data
     * @return
     */
    @SelectProvider(type = BaseMapperProvider.class, method = "findUniqueObjectBySql")
    Object findUniqueObjectBySql(@Param("customSql") String customSql, @Param("data") Map<String, Object> data);

    /**
     * 自定义更新sql
     *
     * @param customSql
     * @param data
     * @return
     */
    @UpdateProvider(type = BaseMapperProvider.class, method = "updateBySql")
    void updateBySql(@Param(ENTITY_CLASS) Class<T> entityClass,@Param(CUSTOM_SQL) String customSql, @Param(DATA) Map<String, Object> data);

}