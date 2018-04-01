package com.github.jyoghurt.wechatbasic.service.statistics.dao;

import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import com.github.jyoghurt.wechatbasic.service.statistics.domain.WechatStatisticsT;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * WechatStatistics Mapper
 *
 */
public interface WechatStatisticsMapper extends BaseMapper<WechatStatisticsT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WechatStatisticsT selectById(@Param(ENTITY_CLASS) Class<WechatStatisticsT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WechatStatisticsT> pageData(@Param(ENTITY_CLASS) Class<WechatStatisticsT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WechatStatisticsT> findAll(@Param(ENTITY_CLASS) Class<WechatStatisticsT> entityClass, @Param(DATA) Map<String, Object> data);
}
