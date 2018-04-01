package com.github.jyoghurt.quartz.dao;


import com.github.jyoghurt.quartz.domain.ScheduleJob;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * ScheduleJob Mapper
 *
 */
public interface ScheduleJobMapper extends BaseMapper<ScheduleJob> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    ScheduleJob selectById(@Param(ENTITY_CLASS) Class<ScheduleJob> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<ScheduleJob> pageData(@Param(ENTITY_CLASS) Class<ScheduleJob> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<ScheduleJob> findAll(@Param(ENTITY_CLASS) Class<ScheduleJob> entityClass, @Param(DATA) Map<String, Object> data);
}
