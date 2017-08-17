package com.github.jyoghurt.security.securityUnitT.domain;

import com.github.jyoghurt.core.domain.BaseEntity;

import javax.persistence.Transient;


@javax.persistence.Table(name = "SecurityUnitT")
public class SecurityUnitT extends BaseEntity<SecurityUnitT> {

    /**
     * 组织机构ID
     */
    @javax.persistence.Id
    private String unitId;
    /**
     *
     */
    private String parentId;
    /**
     * 组织机构名称
     */
    private String unitName;
    /**
     * 组织机构类型 0-代理公司 1-公估公司 2-4S店
     */
    private String type;

    private String appId;

    private String secretKey;


    public Integer getSortId() {
        return sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    private Integer sortId;

    public String getTxstate() {
        return txstate;
    }

    public void setTxstate(String txstate) {
        this.txstate = txstate;
    }

    @Transient
    private String txstate;


    private String appIdAccount;

    private String appIdPwd;

    private String appIdFAccount;

    private String appIdFPwd;

    private String appIdQAccount;

    private String appIdQPwd;

    private String clientId;

    private String clientSecret;

    public String getAppIdAccount() {
        return appIdAccount;
    }

    public void setAppIdAccount(String appIdAccount) {
        this.appIdAccount = appIdAccount;
    }

    public String getAppIdPwd() {
        return appIdPwd;
    }

    public void setAppIdPwd(String appIdPwd) {
        this.appIdPwd = appIdPwd;
    }

    public String getAppIdFAccount() {
        return appIdFAccount;
    }

    public void setAppIdFAccount(String appIdFAccount) {
        this.appIdFAccount = appIdFAccount;
    }

    public String getAppIdFPwd() {
        return appIdFPwd;
    }

    public void setAppIdFPwd(String appIdFPwd) {
        this.appIdFPwd = appIdFPwd;
    }

    public String getAppIdQAccount() {
        return appIdQAccount;
    }

    public void setAppIdQAccount(String appIdQAccount) {
        this.appIdQAccount = appIdQAccount;
    }

    public String getAppIdQPwd() {
        return appIdQPwd;
    }

    public void setAppIdQPwd(String appIdQPwd) {
        this.appIdQPwd = appIdQPwd;
    }

    private String compType;

    public String getCompType() {
        return compType;
    }

    public SecurityUnitT setCompType(String compType) {
        this.compType = compType;
        return this;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * 公估属性1
     */
    private String acol1ColAssessor;
    /**
     * 公估属性2
     */
    private String acol2ColAssessor;
    /**
     * 代理属性1
     */
    private String pcol1ColProxy;
    /**
     * 代理属性2
     */
    private String pcol2ColProxy;
    /**
     * 4s自定义属性1
     */
    private String col1Col4s;
    /**
     * 4s自定义属性2
     */
    private String col2Col4s;

    /**
     * 服务号appIdF
     */
    private String appIdF;

    /**
     * 服务号secretKeyF
     */
    private String secretKeyF;

    /**
     * 企业号 appIdQ
     */
    private String appIdQ;

    public String getSecretKeyQ() {
        return secretKeyQ;
    }

    public void setSecretKeyQ(String secretKeyQ) {
        this.secretKeyQ = secretKeyQ;
    }

    public String getAppIdF() {
        return appIdF;
    }

    public void setAppIdF(String appIdF) {
        this.appIdF = appIdF;
    }

    public String getSecretKeyF() {
        return secretKeyF;
    }

    public void setSecretKeyF(String secretKeyF) {
        this.secretKeyF = secretKeyF;
    }

    public String getAppIdQ() {
        return appIdQ;
    }

    public void setAppIdQ(String appIdQ) {
        this.appIdQ = appIdQ;
    }

    /**
     * 企业号 secretKeyQ
     */
    private String secretKeyQ;

    public String getUnitId() {
        return this.unitId;
    }

    public SecurityUnitT setUnitId(String unitId) {
        this.unitId = unitId;
        return this;
    }

    public String getParentId() {
        return this.parentId;
    }

    public SecurityUnitT setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getUnitName() {
        return this.unitName;
    }

    public SecurityUnitT setUnitName(String unitName) {
        this.unitName = unitName;
        return this;
    }

    public String getType() {
        return this.type;
    }

    public SecurityUnitT setType(String type) {
        this.type = type;
        return this;
    }

    public String getAcol1ColAssessor() {
        return this.acol1ColAssessor;
    }

    public SecurityUnitT setAcol1ColAssessor(String acol1ColAssessor) {
        this.acol1ColAssessor = acol1ColAssessor;
        return this;
    }

    public String getAcol2ColAssessor() {
        return this.acol2ColAssessor;
    }

    public SecurityUnitT setAcol2ColAssessor(String acol2ColAssessor) {
        this.acol2ColAssessor = acol2ColAssessor;
        return this;
    }

    public String getPcol1ColProxy() {
        return this.pcol1ColProxy;
    }

    public SecurityUnitT setPcol1ColProxy(String pcol1ColProxy) {
        this.pcol1ColProxy = pcol1ColProxy;
        return this;
    }

    public String getPcol2ColProxy() {
        return this.pcol2ColProxy;
    }

    public SecurityUnitT setPcol2ColProxy(String pcol2ColProxy) {
        this.pcol2ColProxy = pcol2ColProxy;
        return this;
    }

    public String getCol1Col4s() {
        return this.col1Col4s;
    }

    public SecurityUnitT setCol1Col4s(String col1Col4s) {
        this.col1Col4s = col1Col4s;
        return this;
    }

    public String getCol2Col4s() {
        return this.col2Col4s;
    }

    public SecurityUnitT setCol2Col4s(String col2Col4s) {
        this.col2Col4s = col2Col4s;
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
