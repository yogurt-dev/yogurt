package com.github.jyoghurt.activiti.business.vo;

import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;

/**
 * Created by dell on 2016/1/7.
 */
public class WorkFlowTodoSearchVo {
    private String procDefKey;
    private String workOrderNumber;
    private String subject;
    private String state;
    private String createTime_start;
    private String createTime_end;
    private SecurityUserT securityUserT;

    public SecurityUserT getSecurityUserT() {
        return securityUserT;
    }

    public void setSecurityUserT(SecurityUserT securityUserT) {
        this.securityUserT = securityUserT;
    }

    public String getProcDefKey() {
        return procDefKey;
    }

    public void setProcDefKey(String procDefKey) {
        this.procDefKey = procDefKey;
    }

    public String getWorkOrderNumber() {
        return workOrderNumber;
    }

    public void setWorkOrderNumber(String workOrderNumber) {
        this.workOrderNumber = workOrderNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreateTime_start() {
        return createTime_start;
    }

    public void setCreateTime_start(String createTime_start) {
        this.createTime_start = createTime_start;
    }

    public String getCreateTime_end() {
        return createTime_end;
    }

    public void setCreateTime_end(String createTime_end) {
        this.createTime_end = createTime_end;
    }
}
