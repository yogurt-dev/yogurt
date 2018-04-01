package com.github.jyoghurt.weChat.dao;

import com.github.jyoghurt.weChat.domain.WeChatWebsiteMenuT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * WeChatWebsiteMenuT Mapper
 *
 */
public interface WeChatWebsiteMenuTMapper extends BaseMapper<WeChatWebsiteMenuT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WeChatWebsiteMenuT selectById(@Param(ENTITY_CLASS) Class<WeChatWebsiteMenuT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WeChatWebsiteMenuT> pageData(@Param(ENTITY_CLASS) Class<WeChatWebsiteMenuT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WeChatWebsiteMenuT> findAll(@Param(ENTITY_CLASS) Class<WeChatWebsiteMenuT> entityClass, @Param(DATA) Map<String, Object> data);

    @Select("select * from WeChatWebsiteMenuT s  where s.parentId=#{parentId} order by s.sort " +
            "asc")
    List<WeChatWebsiteMenuT> findByParentId(@Param("parentId") String parentId);

    @Select("delete from WeChatWebsiteMenuT where webId=#{webId}")
    void deleteMenuByWebId(@Param("webId") String webId);
    @Select("UPDATE WeChatWebsiteMenuT m set  m.sort = m.sort+1  where m.parentId=#{parentId} and m.sort>#{sort}")
    void updateBySort(@Param("parentId") String parentId, @Param("sort") int sort);
}
