package com.github.jyoghurt.weChat.dao;

import com.github.jyoghurt.weChat.domain.WeChatMsgT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * WeChatMsgT Mapper
 *
 */
public interface WeChatMsgTMapper extends BaseMapper<WeChatMsgT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WeChatMsgT selectById(@Param(ENTITY_CLASS) Class<WeChatMsgT> entityClass, @Param(ID) Serializable id);

    @ResultMap("cascadeResultMap")
    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WeChatMsgT> pageData(@Param(ENTITY_CLASS) Class<WeChatMsgT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WeChatMsgT> findAll(@Param(ENTITY_CLASS) Class<WeChatMsgT> entityClass, @Param(DATA) Map<String, Object> data);

    @Select("select w.* from WeChatMsgT w")
    List<WeChatMsgT> getNewsList();
}
