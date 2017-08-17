package com.github.jyoghurt.security.securityUserResourceR.dao;

import com.github.jyoghurt.security.securityUserResourceR.domain.SecurityUserResourceR;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SecurityUserResource Mapper
 *
 */
public interface SecurityUserResourceMapper extends BaseMapper<SecurityUserResourceR> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    SecurityUserResourceR selectById(@Param(ENTITY_CLASS) Class<SecurityUserResourceR> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<SecurityUserResourceR> pageData(@Param(ENTITY_CLASS) Class<SecurityUserResourceR> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<SecurityUserResourceR> findAll(@Param(ENTITY_CLASS) Class<SecurityUserResourceR> entityClass, @Param(DATA) Map<String, Object> data);
}
