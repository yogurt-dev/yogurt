package com.github.jyoghurt.activiti.business.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.jyoghurt.activiti.adapter.service.ActivitiService;
import com.github.jyoghurt.activiti.business.constants.ActivitiConstants;
import com.github.jyoghurt.activiti.business.enums.CompontentShowType;
import com.github.jyoghurt.activiti.business.exception.WorkFlowException;
import com.github.jyoghurt.activiti.business.module.*;
import com.github.jyoghurt.activiti.business.service.BulidPageService;
import com.github.jyoghurt.activiti.business.utils.ParseButtonUtil;
import com.github.jyoghurt.security.securityRoleT.domain.SecurityRoleT;
import com.github.jyoghurt.security.securityRoleT.service.SecurityRoleTService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.google.gson.Gson;
import net.sf.json.JSONObject;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dell on 2016/1/7.
 */
@Service("bulidPageService")
public class BuildPageServiceImpl implements BulidPageService {
    private static Logger logger = LoggerFactory.getLogger(BuildPageServiceImpl.class);
    /**
     * activiti核心Api接口注入
     */
    @Autowired
    private ProcessEngine processEngine;
    /**
     * activiti适配接口注入
     */
    @Autowired
    private ActivitiService activitiService;
    @Autowired
    private SecurityRoleTService securityRoleTService;
    private SecurityUserT securityUserT;


    /**
     * 待办流程轨迹构造方法
     *
     * @param procInsId 流程实例Id
     * @param workItem  待办工作项
     * @param userT     人员信息
     * @return JSONObject  前台框架构造页面所需JSON
     * @throws WorkFlowException
     */
    @Override
    public JSONObject build(String procInsId, WorkItem workItem, SecurityUserT userT) throws WorkFlowException, UnsupportedEncodingException {
        if (null != userT) {
            List<SecurityRoleT> securityRoles = securityRoleTService.queryRoleByUserIdSelected(userT.getUserId());
            userT.setRoles(securityRoles);
            securityUserT = userT;
        }
        String taskId = workItem == null ? null : workItem.getTaskId();
        JSONObject object = new JSONObject();
        //根据流程实例Id获得流程实例
        logger.info("获取activiti流程实例");
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(procInsId)
                .includeProcessVariables()
                .singleResult();
        logger.info("获取activiti流程实例完毕");
        logger.info("获取activiti历史流程实例");
        HistoricProcessInstance historicProcessInstance = processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .processInstanceId(procInsId)
                .includeProcessVariables()
                .onlyByProInsId()
                .singleResult();
        logger.info("获取activiti历史流程实例完毕");
        //根据流程实例获得流程相关数据区
        Map<String, Object> processVariables = processInstance == null ? null : processInstance.getProcessVariables();
        Map<String, Object> hisProcessVariables = historicProcessInstance.getProcessVariables();
        //根据任务id获得当前任务
        logger.info("获取当前任务实例");
        Task task = StringUtils.isEmpty(taskId) ? null : processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
        logger.info("获取当前任务实例完毕");
        //根据流程实例Id获得流程轨迹
        logger.info("获取历史任务流程轨迹");
        List<HistoricActivityInstance> histepList = processEngine.getHistoryService().createHistoricActivityInstanceQuery()
                .processInstanceId(procInsId)
                .finished()
                .orderByExecutionId()
                .asc()
                .list();
        logger.info("获取历史任务流程轨迹完毕");
        //历史轨迹拼接List
        Set<Compontent> compontentSet = new LinkedHashSet<>();
        //根据历史轨迹查询对应的form属性
        logger.info("拼接历史");
        for (int i = 0; i < histepList.size(); i++) {
            HistoricActivityInstance hisActivityInstance = histepList.get(i);
            Compontent hisCompontent = createHisCompontent(hisProcessVariables, historicProcessInstance, hisActivityInstance, CompontentShowType.SHOW);
            if (hisCompontent != null) {
                if (compontentSet.contains(hisCompontent)) {
                    compontentSet.remove(hisCompontent);
                }
                compontentSet.add(hisCompontent);
            }
        }
        logger.info("拼接历史完毕");
        //拼接当前待执行compontent
        logger.info("拼接当前任务");
        if (task != null) {
            Compontent compontent = createTaskCompontent(processInstance, task, processVariables);
            compontent.setWorkItem(workItem);
            if (compontentSet.contains(compontent)) {
                compontentSet.remove(compontent);
            }
            compontentSet.add(compontent);
        }
        logger.info("拼接当前任务完毕");
        //封裝流程实例全局信息
        ProcessInstanceMsg processInstanceMsg = new ProcessInstanceMsg();
        processInstanceMsg.setProcessDefinitionKey(processInstance == null ? historicProcessInstance.getProcessDefinitionId() : processInstance.getProcessDefinitionKey());
        processInstanceMsg.setRelevanceData(processVariables == null ? hisProcessVariables : processVariables);
        processInstanceMsg.setActivitiId(task != null ? task.getTaskDefinitionKey() : null);
        object.put("processInstanceMsg", processInstanceMsg);
        object.put("flowList", compontentSet);
        return object;
    }

