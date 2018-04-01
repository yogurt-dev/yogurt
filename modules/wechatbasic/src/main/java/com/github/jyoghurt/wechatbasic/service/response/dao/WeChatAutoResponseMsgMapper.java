package com.github.jyoghurt.wechatbasic.service.response.dao;

import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import com.github.jyoghurt.wechatbasic.service.response.domain.WeChatAutoResponseMsgT;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * WeChatAutoResponseMsg Mapper
 *
 */
public interface WeChatAutoResponseMsgMapper extends BaseMapper<WeChatAutoResponseMsgT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WeChatAutoResponseMsgT selectById(@Param(ENTITY_CLASS) Class<WeChatAutoResponseMsgT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WeChatAutoResponseMsgT> pageData(@Param(ENTITY_CLASS) Class<WeChatAutoResponseMsgT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WeChatAutoResponseMsgT> findAll(@Param(ENTITY_CLASS) Class<WeChatAutoResponseMsgT> entityClass, @Param(DATA) Map<String, Object> data);
}
