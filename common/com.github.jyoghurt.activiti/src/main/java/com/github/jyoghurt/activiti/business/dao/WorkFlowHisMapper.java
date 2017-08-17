package com.github.jyoghurt.activiti.business.dao;


import com.github.jyoghurt.activiti.business.flowEntity.WorkFlowHisRelevance;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2016/1/4.
 */

public interface WorkFlowHisMapper extends BaseMapper<WorkFlowHisRelevance> {
    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WorkFlowHisRelevance selectById(@Param(ENTITY_CLASS) Class<WorkFlowHisRelevance> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WorkFlowHisRelevance> pageData(@Param(ENTITY_CLASS) Class<WorkFlowHisRelevance> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WorkFlowHisRelevance> findAll(@Param(ENTITY_CLASS) Class<WorkFlowHisRelevance> entityClass, @Param(DATA) Map<String, Object> data);
}