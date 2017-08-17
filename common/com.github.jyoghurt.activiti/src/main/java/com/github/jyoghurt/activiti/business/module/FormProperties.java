package com.github.jyoghurt.activiti.business.module;

import java.io.UnsupportedEncodingException;
import java.util.List;


public class FormProperties {
    private String formId;
    private String linkId;
    private String linkType;
    private String editFormId;
    private String showFormId;
    private String compontentTitle;
    private String workFlowState;
    private String activitiId;
    private List<Button> buttons;

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getActivitiId() {
        return activitiId;
    }

    public void setActivitiId(String activitiId) {
        this.activitiId = activitiId;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getEditFormId() {
        return editFormId;
    }

    public void setEditFormId(String editFormId) {
        this.editFormId = editFormId;
    }

    public String getShowFormId() {
        return showFormId;
    }

    public void setShowFormId(String showFormId) {
        this.showFormId = showFormId;
    }

    public String getCompontentTitle() {
        return compontentTitle;
    }

    public void setCompontentTitle(String compontentTitle) throws UnsupportedEncodingException {
        this.compontentTitle = compontentTitle;
    }

    public String getWorkFlowState() {
        return workFlowState;
    }

    public void setWorkFlowState(String workFlowState) {
        this.workFlowState = workFlowState;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }

    @Override
    public String toString() {
        return "FormProperties{" +
                "formId='" + formId + '\'' +
                ", editFormId='" + editFormId + '\'' +
                ", showFormId='" + showFormId + '\'' +
                ", compontentTitle='" + compontentTitle + '\'' +
                ", workFlowState='" + workFlowState + '\'' +
                ", activitiId='" + activitiId + '\'' +
                '}';
    }
}
