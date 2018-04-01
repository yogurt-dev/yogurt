package com.github.jyoghurt.weChat.dao;

import com.github.jyoghurt.weChat.domain.WeChatAutoResponseMsgT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * WeChatAutoResponseMsgT Mapper
 *
 */
public interface WeChatAutoResponseMsgTMapper extends BaseMapper<WeChatAutoResponseMsgT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WeChatAutoResponseMsgT selectById(@Param(ENTITY_CLASS) Class<WeChatAutoResponseMsgT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WeChatAutoResponseMsgT> pageData(@Param(ENTITY_CLASS) Class<WeChatAutoResponseMsgT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WeChatAutoResponseMsgT> findAll(@Param(ENTITY_CLASS) Class<WeChatAutoResponseMsgT> entityClass, @Param(DATA) Map<String, Object> data);

    @Select("select * from WeChatAutoResponseMsgT t  where t.AutoResponseId=#{AutoResponseId} order by t.createDateTime DESC")
    List<WeChatAutoResponseMsgT> findByAutoResponseId(@Param("AutoResponseId") String AutoResponseId);
}
