package com.github.jyoghurt.activiti.adapter.service.impl;

import com.github.jyoghurt.activiti.adapter.service.ActivitiService;
import com.github.jyoghurt.activiti.adapter.util.TransFormUtil;
import com.github.jyoghurt.activiti.business.constants.ActivitiConstants;
import com.github.jyoghurt.activiti.business.module.Button;
import com.github.jyoghurt.activiti.business.module.FormProperties;
import com.github.jyoghurt.core.exception.BaseErrorException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjl on 2015/12/30.
 */
@Service("activitiService")
public class ActivitiServiceImpl implements ActivitiService {
    private static Logger logger = LoggerFactory.getLogger(ActivitiServiceImpl.class);

    /*注册activiti服务接口*/
    @Autowired
    private RuntimeService runtimeService;//启动流程接口
    @Autowired
    private TaskService taskService;//任务的相关业务接口
    @Autowired
    protected RepositoryService repositoryService;

    /**
     * 根据任务ID获得任务实例
     *
     * @param taskId 任务ID
     * @return
     * @
     */
    private TaskEntity findTaskById(String taskId) {
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
        return task;
    }

    /**
     * 根据任务ID获取流程定义
     *
     * @param taskId 任务ID
     * @return
     * @
     */
    private ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(String taskId) {
        // 取得流程定义
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(findTaskById(taskId).getProcessDefinitionId());
        if (processDefinition == null) {
            throw new BaseErrorException("流程定义未找到!");
        }
        return processDefinition;
    }

    /**
     * 根据任务ID和节点ID获取活动实例
     *
     * @param taskId     任务ID
     * @param activityId 活动节点ID 如果为null或""，则默认查询当前活动节点 如果为"end"，则查询结束节点 <br>
     * @return
     * @
     */
    public ActivityImpl findActivitiImpl(String taskId, String activityId) {
        ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);
        // 获取当前活动节点ID
        if (StringUtils.isEmpty(activityId)) {
            activityId = findTaskById(taskId).getTaskDefinitionKey();
        }
        // 根据流程定义，获取该流程实例的结束节点
        if (activityId.toUpperCase().equals("END")) {
            for (ActivityImpl activityImpl : processDefinition.getActivities()) {
                List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
                if (pvmTransitionList.isEmpty()) {
                    return activityImpl;
                }
            }
        }
        return ((ProcessDefinitionImpl) processDefinition).findActivity(activityId);
    }

    /**
     * 根据当前任务的表单数据获取当前任务的表单属性
     *
     * @param taskFormData 当前任务的表单数据
     * @return FormProperties   表单属性
     */
    public FormProperties findFormProperties(TaskFormData taskFormData) throws UnsupportedEncodingException {//todo
        FormProperties formProperties = new FormProperties();
        List<Button> buttons = new ArrayList<>();
        for (FormProperty formProperty : taskFormData.getFormProperties()) {
            if (ActivitiConstants.Form.equals(formProperty.getId())) {
                formProperties.setFormId(formProperty.getName());
                continue;
            }
            if (ActivitiConstants.Button.equals(formProperty.getId())) {
                formProperties.setLinkId(formProperty.getName());
                formProperties.setLinkType(ActivitiConstants.Button);
                continue;
            }
            if (ActivitiConstants.editForm.equals(formProperty.getId())) {
                formProperties.setEditFormId(formProperty.getName());
                continue;
            }
            if (ActivitiConstants.showForm.equals(formProperty.getId())) {
                formProperties.setShowFormId(formProperty.getName());
                continue;
            }
            if (ActivitiConstants.compontentTitle.equals(formProperty.getId())) {
                String compontentTitle = TransFormUtil.transFormCompontentTitle(formProperty);
                if (!StringUtils.isEmpty(compontentTitle)) {
                    formProperties.setCompontentTitle(compontentTitle);
                }
                continue;
            }
            if (ActivitiConstants.state.equals(formProperty.getId())) {
                formProperties.setWorkFlowState(formProperty.getName());
                continue;
            }
            if (formProperty.getId().endsWith(ActivitiConstants.Btn)) {
                Button button = new Button();
                button.setId(formProperty.getId());
                button.setBtnRule(formProperty.getName());
                buttons.add(button);
                formProperties.setButtons(buttons);
            }
        }
        return formProperties;
    }

    /**
     * 根据当前任务的表单数据获取当前任务的表单属性
     *
     * @param startFormData 启动步骤的表单数据
     * @return FormProperties   表单属性
     */
    public FormProperties findFormProperties(StartFormData startFormData) throws UnsupportedEncodingException {
        FormProperties formProperties = new FormProperties();
        for (FormProperty formProperty : startFormData.getFormProperties()) {
            if (ActivitiConstants.Form.equals(formProperty.getId())) {
                formProperties.setFormId(formProperty.getName());
                continue;
            }
            if (ActivitiConstants.editForm.equals(formProperty.getId())) {
                formProperties.setEditFormId(formProperty.getName());
                continue;
            }
            if (ActivitiConstants.showForm.equals(formProperty.getId())) {
                formProperties.setShowFormId(formProperty.getName());
                continue;
            }
            if (ActivitiConstants.compontentTitle.equals(formProperty.getId())) {
                String compontentTitle = TransFormUtil.transFormCompontentTitle(formProperty);
                if (!StringUtils.isEmpty(compontentTitle)) {
                    formProperties.setCompontentTitle(compontentTitle);
                }
                continue;
            }
            if (ActivitiConstants.state.equals(formProperty.getId())) {
                formProperties.setWorkFlowState(formProperty.getName());
                continue;
            }
            if (ActivitiConstants.activitiId.equals(formProperty.getId())) {
                formProperties.setActivitiId(formProperty.getName());
                continue;
            }
        }
        return formProperties;
    }
}
