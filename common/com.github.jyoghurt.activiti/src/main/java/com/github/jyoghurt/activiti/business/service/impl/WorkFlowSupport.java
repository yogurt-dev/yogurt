package com.github.jyoghurt.activiti.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.jyoghurt.activiti.adapter.service.ActivitiService;
import com.github.jyoghurt.activiti.business.annotations.MainFormId;
import com.github.jyoghurt.activiti.business.constants.ActivitiConstants;
import com.github.jyoghurt.activiti.business.exception.RepetitionSubmitException;
import com.github.jyoghurt.activiti.business.flowEntity.FlowEntity;
import com.github.jyoghurt.activiti.business.flowEntity.FlowMainFormEntity;
import com.github.jyoghurt.activiti.business.handle.JPAHandler;
import com.github.jyoghurt.activiti.business.module.FormProperties;
import com.github.jyoghurt.activiti.business.service.WorkFlowService;
import com.github.jyoghurt.activiti.business.vo.WorkFlowToDoVo;
import com.github.jyoghurt.activiti.business.vo.WorkFlowTodoSearchVo;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.QueryResult;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Id;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public abstract class WorkFlowSupport<T, M extends BaseMapper<T>> extends ServiceSupport implements
        WorkFlowService {
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private ActivitiService activitiService;


    public abstract M getMapper();

    public ProcessEngine getProcessEngine() {
        return this.processEngine;
    }


    @Override
    public void startWorkFlow(FlowMainFormEntity flowMainFormEntity, SecurityUserT securityUserT) throws UnsupportedEncodingException {
        //设置相关数据区
        Map<String, Object> vars = new HashMap<>();
        startWorkFlow(flowMainFormEntity, securityUserT, vars);
    }

    @Override
    public void startWorkFlow(FlowMainFormEntity flowMainFormEntity, SecurityUserT securityUserT, Map<String, Object> vars) throws UnsupportedEncodingException {
        //todo 设置工单编号
        //todo 解析业务信息 附件、下一步处理人等
        //获得流程定义名称下的最新流程定义Id
        ProcessDefinition processDefinition = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionKey(flowMainFormEntity.getProcKey())
                .latestVersion()
                .singleResult();
        //获取流程启动FormData
        StartFormData startFormData = processEngine.getFormService().getStartFormData(processDefinition.getId());
        //获取formData中的formProperties
        FormProperties formProperties = activitiService.findFormProperties(startFormData);
        //保存业务实体
        saveOrUpdate(flowMainFormEntity);
        //设置相关数据区
        //设置流程启动者
        vars.put(ActivitiConstants.startPerson, securityUserT.getUserId());
        vars.put(ActivitiConstants.startPersonName, securityUserT.getUserName());
        vars.put(ActivitiConstants.subject, flowMainFormEntity.getSubject());
        vars.put(ActivitiConstants.workOrderNumber, new Date().getTime());
        vars.put(ActivitiConstants.compontentId, flowMainFormEntity.getCompontentId());
        vars.put(ActivitiConstants.compontentTitle, flowMainFormEntity.getCompontentTitle());
        vars.put(ActivitiConstants.auditedId, getMainFormId(flowMainFormEntity));
        vars.put(ActivitiConstants.auditedClass, flowMainFormEntity.getClass().getSimpleName());
        vars.put(ActivitiConstants.BaseFormId, getMainFormId(flowMainFormEntity));
        vars.put(ActivitiConstants.mainFormClassName, flowMainFormEntity.getClass().getName());
        vars.put(ActivitiConstants.mainFormIdName, JPAHandler.getMainFormIdName(flowMainFormEntity));
        //componentId 相关数据集
        Map<String, Object> formDataMap = new HashMap<>();
        formDataMap.put(ActivitiConstants.businessId, getMainFormId(flowMainFormEntity));
        formDataMap.put(ActivitiConstants.businessClass, flowMainFormEntity.getClass().getSimpleName());
        formDataMap.put(ActivitiConstants.FormProperties, JSON.toJSONString(formProperties));
        //设置activitiId与compontentId的关系
        buildActIdAndCompontentR(formProperties, formProperties.getActivitiId(), vars);
        //保存compontent与业务的关系
        buildRelevanceVars(formProperties, formDataMap, vars);
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceById(processDefinition.getId(), getMainFormId(flowMainFormEntity), vars);
        //更新流程实例信息
        flowMainFormEntity.setProInsId(processInstance.getProcessInstanceId());
        saveOrUpdate(flowMainFormEntity);
    }

    /**
     * //解析业务信息
     * //保存附件
     * //更新业务主表的流程状态
     *
     * @param flowEntity 业务实体
     * @param taskId     代办id
     * @
     */
    @Override
    public void completeTask(FlowEntity flowEntity, String taskId) throws RepetitionSubmitException {
        //查询流程数据
        Task task = processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
        if(null==task){
            throw new RepetitionSubmitException();
        }
        TaskFormData taskFormData = processEngine.getFormService().getTaskFormData(task.getId());
        //获取formData中的formProperties
        FormProperties formProperties;
        try {
            formProperties = activitiService.findFormProperties(taskFormData);
        } catch (UnsupportedEncodingException e) {
            throw new BaseErrorException("获取activiti的FormData异常", e);
        }
        //保存或更新业务实体
        saveOrUpdate(flowEntity);
        //设置相关数据区
        Map<String, Object> vars = new HashMap<>();
        if (!ActivitiConstants.AuditImpl.equals(this.getClass().getSimpleName())) {
            vars.put(ActivitiConstants.auditedId, getMainFormId(flowEntity));
            vars.put(ActivitiConstants.auditedClass, flowEntity.getClass().getSimpleName());
        }
        //设置activitiId与compontentId的关系
        Map<String, Object> formDataMap = new HashMap<>();
        formDataMap.put(ActivitiConstants.businessId, getMainFormId(flowEntity));
        formDataMap.put(ActivitiConstants.businessClass, flowEntity.getClass().getSimpleName());
        formDataMap.put(ActivitiConstants.FormProperties, JSON.toJSONString(formProperties));
        //设置activitiId与compontentId的关系
        buildActIdAndCompontentR(formProperties, task.getTaskDefinitionKey(), vars);
        //保存compontent与业务的关系
        buildRelevanceVars(formProperties, formDataMap, vars);
        processEngine.getTaskService().complete(taskId, vars);

    }

    @Override
    public void completeTask(FlowEntity flowEntity, String taskId, Map<String, Object> vars) throws RepetitionSubmitException {
        //查询流程数据
        Task task = processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
        if(null==task){
            throw new RepetitionSubmitException();
        }
        //保存或更新业务实体
        saveOrUpdate(flowEntity);
        TaskFormData taskFormData = processEngine.getFormService().getTaskFormData(task.getId());
        //获取formData中的formProperties
        FormProperties formProperties;
        try {
            formProperties = activitiService.findFormProperties(taskFormData);
        } catch (UnsupportedEncodingException e) {
            throw new BaseErrorException("获取activiti的FormData异常", e);
        }
        //设置相关数据区
        if (!ActivitiConstants.AuditImpl.equals(this.getClass().getSimpleName())) {
            vars.put(ActivitiConstants.auditedId, getMainFormId(flowEntity));
            vars.put(ActivitiConstants.auditedClass, flowEntity.getClass().getSimpleName());
        }
        //设置activitiId与compontentId的关系
        Map<String, Object> formDataMap = new HashMap<>();
        //业务主键及业务表名称
        formDataMap.put(ActivitiConstants.businessId, getPrimaryKey(flowEntity));
        formDataMap.put(ActivitiConstants.businessClass, flowEntity.getClass().getSimpleName());
        formDataMap.put(ActivitiConstants.FormProperties, JSON.toJSONString(formProperties));
        //设置activitiId与compontentId的关系
        buildActIdAndCompontentR(formProperties, task.getTaskDefinitionKey(), vars);
        //保存compontent与业务的关系
        buildRelevanceVars(formProperties, formDataMap, vars);
        processEngine.getTaskService().complete(taskId, vars);
    }


    /**
     * 设置activitiId与compontentId的关系
     *
     * @param formProperties 流程数据
     * @param activitiId     活动定义Id
     * @param vars           相关数据区
     * @return vars           相关数据区
     */
    private Map<String, Object> buildActIdAndCompontentR(FormProperties formProperties, String activitiId, Map<String, Object> vars) {
        Map<String, Object> relevanceMap = new HashMap<>();
        if (formProperties.getFormId() != null) {
            relevanceMap.put(ActivitiConstants.Form, formProperties.getFormId());
        }
        if (formProperties.getEditFormId() != null) {
            relevanceMap.put(ActivitiConstants.editForm, formProperties.getEditFormId());
        }
        if (formProperties.getShowFormId() != null) {
            relevanceMap.put(ActivitiConstants.showForm, formProperties.getShowFormId());
        }
        if (formProperties.getLinkId() != null) {
            relevanceMap.put(ActivitiConstants.Link, formProperties.getLinkId());
            relevanceMap.put(ActivitiConstants.linkType, formProperties.getLinkType());
        }
        relevanceMap.put(ActivitiConstants.compontentTitle, formProperties.getCompontentTitle()==null?"无":formProperties.getCompontentTitle());
        vars.put(activitiId, relevanceMap);
        return vars;
    }

    /**
     * 保存compontent与业务的关系
     *
     * @param formProperties 流程表单属性
     * @param data           业务信息
     * @param vars           相关数据区
     * @return vars           相关数据区
     */
    private Map<String, Object> buildRelevanceVars(FormProperties formProperties, Map<String, Object> data, Map<String, Object> vars) {
        // 保存compontent与业务的关系
        if (formProperties.getFormId() != null) {
            vars.put(formProperties.getFormId(), data);
        }
        if (formProperties.getEditFormId() != null) {
            vars.put(formProperties.getEditFormId(), data);
        }
        if (formProperties.getShowFormId() != null) {
            vars.put(formProperties.getShowFormId(), data);
        }
        if (formProperties.getLinkId() != null) {
            vars.put(formProperties.getLinkId(), data);
        }
        return vars;
    }


    @Override
    public QueryResult findToDo(WorkFlowTodoSearchVo workFlowTodoSearchVo, QueryHandle queryHandle) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int pageRows = queryHandle.getRows();
        int pageStart = (queryHandle.getPage() - 1) * queryHandle.getRows();
        TaskQuery taskQuery;
        try {
            taskQuery = processEngine.getTaskService().createTaskQuery()
                    .processDefinitionKey(workFlowTodoSearchVo.getProcDefKey())
                    .taskCandidateOrAssigned(workFlowTodoSearchVo.getSecurityUserT().getUserAccount())
                    .taskCreatedAfter(StringUtils.isEmpty(workFlowTodoSearchVo.getCreateTime_end()) ? null : sdf.parse(StringUtils.join(workFlowTodoSearchVo.getCreateTime_end(), " 00:00:00")))
                    .taskCreatedBefore(StringUtils.isEmpty(workFlowTodoSearchVo.getCreateTime_end()) ? null : sdf.parse(StringUtils.join(workFlowTodoSearchVo.getCreateTime_end(), " 23:59:59")))
                    .includeProcessVariables().orderByTaskPriority()
                    .orderByTaskCreateTime()
                    .desc();
        } catch (ParseException e) {
            throw new BaseErrorException("查询activiti待办异常", e);
        }
        if (!StringUtils.isEmpty(workFlowTodoSearchVo.getWorkOrderNumber())) {
            taskQuery.processVariableValueLike(ActivitiConstants.workOrderNumber, "%" + workFlowTodoSearchVo.getWorkOrderNumber() + "%");
        }
        if (!StringUtils.isEmpty(workFlowTodoSearchVo.getSubject())) {
            taskQuery.processVariableValueLike(ActivitiConstants.subject, "%" + workFlowTodoSearchVo.getSubject() + "%");
        }

        List<Task> tasks = taskQuery.listPage(pageStart, pageRows);
        QueryResult queryResult = this.newQueryResult();
        queryResult.setData(convert(tasks));
        queryResult.setRecordsTotal(taskQuery.count());
        return queryResult;
    }

    protected List<WorkFlowToDoVo> convert(List<Task> tasks) {
        List<WorkFlowToDoVo> voList = new ArrayList<>();
        for (Task task : tasks) {
            WorkFlowToDoVo workItem = new WorkFlowToDoVo();
            workItem.setTaskId(task.getId());
            workItem.setAssignee(task.getAssignee());
            workItem.setProcInsId(task.getProcessInstanceId());
            workItem.setProcessVariables(task.getProcessVariables());
            workItem.setTaskLocalVariables(task.getTaskLocalVariables());
            workItem.setCreateTime(task.getCreateTime());
            workItem.setActivitiName(task.getName());
            voList.add(workItem);
        }
        return voList;
    }

    /**
     * 保存并提交
     *
     * @param flowEntity 业务实体
     */
    private void saveOrUpdate(FlowEntity flowEntity) {
        if (null == flowEntity) {
            return;
        }
        if (StringUtils.isEmpty(getMainFormId(flowEntity))) {
            save(flowEntity);
            return;
        }
        updateForSelective(flowEntity);
    }

    /**
     * 获取主表ID
     *
     * @param flowEntity 流程基类
     * @return 主表Id
     */
    private String getMainFormId(FlowEntity flowEntity) {
        Class entity = flowEntity.getClass();
        Field[] fields = entity.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            //获取字段中包含MainFormId的注解
            MainFormId mainFormId = f.getAnnotation(MainFormId.class);
            if (mainFormId != null) {
                try {
                    return (String) f.get(flowEntity);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 获取主键
     *
     * @param flowEntity 流程基类
     * @return 主表Id
     */
    private String getPrimaryKey(FlowEntity flowEntity) {
        Class entity = flowEntity.getClass();
        Field[] fields = entity.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            //获取字段中包含MainFormId的注解
            Id id = f.getAnnotation(Id.class);
            if (id != null) {
                try {
                    return (String) f.get(flowEntity);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
