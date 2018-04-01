package com.github.jyoghurt.weChat.dao;

import com.github.jyoghurt.weChat.domain.WeChatSendMsgT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * WeChatSendMsgT Mapper
 *
 */
public interface WeChatSendMsgTMapper extends BaseMapper<WeChatSendMsgT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WeChatSendMsgT selectById(@Param(ENTITY_CLASS) Class<WeChatSendMsgT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WeChatSendMsgT> pageData(@Param(ENTITY_CLASS) Class<WeChatSendMsgT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WeChatSendMsgT> findAll(@Param(ENTITY_CLASS) Class<WeChatSendMsgT> entityClass, @Param(DATA) Map<String, Object> data);
}
