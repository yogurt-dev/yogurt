package com.github.jyoghurt.activiti.compontent.audit.dao;


import com.github.jyoghurt.activiti.compontent.audit.domain.WorkflowAudit;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * WorkflowAudit Mapper
 *
 */
public interface WorkflowAuditMapper extends BaseMapper<WorkflowAudit> {

    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    WorkflowAudit selectById(@Param(ENTITY_CLASS) Class<WorkflowAudit> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<WorkflowAudit> pageData(@Param(ENTITY_CLASS) Class<WorkflowAudit> entityClass, @Param(DATA) Map<String, Object> map);

    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<WorkflowAudit> findAll(@Param(ENTITY_CLASS) Class<WorkflowAudit> entityClass, @Param(DATA) Map<String, Object> data);
    @Select("select * from WorkFlowAuditT where auditedId=#{auditedId} and auditedClass=#{auditedClass} order by createDateTime asc")
    List<WorkflowAudit> findWorkflowAuditList(@Param("auditedId")String auditedId,@Param("auditedClass")String auditedClass);
}
