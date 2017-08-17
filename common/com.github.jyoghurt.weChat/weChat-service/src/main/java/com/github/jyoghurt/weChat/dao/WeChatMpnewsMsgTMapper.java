package com.github.jyoghurt.weChat.dao;

import com.github.jyoghurt.weChat.domain.WeChatMpnewsMsgT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * WeChatMpnewsMsgT Mapper
 *
 */
public interface WeChatMpnewsMsgTMapper extends BaseMapper<WeChatMpnewsMsgT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WeChatMpnewsMsgT selectById(@Param(ENTITY_CLASS) Class<WeChatMpnewsMsgT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WeChatMpnewsMsgT> pageData(@Param(ENTITY_CLASS) Class<WeChatMpnewsMsgT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WeChatMpnewsMsgT> findAll(@Param(ENTITY_CLASS) Class<WeChatMpnewsMsgT> entityClass, @Param(DATA) Map<String, Object> data);

    @Select("delete from WeChatMpnewsMsgT where messageId=#{messageId}")
    void deleteByMessageId(@Param("messageId") String messageId);


    @Select("select * from WeChatMpnewsMsgT where messageId=#{messageId} order by sort asc")
    List<WeChatMpnewsMsgT> getListByMessageId(@Param("messageId") String messageId);


}
