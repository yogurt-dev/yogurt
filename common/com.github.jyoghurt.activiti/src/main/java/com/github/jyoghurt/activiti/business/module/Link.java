package com.github.jyoghurt.activiti.business.module;

import com.github.jyoghurt.activiti.business.enums.CompontentShowType;

/**
 * Created by dell on 2016/1/7.
 */
public class Link extends Compontent {

    public Link(String compontentId, CompontentShowType showType, String procInstId, String acvitiviId, String compontentTitle, String bussinessId, String bussinessClass,String linkType) {
        this.setCompontentId(compontentId);
        this.setShowType(showType);
        this.setProcInstId(procInstId);
        this.setActivitiId(acvitiviId);
        this.setCompontentTitle(compontentTitle);
        this.bussinessId = bussinessId;
        this.bussinessClass = bussinessClass;
        this.compontentType = linkType;
    }

    /**
     * Form对应的业务主键
     */
    private String bussinessId;
    /**
     * Form对应的业务实体
     */
    private String bussinessClass;
    /**
     * 组件类型
     */
    private String compontentType;

    public String getBussinessId() {
        return bussinessId;
    }

    public void setBussinessId(String bussinessId) {
        this.bussinessId = bussinessId;
    }

    public String getBussinessClass() {
        return bussinessClass;
    }

    public void setBussinessClass(String bussinessClass) {
        this.bussinessClass = bussinessClass;
    }

    public String getCompontentType() {
        return compontentType;
    }

    public void setCompontentType(String compontentType) {
        this.compontentType = compontentType;
    }
}
