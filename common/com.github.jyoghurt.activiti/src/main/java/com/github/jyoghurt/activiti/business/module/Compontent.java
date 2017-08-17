package com.github.jyoghurt.activiti.business.module;

import com.github.jyoghurt.activiti.business.enums.CompontentShowType;

/**
 * Created by dell on 2016/1/7.
 */
public class Compontent {
    private String compontentId;
    private CompontentShowType showType;
    private String procInstId;
    private String activitiId;
    private String compontentTitle;
    private WorkItem workItem=new WorkItem();

    public WorkItem getWorkItem() {
        return workItem;
    }

    public void setWorkItem(WorkItem workItem) {
        this.workItem = workItem;
    }

    public String getCompontentTitle() {
        return compontentTitle;
    }

    public void setCompontentTitle(String compontentTitle) {
        this.compontentTitle = compontentTitle;
    }

    public CompontentShowType getShowType() {
        return showType;
    }

    public void setShowType(CompontentShowType showType) {
        this.showType = showType;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getActivitiId() {
        return activitiId;
    }

    public void setActivitiId(String activitiId) {
        this.activitiId = activitiId;
    }

    public String getCompontentId() {
        return compontentId;
    }

    public void setCompontentId(String compontentId) {
        this.compontentId = compontentId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Compontent that = (Compontent) o;

        if (compontentId != null ? !compontentId.equals(that.compontentId) : that.compontentId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return compontentId != null ? compontentId.hashCode() : 0;
    }
}
