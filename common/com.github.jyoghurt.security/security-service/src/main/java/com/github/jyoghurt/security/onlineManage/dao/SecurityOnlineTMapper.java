package com.github.jyoghurt.security.onlineManage.dao;

import com.github.jyoghurt.security.onlineManage.domain.SecurityOnlineT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SecurityOnlineT Mapper
 *
 */
public interface SecurityOnlineTMapper extends BaseMapper<SecurityOnlineT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    SecurityOnlineT selectById(@Param(ENTITY_CLASS) Class<SecurityOnlineT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<SecurityOnlineT> pageData(@Param(ENTITY_CLASS) Class<SecurityOnlineT> entityClass,@Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<SecurityOnlineT> findAll(@Param(ENTITY_CLASS) Class<SecurityOnlineT> entityClass, @Param(DATA) Map<String, Object> data);

//    @Delete("delete from SecurityOnlineT s where s.sessionId = #{sessionId}")
    @Update("update SecurityOnlineT s set s.bsflag='1' where s.sessionId=#{sessionId}")
    void deleteSession(@Param("sessionId") String sessionId);

    @Update("update SecurityOnlineT s set s.bsflag='1'")
    void deleteAll();
}
