package com.github.jyoghurt.weChat.dao;

import com.github.jyoghurt.weChat.domain.WeChatRelevanceT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * RelevanceChat Mapper
 */
public interface RelevanceChatMapper extends BaseMapper<WeChatRelevanceT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WeChatRelevanceT selectById(@Param(ENTITY_CLASS) Class<WeChatRelevanceT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WeChatRelevanceT> pageData(@Param(ENTITY_CLASS) Class<WeChatRelevanceT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WeChatRelevanceT> findAll(@Param(ENTITY_CLASS) Class<WeChatRelevanceT> entityClass, @Param(DATA) Map<String, Object> data);

    @Select("select * from  WeChatRelevanceT where openId=#{openId}")
    WeChatRelevanceT findByOpenId(@Param("openId") String openId);

    @Select("select * from (select * from WeChatRelevanceT t where (appId=#{appId} or " +
            "appId=#{appIdF} OR appId=#{appIdQ}) ORDER BY t.createDateTime desc) n group by n.appId")
    List<WeChatRelevanceT> findGroup(@Param("appId") String appId, @Param("appIdF") String appIdF, @Param("appIdQ") String
            appIdQ);
}