    /**
     * 构造当前任务compontent
     *
     * @param processInstance  流程实例
     * @param task             当前任务
     * @param processVariables 流程相关数据区
     * @return Compontent        组件
     */
    private Compontent createTaskCompontent(ProcessInstance processInstance, Task task, Map<String, Object> processVariables) throws WorkFlowException, UnsupportedEncodingException {
        //获取活动关联的compontent
        Map<String, Object> compontentMap = (Map) processVariables.get(task.getTaskDefinitionKey());
        //走过的流程环节 当前任务为编辑 可通过之前的compontentMap获取数据 无需调用接口
        if (compontentMap != null) {
            return createTaskCompontent(compontentMap, processInstance, task, processVariables);
        }
        //未走过的流程环节从当前任务中获取信息
        TaskFormData taskFormData = processEngine.getFormService().getTaskFormData(task.getId());
        FormProperties formProperties = activitiService.findFormProperties(taskFormData);
        ParseButtonUtil.parseButtonRule(JSONObject.fromObject(securityUserT), JSONObject.fromObject(processVariables), CompontentShowType.EDIT, formProperties);
        String compontentTitle = null;
        if (formProperties.getCompontentTitle() != null) {
            compontentTitle = formProperties.getCompontentTitle();
        }
        //根据组件类型封装组件
        //Form型
        if (formProperties.getFormId() != null) {
            Map<String, Object> formDataMap = processVariables.get(formProperties.getFormId()) == null ? (Map) processVariables.get(task.getTaskDefinitionKey()) : (Map) processVariables.get(formProperties.getFormId());
            Form form = new Form(formProperties.getFormId(), CompontentShowType.EDIT, processInstance.getProcessInstanceId(), task.getTaskDefinitionKey(), compontentTitle
                    , formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessId) == null ? null : formDataMap.get(ActivitiConstants.businessId).toString()
                    , formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessClass) == null ? null : formDataMap.get(ActivitiConstants.businessClass).toString()
                    , formProperties.getButtons());
            return form;
        }
        if (formProperties.getEditFormId() != null) {
            Map<String, Object> formDataMap = processVariables.get(formProperties.getEditFormId()) == null ? (Map) processVariables.get(task.getTaskDefinitionKey()) : (Map) processVariables.get(formProperties.getEditFormId());
            Form form = new Form(formProperties.getEditFormId(), CompontentShowType.EDIT, processInstance.getProcessInstanceId(), task.getTaskDefinitionKey(), compontentTitle
                    , formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessId) == null ? null : formDataMap.get(ActivitiConstants.businessId).toString()
                    , formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessClass) == null ? null : formDataMap.get(ActivitiConstants.businessClass).toString()
                    , formProperties.getButtons());
            return form;
        }
        if (formProperties.getLinkId() != null) {
            Map<String, Object> formDataMap = processVariables.get(formProperties.getLinkId()) == null ? (Map) processVariables.get(task.getTaskDefinitionKey()) : (Map) processVariables.get(formProperties.getLinkId());
            Link link = new Link(formProperties.getLinkId(), CompontentShowType.EDIT, processInstance.getProcessInstanceId(), task.getTaskDefinitionKey(), compontentTitle
                    , formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessId) == null ? null : formDataMap.get(ActivitiConstants.businessId).toString()
                    , formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessClass) == null ? null : formDataMap.get(ActivitiConstants.businessClass).toString()
                    , formDataMap == null ? formProperties.getLinkType() : formDataMap.get(ActivitiConstants.linkType) == null ? null : formDataMap.get(ActivitiConstants.linkType).toString()
            );
            return link;
        }
        logger.error("未取到流程实例Id为:" + processInstance.getProcessInstanceId() + "taskId为:" + task.getId() + "的taskCompontent");
        throw new WorkFlowException("1", "未取到流程实例Id为:" + processInstance.getProcessInstanceId()
                + "taskId为:" + task.getId() + "的taskCompontent");
    }

    /**
     * 走过的流程环节 当前任务为编辑 可通过之前的compontentMap获取数据 无需调用接口
     *
     * @param compontentMap   当前活动实例与compontent关联关系map
     * @param processInstance 流程实例
     * @param task            当前任务
     * @return Compontent        组件
     */
    private Compontent createTaskCompontent(Map<String, Object> compontentMap, ProcessInstance processInstance, Task task, Map<String, Object> processVariables) throws WorkFlowException {
        //根据组件类型封装组件
        String compontentTitle = null;
        if (compontentMap.get(ActivitiConstants.compontentTitle) != null) {
            compontentTitle = compontentMap.get(ActivitiConstants.compontentTitle).toString();
        }
        //Form型
        if (compontentMap.get(ActivitiConstants.Form) != null) {
            Gson gson = new Gson();
            Map<String, Object> formDataMap = (Map) processVariables.get(compontentMap.get(ActivitiConstants.Form));
            FormProperties formProperties = gson.fromJson(formDataMap.get(ActivitiConstants.FormProperties).toString(), FormProperties.class);
            ParseButtonUtil.parseButtonRule(JSONObject.fromObject(securityUserT), JSONObject.fromObject(processVariables), CompontentShowType.EDIT, formProperties);
            Form form = new Form(formProperties.getFormId(), CompontentShowType.EDIT, processInstance.getProcessInstanceId(), task.getTaskDefinitionKey()
                    , formProperties.getCompontentTitle()
                    , formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessId) == null ? null : formDataMap.get(ActivitiConstants.businessId).toString()
                    , formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessClass) == null ? null : formDataMap.get(ActivitiConstants.businessClass).toString()
                    , formProperties.getButtons());
            return form;
        }
        if (compontentMap.get(ActivitiConstants.editForm) != null) {
            Gson gson = new Gson();
            Map<String, Object> formDataMap = (Map) processVariables.get(compontentMap.get(ActivitiConstants.editForm));
            FormProperties formProperties = gson.fromJson(formDataMap.get(ActivitiConstants.FormProperties).toString(), FormProperties.class);
            ParseButtonUtil.parseButtonRule(JSONObject.fromObject(securityUserT), JSONObject.fromObject(processVariables), CompontentShowType.EDIT, formProperties);
            Form form = new Form(formProperties.getEditFormId(), CompontentShowType.EDIT, processInstance.getProcessInstanceId(), task.getTaskDefinitionKey()
                    , compontentTitle, formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessId) == null ? null : formDataMap.get(ActivitiConstants.businessId).toString()
                    , formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessClass) == null ? null : formDataMap.get(ActivitiConstants.businessClass).toString()
                    , formProperties.getButtons());
            return form;
        }
        if (compontentMap.get(ActivitiConstants.Link) != null) {
            Gson gson = new Gson();
            Map<String, Object> formDataMap = (Map) processVariables.get(compontentMap.get(ActivitiConstants.Link));
            FormProperties formProperties = gson.fromJson(formDataMap.get(ActivitiConstants.FormProperties).toString(), FormProperties.class);
            Link link = new Link(formProperties.getLinkId(), CompontentShowType.EDIT, processInstance.getProcessInstanceId(), task.getTaskDefinitionKey(), compontentTitle
                    , formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessId) == null ? null : formDataMap.get(ActivitiConstants.businessId).toString()
                    , formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessClass) == null ? null : formDataMap.get(ActivitiConstants.businessClass).toString()
                    , formDataMap == null ? formProperties.getLinkType() : formDataMap.get(ActivitiConstants.linkType) == null ? formProperties.getLinkType() : formDataMap.get(ActivitiConstants.linkType).toString()
            );
            return link;
        }
        logger.error("未取到流程实例Id为:" + processInstance.getProcessInstanceId() + "taskId为:" + task.getId() + "的taskCompontent");
        throw new WorkFlowException("1", "未取到流程实例Id为:" + processInstance.getProcessInstanceId() + "taskId为:" + task.getId() + "的taskCompontent");
    }

    /**
     * 根据流程历史数据构造前台compontent对象
     *
     * @param processVariables    相关数据区
     * @param processInstance     流程实例
     * @param hisActivityInstance 当前活动实例
     * @param showType            显示类型   show显示/edit编辑
     * @return Compontent  前台组件
     * @throws WorkFlowException
     */
    private Compontent createHisCompontent(Map<String, Object> processVariables, HistoricProcessInstance processInstance, HistoricActivityInstance hisActivityInstance, CompontentShowType showType) throws WorkFlowException {
        //若是走线环节即gateWay则返回空，无需封装compontent

        if (ActivitiConstants.parallelGateway.equals(hisActivityInstance.getActivityType())) {
            return null;
        }
        if (ActivitiConstants.inclusiveGateway.equals(hisActivityInstance.getActivityType())) {
            return null;
        }
        if ("exclusiveGateway".equals(hisActivityInstance.getActivityType())) {
            return null;
        }
        if ("serviceTask".equals(hisActivityInstance.getActivityType())) {
            return null;
        }
        if ("endEvent".equals(hisActivityInstance.getActivityType())) {
            return null;
        }
        //获取活动关联的compontent
        Map<String, Object> compontentMap = (Map) processVariables.get(hisActivityInstance.getActivityId());
        if ("startEvent".equals(hisActivityInstance.getActivityType())) {
            compontentMap = (Map) processVariables.get("step1");
        }
        //构造流程启动的compontent
        //根据相关数据区中的主表对应的compontentId进行封装
        if (processVariables.get(ActivitiConstants.compontentId) == null) {
            logger.error("流程实例Id为:" + processInstance.getId() + "相关数据区未存主表compontentId");
            throw new WorkFlowException("1", "流程实例Id为:" + processInstance.getId() + "相关数据区未存主表compontentId");
        }
        if (processVariables.get(ActivitiConstants.compontentTitle) == null) {
            logger.error("流程实例Id为:" + processInstance.getId() + "相关数据区未存主表compontentId");
            throw new WorkFlowException("1", "流程实例Id为:" + processInstance.getId() + "相关数据区未存主表compontentTitle");
        }
        //根据组件类型封装组件
        //Form型
        if (compontentMap == null) {
            logger.error("流程实例关联map为空,参数:{}", processVariables.toString());
        }
        if (compontentMap.get(ActivitiConstants.Form) != null) {
            Map<String, Object> formDataMap = (Map) processVariables.get(compontentMap.get(ActivitiConstants.Form));
            FormProperties formProperties = JSON.parseObject(formDataMap.get(ActivitiConstants.FormProperties)
                    .toString(), FormProperties.class);
            ParseButtonUtil.parseButtonRule(JSONObject.fromObject(securityUserT), JSONObject.fromObject
                    (processVariables), CompontentShowType.SHOW, formProperties);
            Form form = new Form(formProperties.getFormId(), CompontentShowType.SHOW, processInstance.getId(),
                    hisActivityInstance.getActivityId()
                    , formProperties.getCompontentTitle()
                    , formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessId) == null ? null : formDataMap.get(ActivitiConstants.businessId).toString()
                    , formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessClass) == null ? null : formDataMap.get(ActivitiConstants.businessClass).toString()
                    , formProperties.getButtons());
            WorkItem workItem = new WorkItem();
            form.setWorkItem(workItem);
            return form;
        }
        if (compontentMap.get(ActivitiConstants.showForm) != null) {
            Map<String, Object> formDataMap = (Map) processVariables.get(compontentMap.get(ActivitiConstants.showForm));
            FormProperties formProperties = JSON.parseObject(formDataMap.get(ActivitiConstants.FormProperties)
                    .toString(), FormProperties.class);
            ParseButtonUtil.parseButtonRule(JSONObject.fromObject(securityUserT), JSONObject.fromObject
                    (processVariables), CompontentShowType.SHOW, formProperties);
            Form form = new Form(formProperties.getShowFormId(), CompontentShowType.SHOW, processInstance.getId(), hisActivityInstance.getActivityId()
                    , formProperties.getCompontentTitle()
                    , formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessId) == null ? null : formDataMap.get(ActivitiConstants.businessId).toString()
                    , formDataMap == null ? null : formDataMap.get(ActivitiConstants.businessClass) == null ? null : formDataMap.get(ActivitiConstants.businessClass).toString()
                    , formProperties.getButtons());
            WorkItem workItem = new WorkItem();
            form.setWorkItem(workItem);
            return form;
        }
        return null;
    }
}
