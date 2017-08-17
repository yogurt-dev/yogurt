package com.github.jyoghurt.activiti.business.module;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dell on 2016/1/21.
 */
public class WorkItem {
    /**
     * 任务Id
     */
    private String taskId;
    /**
     * 活动定义名称
     */
    private String activitiName;
    /**
     * 待办人
     */
    private String assignee;
    /**
     * 流程实例Id
     */
    private String procInsId;
    /**
     * 任务数据区
     */
    private Map<String,Object> taskLocalVariables=new HashMap<>();
    /**
     * 相关数据区
     */
    private Map<String,Object> processVariables=new HashMap<>();


    public String getActivitiName() {
        return activitiName;
    }

    public void setActivitiName(String activitiName) {
        this.activitiName = activitiName;
    }


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getProcInsId() {
        return procInsId;
    }

    public void setProcInsId(String procInsId) {
        this.procInsId = procInsId;
    }

    public Map<String, Object> getTaskLocalVariables() {
        return taskLocalVariables;
    }

    public void setTaskLocalVariables(Map<String, Object> taskLocalVariables) {
        this.taskLocalVariables=taskLocalVariables;
    }

    public Map<String, Object> getProcessVariables() {
        return processVariables;
    }

    public void setProcessVariables(Map<String, Object> processVariables) {
        this.processVariables = processVariables;
    }
}
