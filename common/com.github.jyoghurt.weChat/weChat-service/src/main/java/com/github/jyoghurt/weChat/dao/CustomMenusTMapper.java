package com.github.jyoghurt.weChat.dao;

import com.github.jyoghurt.weChat.domain.WeChatCustomMenusT;
import com.github.jyoghurt.weChat.domain.WeChatCustomMenusTVo;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * CustomMenusT Mapper
 *
 */
public interface CustomMenusTMapper extends BaseMapper<WeChatCustomMenusT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WeChatCustomMenusT selectById(@Param(ENTITY_CLASS) Class<WeChatCustomMenusT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WeChatCustomMenusT> pageData(@Param(ENTITY_CLASS) Class<WeChatCustomMenusT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WeChatCustomMenusT> findAll(@Param(ENTITY_CLASS) Class<WeChatCustomMenusT> entityClass, @Param(DATA) Map<String, Object> data);
    @Select("select * from WeChatCustomMenusT where parentId=#{parentId} and appId=#{appId} order by createDateTime ASC")
    List<WeChatCustomMenusTVo> findByParentIdAsc(@Param("parentId") String parentId, @Param("appId") String appId);
    @Select("select * from WeChatCustomMenusT where parentId=#{parentId} and appId=#{appId} order by createDateTime DESC")
    List<WeChatCustomMenusTVo> findByParentIdDesc(@Param("parentId") String parentId, @Param("appId") String appId);
    @InsertProvider(type = BaseMapperProvider.class, method = "save")
    void saveCustomMenusT(@Param(ENTITY) WeChatCustomMenusTVo customMenusT);

}
