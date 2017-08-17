package com.github.jyoghurt.serviceLog.dao;


import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import com.github.jyoghurt.serviceLog.domain.ServiceLogT;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * ServiceLog Mapper
 *
 */
public interface ServiceLogMapper extends BaseMapper<ServiceLogT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    ServiceLogT selectById(@Param(ENTITY_CLASS) Class<ServiceLogT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<ServiceLogT> pageData(@Param(ENTITY_CLASS) Class<ServiceLogT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<ServiceLogT> findAll(@Param(ENTITY_CLASS) Class<ServiceLogT> entityClass, @Param(DATA) Map<String, Object> data);
}
