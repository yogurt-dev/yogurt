package com.github.jyoghurt.activiti.business.module;

import com.github.jyoghurt.activiti.business.constants.ActivitiConstants;
import com.github.jyoghurt.activiti.business.enums.CompontentShowType;

import java.util.List;

/**
 * Created by dell on 2016/1/7.
 */
public class Form extends Compontent {

    public Form() {
        this.compontentType = ActivitiConstants.Form;
    }

    public Form(String compontentId, CompontentShowType showType, String procInstId, String acvitiviId, String compontentTitle, String bussinessId, String bussinessClass, List<Button> buttons) {
        this.setCompontentId(compontentId);
        this.setShowType(showType);
        this.setProcInstId(procInstId);
        this.setActivitiId(acvitiviId);
        this.setCompontentTitle(compontentTitle);
        this.bussinessId = bussinessId;
        this.bussinessClass = bussinessClass;
        this.compontentType = ActivitiConstants.Form;
        this.buttons = buttons;
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
    /**
     * 组件按钮
     */
    private List<Button> buttons;

    public List<Button> getButtons() {
        return buttons;
    }

    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }

    public String getBussinessClass() {
        return bussinessClass;
    }

    public void setBussinessClass(String bussinessClass) {
        this.bussinessClass = bussinessClass;
    }

    public String getBussinessId() {
        return bussinessId;
    }

    public void setBussinessId(String bussinessId) {
        this.bussinessId = bussinessId;
    }


    public String getCompontentType() {
        return compontentType;
    }

    public void setCompontentType(String compontentType) {
        this.compontentType = compontentType;
    }
}
