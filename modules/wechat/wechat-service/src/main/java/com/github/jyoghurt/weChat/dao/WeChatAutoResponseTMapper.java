package com.github.jyoghurt.weChat.dao;

import com.github.jyoghurt.weChat.domain.WeChatAutoResponseT;
import com.github.jyoghurt.weChat.domain.WeChatAutoResponseTVo;
import com.github.jyoghurt.wechatbasic.enums.WeChatAutoResponseType;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * WeChatAutoResponseT Mapper
 *
 */
public interface WeChatAutoResponseTMapper extends BaseMapper<WeChatAutoResponseT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WeChatAutoResponseT selectById(@Param(ENTITY_CLASS) Class<WeChatAutoResponseT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WeChatAutoResponseT> pageData(@Param(ENTITY_CLASS) Class<WeChatAutoResponseT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WeChatAutoResponseT> findAll(@Param(ENTITY_CLASS) Class<WeChatAutoResponseT> entityClass, @Param(DATA) Map<String, Object> data);


    @Select("select * from WeChatAutoResponseT t  where t.appId=#{appId} and t.ResponseType=#{ResponseType} and " +
            "t.state='0' order by t.createDateTime DESC,t.appId")
    List<WeChatAutoResponseT> findAutoResponseList(@Param("appId") String appId, @Param("ResponseType") WeChatAutoResponseType ResponseType);

    @Select("select * from WeChatAutoResponseT t  where t.appId=#{appId} and t.ResponseType=#{ResponseType} and " +
            "t.state='0' order by t.createDateTime DESC,t.appId")
    List<WeChatAutoResponseTVo> findAutoResponseVoList(@Param("appId") String appId, @Param("ResponseType") WeChatAutoResponseType ResponseType);
}
