package com.github.jyoghurt.weChat.dao;

import com.github.jyoghurt.weChat.domain.WeChatWebMouldT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * WeChatWebMouldT Mapper
 *
 */
public interface WeChatWebMouldTMapper extends BaseMapper<WeChatWebMouldT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WeChatWebMouldT selectById(@Param(ENTITY_CLASS) Class<WeChatWebMouldT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WeChatWebMouldT> pageData(@Param(ENTITY_CLASS) Class<WeChatWebMouldT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WeChatWebMouldT> findAll(@Param(ENTITY_CLASS) Class<WeChatWebMouldT> entityClass, @Param(DATA) Map<String, Object> data);
}
