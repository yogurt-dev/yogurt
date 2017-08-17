package com.github.jyoghurt.activiti.compontent.audit.servive.impl;


import com.github.jyoghurt.activiti.business.exception.RepetitionSubmitException;
import com.github.jyoghurt.activiti.business.service.impl.WorkFlowSupport;
import com.github.jyoghurt.activiti.compontent.audit.dao.WorkflowAuditMapper;
import com.github.jyoghurt.activiti.compontent.audit.domain.WorkflowAudit;
import com.github.jyoghurt.activiti.compontent.audit.enums.AuditEnum;
import com.github.jyoghurt.activiti.compontent.audit.servive.WorkflowAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("workflowAuditService")
public class WorkflowAuditImpl extends WorkFlowSupport<WorkflowAudit, WorkflowAuditMapper> implements WorkflowAuditService {
    @Autowired
    private WorkflowAuditMapper workflowAuditMapper;

    @Override
    public WorkflowAuditMapper getMapper() {
        return workflowAuditMapper;
    }

    @Override
    public void delete(Serializable id)  {
//        getMapper().delete(WorkflowAudit.class, id);
    }

    @Override
    public WorkflowAudit find(Serializable id)  {
        return getMapper().selectById(WorkflowAudit.class, id);
    }

    @Override
    public void subAudit(WorkflowAudit workflowAudit, String taskId, String activitiId) throws RepetitionSubmitException {
        Map<String, Object> vars = new HashMap<>();
        vars.put(activitiId + "route", AuditEnum.PASS.equals(workflowAudit.getResult()) ? 1 : 0);
        completeTask(workflowAudit, taskId, vars);
    }

    @Override
    public List<WorkflowAudit> findWorkflowAuditList(String auditedId, String auditedClass)   {
        return getMapper().findWorkflowAuditList(auditedId, auditedClass);
    }
}
