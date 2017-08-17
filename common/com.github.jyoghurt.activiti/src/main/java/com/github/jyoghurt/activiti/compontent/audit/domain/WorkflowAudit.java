package com.github.jyoghurt.activiti.compontent.audit.domain;


import com.github.jyoghurt.activiti.business.annotations.MainFormId;
import com.github.jyoghurt.activiti.business.flowEntity.FlowEntity;
import com.github.jyoghurt.activiti.compontent.audit.enums.AuditEnum;

@javax.persistence.Table(name = "WorkFlowAuditT")
public class WorkflowAudit extends FlowEntity {
    /**
     * 审核主键
     */
    @MainFormId
    @javax.persistence.Id
    private  String auditId;
    /**
     * 被被审核业务主键
     */
    private String auditedId;
    /**
     * 被审核业务实体
     */
    private String auditedClass;
    /**
     * 审核意见
     */
    private String content;
    /**
     * 审核结果
     */
    private AuditEnum result;

    private String column1;

    private String column2;

    private String column3;

    public String getColumn1() {
        return column1;
    }

    public void setColumn1(String column1) {
        this.column1 = column1;
    }

    public String getColumn2() {
        return column2;
    }

    public void setColumn2(String column2) {
        this.column2 = column2;
    }

    public String getColumn3() {
        return column3;
    }

    public void setColumn3(String column3) {
        this.column3 = column3;
    }

    public String getAuditedId() {
        return auditedId;
    }

    public void setAuditedId(String auditedId) {
        this.auditedId = auditedId;
    }

    public String getAuditedClass() {
        return auditedClass;
    }

    public WorkflowAudit setAuditedClass(String auditedClass) {
        this.auditedClass = auditedClass;
        return this;
    }


    public String getContent() {
        return this.content;
    }

    public WorkflowAudit setContent(String content) {
        this.content = content;
        return this;
    }

    public AuditEnum getResult() {
        return result;
    }

    public void setResult(AuditEnum result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkflowAudit that = (WorkflowAudit) o;

        return column1 != null ? column1.equals(that.column1) : that.column1 == null;

    }

    @Override
    public int hashCode() {
        return column1 != null ? column1.hashCode() : 0;
    }
}
