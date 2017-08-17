package com.github.jyoghurt.security.securityUserRoleR.dao;

import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import com.github.jyoghurt.security.securityUserRoleR.domain.SecurityUserRoleR;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SecurityUserRoleR Mapper
 */
public interface SecurityUserRoleRMapper extends BaseMapper<SecurityUserRoleR> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    SecurityUserRoleR selectById(@Param(ENTITY_CLASS) Class<SecurityUserRoleR> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<SecurityUserRoleR> pageData(@Param(ENTITY_CLASS) Class<SecurityUserRoleR> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<SecurityUserRoleR> findAll(@Param(ENTITY_CLASS) Class<SecurityUserRoleR> entityClass, @Param(DATA) Map<String, Object> data);

    @Delete("delete from SecurityUserRoleR \n" +
            "where userId = #{userId}")
    public void deleteRelByUserId(@Param("userId") String userId);

}
