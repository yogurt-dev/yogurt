package com.github.jyoghurt.sw.dao;

import com.github.jyoghurt.sw.domain.SwitchT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Switch Mapper
 */
public interface SwitchMapper extends BaseMapper<SwitchT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    SwitchT selectById(@Param(ENTITY_CLASS) Class<SwitchT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<SwitchT> pageData(@Param(ENTITY_CLASS) Class<SwitchT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<SwitchT> findAll(@Param(ENTITY_CLASS) Class<SwitchT> entityClass, @Param(DATA) Map<String, Object> data);

}
