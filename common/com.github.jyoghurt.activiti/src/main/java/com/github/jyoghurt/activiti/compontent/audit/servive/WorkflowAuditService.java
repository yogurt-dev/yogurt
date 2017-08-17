package com.github.jyoghurt.activiti.compontent.audit.servive;


import com.github.jyoghurt.activiti.business.exception.RepetitionSubmitException;
import com.github.jyoghurt.activiti.business.service.WorkFlowService;
import com.github.jyoghurt.activiti.compontent.audit.domain.WorkflowAudit;

import java.util.List;

/**
 * 审核服务层
 *
 */
public interface WorkflowAuditService extends WorkFlowService {
    public void subAudit(WorkflowAudit workflowAudit, String taskId, String activitiId) throws RepetitionSubmitException;

    public List<WorkflowAudit> findWorkflowAuditList(String auditedId,String auditedClass)  ;
}
