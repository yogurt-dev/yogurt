package com.github.jyoghurt.security.SecurityRoleButtonR.dao;

import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import com.github.jyoghurt.security.SecurityButtonT.domain.SecurityButtonT;
import com.github.jyoghurt.security.SecurityRoleButtonR.domain.SecurityRoleButtonR;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SecurityRoleButtonR Mapper
 *
 */
public interface SecurityRoleButtonRMapper extends BaseMapper<SecurityRoleButtonR> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    SecurityRoleButtonR selectById(@Param(ENTITY_CLASS) Class<SecurityRoleButtonR> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<SecurityRoleButtonR> pageData(@Param(ENTITY_CLASS) Class<SecurityRoleButtonR> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<SecurityRoleButtonR> findAll(@Param(ENTITY_CLASS) Class<SecurityRoleButtonR> entityClass, @Param(DATA) Map<String, Object> data);

    @Select("select * from SecurityButtonT s  where s.buttonId in (select buttonId from SecurityRoleButtonR where roleId " +
            "in (select roleId from SecurityUserRoleR where userId = #{userId}))")
    List<SecurityButtonT> getButtonByUserId(@Param("userId") String userId);
}
