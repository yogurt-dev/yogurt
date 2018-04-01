package com.github.jyoghurt.msgcen.domain;

import com.github.jyoghurt.core.domain.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Transient;


@javax.persistence.Table(name = "MsgTirggerT")
public class MsgTirggerT extends BaseEntity<MsgTirggerT> {

    /**
     * 触发器编码
     */
    @javax.persistence.Id
    private String tirggerCode;
    /**
     * 触发器标题
     */
    private String tirggerSubject;
    /**
     * 触发器规则
     */
    private String tirggerRule;
    /**
     * 触发器是否启用 0否 1是
     */
    @Column(nullable = false)
    private Boolean tirggerStatus;
    @Transient
    private String tirggerStatusValue;

    public String getTirggerCode() {
        return this.tirggerCode;
    }

    public MsgTirggerT setTirggerCode(String tirggerCode) {
        this.tirggerCode = tirggerCode;
        return this;
    }

    public String getTirggerSubject() {
        return this.tirggerSubject;
    }

    public MsgTirggerT setTirggerSubject(String tirggerSubject) {
        this.tirggerSubject = tirggerSubject;
        return this;
    }

    public String getTirggerRule() {
        return this.tirggerRule;
    }

    public MsgTirggerT setTirggerRule(String tirggerRule) {
        this.tirggerRule = tirggerRule;
        return this;
    }

    public Boolean getTirggerStatus() {
        return this.tirggerStatus;
    }

    public MsgTirggerT setTirggerStatus(Boolean tirggerStatus) {
        this.tirggerStatus = tirggerStatus;
        if (null != tirggerStatus) {
            this.setTirggerStatusValue(tirggerStatus ? "是" : "否");
        }
        return this;
    }

    public String getTirggerStatusValue() {
        return tirggerStatusValue;
    }

    public void setTirggerStatusValue(String tirggerStatusValue) {
        this.tirggerStatusValue = tirggerStatusValue;
    }
}
