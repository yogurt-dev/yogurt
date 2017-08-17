package com.github.jyoghurt.security.securityUserUnitR.dao;

import com.github.jyoghurt.security.securityUserUnitR.domain.SecurityUserUnitR;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SecurityUserUnit Mapper
 *
 */
public interface SecurityUserUnitMapper extends BaseMapper<SecurityUserUnitR> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    SecurityUserUnitR selectById(@Param(ENTITY_CLASS) Class<SecurityUserUnitR> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<SecurityUserUnitR> pageData(@Param(ENTITY_CLASS) Class<SecurityUserUnitR> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    @ResultMap("securityUserMap")
    List<SecurityUserUnitR> findAll(@Param(ENTITY_CLASS) Class<SecurityUserUnitR> entityClass, @Param(DATA) Map<String, Object> data);
}
