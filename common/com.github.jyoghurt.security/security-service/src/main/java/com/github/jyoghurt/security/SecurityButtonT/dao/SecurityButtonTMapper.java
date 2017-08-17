package com.github.jyoghurt.security.SecurityButtonT.dao;

import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import com.github.jyoghurt.security.SecurityButtonT.domain.SecurityButtonT;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SecurityButtonT Mapper
 *
 */
public interface SecurityButtonTMapper extends BaseMapper<SecurityButtonT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    SecurityButtonT selectById(@Param(ENTITY_CLASS) Class<SecurityButtonT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<SecurityButtonT> pageData(@Param(ENTITY_CLASS) Class<SecurityButtonT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<SecurityButtonT> findAll(@Param(ENTITY_CLASS) Class<SecurityButtonT> entityClass, @Param(DATA) Map<String, Object> data);
    @Select("select * from SecurityButtonT s  where s.menuId=#{menuId}")
    List<SecurityButtonT> getButtonByMenuId(@Param("menuId") String menuId);
}
