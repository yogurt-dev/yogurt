package com.github.jyoghurt.weChat.dao;

import com.github.jyoghurt.weChat.domain.WeChatWebsiteT;
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
 * WeChatWebsiteT Mapper
 *
 */
public interface WeChatWebsiteTMapper extends BaseMapper<WeChatWebsiteT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WeChatWebsiteT selectById(@Param(ENTITY_CLASS) Class<WeChatWebsiteT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WeChatWebsiteT> pageData(@Param(ENTITY_CLASS) Class<WeChatWebsiteT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WeChatWebsiteT> findAll(@Param(ENTITY_CLASS) Class<WeChatWebsiteT> entityClass, @Param(DATA) Map<String, Object> data);

    @ResultMap("websitetResultMap")
    @Select("select * from WeChatWebsiteT s join WeChatWebsiteMenuT m on s.webId=m.webId where s.webId=#{webId} order by m.sort " +
            "asc")
    WeChatWebsiteT findByWebId(@Param("webId") String webId);
}
