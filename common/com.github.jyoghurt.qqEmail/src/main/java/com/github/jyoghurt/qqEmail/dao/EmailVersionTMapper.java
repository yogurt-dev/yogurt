package com.github.jyoghurt.qqEmail.dao;

import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import com.github.jyoghurt.qqEmail.domain.EmailVersionT;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * EmailVersionT Mapper
 */
public interface EmailVersionTMapper extends BaseMapper<EmailVersionT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    EmailVersionT selectById(@Param(ENTITY_CLASS) Class<EmailVersionT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<EmailVersionT> pageData(@Param(ENTITY_CLASS) Class<EmailVersionT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<EmailVersionT> findAll(@Param(ENTITY_CLASS) Class<EmailVersionT> entityClass, @Param(DATA) Map<String, Object> data);

    @Select("select * from EmailVersionT where clientId=#{clientId} order by createDateTime Desc limit 1")
    EmailVersionT findCurrentVersion(@Param("clientId") String clientId);
}
