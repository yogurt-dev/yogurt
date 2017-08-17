package com.github.jyoghurt.activiti.business.flowEntity;

import com.github.jyoghurt.core.domain.BaseEntity;

/**
 * Created by dell on 2016/1/4.
 */
@javax.persistence.Table(name = "WorkFlowHisRelevanceR")
public class WorkFlowHisRelevance extends BaseEntity {
    @javax.persistence.Id
    /**
     * 主键
     */
    private String hisId;
    private String businessKey;/*业务主键*/
    /*流程定义名称*/
    private String procKey;
    /*流程实例Id*/
    private String procInsId;
    /*活动实例Id*/
    private String activitiId;
    /*任务实例Id*/
    private String taskId;

    public String getHisId() {
        return hisId;
    }

    public void setHisId(String hisId) {
        this.hisId = hisId;
    }

    public String getProcKey() {
        return procKey;
    }

    public void setProcKey(String procKey) {
        this.procKey = procKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getProcInsId() {
        return procInsId;
    }

    public void setProcInsId(String procInsId) {
        this.procInsId = procInsId;
    }

    public String getActivitiId() {
        return activitiId;
    }

    public void setActivitiId(String activitiId) {
        this.activitiId = activitiId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
