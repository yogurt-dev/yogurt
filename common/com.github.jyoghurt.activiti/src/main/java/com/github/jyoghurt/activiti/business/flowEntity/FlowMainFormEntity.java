package com.github.jyoghurt.activiti.business.flowEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by dell on 2016/1/13.
 */
public class FlowMainFormEntity<R extends FlowMainFormEntity<R>> extends FlowEntity<R> {
    /**
     * 工单主题
     */
    private String subject;
    /**
     * 工单编号
     */
    private String workOrderNumber;
    /*流程实例Id*/
    private String proInsId;
    /*流程定义名称*/
    private String procKey;
    private Date archiveTime;
    /*主表compontentId*/
    @Transient
    private String compontentId;
    /**
     * 主表组件标题
     */
    @Transient
    private String compontentTitle;
    /**
     * 流程定义Id
     */
    @Transient
    private String processDefinitionId;
    /**
     * 主流程状态
     */
    private String state;

    public String getState() {
        return state;
    }

    public FlowMainFormEntity setState(String state) {
        this.state = state;
        return this;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getArchiveTime() {
        return archiveTime;
    }

    public void setArchiveTime(Date archiveTime) {
        this.archiveTime = archiveTime;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getWorkOrderNumber() {
        return workOrderNumber;
    }

    public void setWorkOrderNumber(String workOrderNumber) {
        this.workOrderNumber = workOrderNumber;
    }

    public String getProInsId() {
        return proInsId;
    }

    public void setProInsId(String proInsId) {
        this.proInsId = proInsId;
    }

    public String getProcKey() {
        return procKey;
    }

    public void setProcKey(String procKey) {
        this.procKey = procKey;
    }

    public String getCompontentId() {
        return compontentId;
    }

    public void setCompontentId(String compontentId) {
        this.compontentId = compontentId;
    }

    public String getCompontentTitle() {
        return compontentTitle;
    }

    public void setCompontentTitle(String compontentTitle) {
        this.compontentTitle = compontentTitle;
    }
}
