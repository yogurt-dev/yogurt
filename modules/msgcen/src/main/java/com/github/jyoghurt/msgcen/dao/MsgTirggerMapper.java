package com.github.jyoghurt.msgcen.dao;

import com.github.jyoghurt.msgcen.domain.MsgTirggerT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * MsgTirgger Mapper
 *
 */
public interface MsgTirggerMapper extends BaseMapper<MsgTirggerT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    MsgTirggerT selectById(@Param(ENTITY_CLASS) Class<MsgTirggerT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<MsgTirggerT> pageData(@Param(ENTITY_CLASS) Class<MsgTirggerT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<MsgTirggerT> findAll(@Param(ENTITY_CLASS) Class<MsgTirggerT> entityClass, @Param(DATA) Map<String, Object> data);
}
