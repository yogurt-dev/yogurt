package com.github.jyoghurt.dataDict.dao;

import com.github.jyoghurt.dataDict.domain.DataDictValue;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DataDictValue Mapper
 *
 */
public interface DataDictValueMapper extends BaseMapper<DataDictValue> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    DataDictValue selectById(@Param(ENTITY_CLASS) Class<DataDictValue> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<DataDictValue> pageData(@Param(ENTITY_CLASS) Class<DataDictValue> entityClass,@Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<DataDictValue> findAll(@Param(ENTITY_CLASS) Class<DataDictValue> entityClass, @Param(DATA) Map<String, Object> data);
}
