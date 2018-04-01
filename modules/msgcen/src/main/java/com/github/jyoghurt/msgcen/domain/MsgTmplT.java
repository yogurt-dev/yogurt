package com.github.jyoghurt.msgcen.domain;

import com.github.jyoghurt.msgcen.common.enums.MsgTypeEnum;
import com.github.jyoghurt.core.domain.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Transient;


@javax.persistence.Table(name = "MsgTmplT")
public class MsgTmplT extends BaseEntity<MsgTmplT> {
    /**
     * 模板编码
     */
    @javax.persistence.Id
    private String tmplCode;
    /**
     * 模板类型
     */
    private MsgTypeEnum tmplType;
    /**
     * 模板类型
     */
    @Transient
    private String tmplTypeValue;
    /**
     * 模板标题
     */
    private String tmplSubject;

    /**
     * 模板内容{{解析的key}}
     */
    private String tmplContent;
    /**
     * 短信签名
     */
    private String signName;
    /**
     * 模板状态是否启用 0否 1是
     */
    @Column(nullable = false)
    private Boolean tmplStatus;
    @Transient
    private String tmplStatusValue;
    /**
     * 消息模板参数规则
     */
    private String tmplParamRule;

    public String getSignName() {
        return signName;
    }

    public MsgTmplT setSignName(String signName) {
        this.signName = signName;
        return this;
    }

    public MsgTypeEnum getTmplType() {
        return this.tmplType;
    }

    public MsgTmplT setTmplType(MsgTypeEnum tmplType) {
        this.tmplType = tmplType;
        if (null != tmplType) {
            this.setTmplTypeValue(tmplType.getTypeValue());
        }
        return this;
    }

    public String getTmplSubject() {
        return this.tmplSubject;
    }

    public MsgTmplT setTmplSubject(String tmplSubject) {
        this.tmplSubject = tmplSubject;
        return this;
    }

    public String getTmplContent() {
        return this.tmplContent;
    }

    public MsgTmplT setTmplContent(String tmplContent) {
        this.tmplContent = tmplContent;
        return this;
    }

    public String getTmplCode() {
        return tmplCode;
    }

    public MsgTmplT setTmplCode(String tmplCode) {
        this.tmplCode = tmplCode;
        return this;
    }

    public Boolean getTmplStatus() {
        return tmplStatus;
    }

    public MsgTmplT setTmplStatus(Boolean tmplStatus) {
        this.tmplStatus = tmplStatus;
        if(null!=tmplStatus){
            this.setTmplStatusValue(tmplStatus ? "是" : "否");
        }
        return this;
    }

    public String getTmplTypeValue() {
        return tmplTypeValue;
    }

    public void setTmplTypeValue(String tmplTypeValue) {
        this.tmplTypeValue = tmplTypeValue;
    }

    public String getTmplStatusValue() {
        return tmplStatusValue;
    }

    public void setTmplStatusValue(String tmplStatusValue) {
        this.tmplStatusValue = tmplStatusValue;
    }

    public String getTmplParamRule() {
        return tmplParamRule;
    }

    public void setTmplParamRule(String tmplParamRule) {
        this.tmplParamRule = tmplParamRule;
    }
}
