package com.github.jyoghurt.security.securityMenuRoleR.dao;

import com.github.jyoghurt.security.securityMenuRoleR.domain.SecurityMenuRoleR;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SecurityMenuRoleR Mapper
 *
 */
public interface SecurityMenuRoleRMapper extends BaseMapper<SecurityMenuRoleR> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    SecurityMenuRoleR selectById(@Param(ENTITY_CLASS) Class<SecurityMenuRoleR> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<SecurityMenuRoleR> pageData(@Param(ENTITY_CLASS) Class<SecurityMenuRoleR> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<SecurityMenuRoleR> findAll(@Param(ENTITY_CLASS) Class<SecurityMenuRoleR> entityClass, @Param(DATA) Map<String, Object> data);

    @Delete("delete from SecurityMenuRoleR \n" +
            "where roleId = #{roleId}")
    void deleteRelByRoleId(@Param("roleId") String roleId);
}
