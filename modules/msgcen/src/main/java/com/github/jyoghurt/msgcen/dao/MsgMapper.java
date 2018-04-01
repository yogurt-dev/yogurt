package com.github.jyoghurt.msgcen.dao;

import com.github.jyoghurt.msgcen.domain.MsgT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Msg Mapper
 *
 */
public interface MsgMapper extends BaseMapper<MsgT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    MsgT selectById(@Param(ENTITY_CLASS) Class<MsgT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<MsgT> pageData(@Param(ENTITY_CLASS) Class<MsgT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<MsgT> findAll(@Param(ENTITY_CLASS) Class<MsgT> entityClass, @Param(DATA) Map<String, Object> data);
}
