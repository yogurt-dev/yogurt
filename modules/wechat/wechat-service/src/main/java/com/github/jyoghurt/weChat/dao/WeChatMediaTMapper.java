package com.github.jyoghurt.weChat.dao;


import com.github.jyoghurt.weChat.domain.WeChatMediaT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * WeChatMediaT Mapper
 *
 */
public interface WeChatMediaTMapper extends BaseMapper<WeChatMediaT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WeChatMediaT selectById(@Param(ENTITY_CLASS) Class<WeChatMediaT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WeChatMediaT> pageData(@Param(ENTITY_CLASS) Class<WeChatMediaT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WeChatMediaT> findAll(@Param(ENTITY_CLASS) Class<WeChatMediaT> entityClass, @Param(DATA) Map<String, Object> data);
}
