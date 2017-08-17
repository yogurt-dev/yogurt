package com.github.jyoghurt.common.msgcen.dao;

import com.github.jyoghurt.common.msgcen.domain.MsgTmplT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * MsgTmpl Mapper
 *
 */
public interface MsgTmplMapper extends BaseMapper<MsgTmplT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    MsgTmplT selectById(@Param(ENTITY_CLASS) Class<MsgTmplT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<MsgTmplT> pageData(@Param(ENTITY_CLASS) Class<MsgTmplT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<MsgTmplT> findAll(@Param(ENTITY_CLASS) Class<MsgTmplT> entityClass, @Param(DATA) Map<String, Object> data);
}
