package com.github.jyoghurt.security.securityUserT.domain;

import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUserResourceR.domain.SecurityUserResourceR;
import com.github.jyoghurt.security.securityUserUnitR.domain.SecurityUserUnitR;
import com.github.jyoghurt.core.domain.BaseEntity;
import com.github.jyoghurt.security.securityRoleT.domain.SecurityRoleT;

import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;


//@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@javax.persistence.Table(name = "SecurityUserT")
public class SecurityUserT extends BaseEntity<SecurityUserT> {

    private static final long serialVersionUID = -56879955677907845L;

    /**
     * 用户ID
     */
    @javax.persistence.Id
    private String userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 联系方式
     */
    private String linkWay;
    /**
     * 账号
     */
    private String userAccount;
    /**
     *
     */
    private String passwd;
    /**
     * 所属单位
     */
    private String belongOrg;

    /**
     * 编号
     */
    private String extId;

    /**
     * 别名
     */
    private String slaveList;

    /**
     * 微信用户唯一标识
     */
    private String open_id;

    /**
     * 登录校验码
     */
    private String loginVerification;

    public String getTxstate() {
        return txstate;
    }

    public void setTxstate(String txstate) {
        this.txstate = txstate;
    }

    @Transient
    private String txstate;

    /**
     * 用户包含的资源
     */
    @Transient
    @OneToMany
    private List<SecurityUserResourceR> securityUserResourceRs;

    /**
     * 用户管辖的单位
     */
    @Transient
    @OneToMany
    private List<SecurityUserUnitR> securityUserUnitRs;

    public List<SecurityUserResourceR> getSecurityUserResourceRs() {
        return securityUserResourceRs;
    }

    public void setSecurityUserResourceRs(List<SecurityUserResourceR> securityUserResourceRs) {
        this.securityUserResourceRs = securityUserResourceRs;
    }

    /**
     * 当前用户所属单位信息
     */
    @Transient
    private SecurityUnitT belongCompany;

    /**
     * 职位
     */
    private String position;

    public String getSlaveList() {
        return slaveList;
    }

    public void setSlaveList(String slaveList) {
        this.slaveList = slaveList;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }

    public String getExtId() {

        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 头像
     */
    private String portrait;

    /**
     * 成员状态：1=启用，2=禁用
     */
    private String openType;

    @Transient
    private String belongResources;
    @Transient
    private String belongResourcesName;

    private String belongOrgName;

    public String getEmailAddr() {
        return emailAddr;
    }

    public void setEmailAddr(String emailAddr) {
        this.emailAddr = emailAddr;
    }

    private String emailAddr;


    public String getBelongOrgName() {
        return belongOrgName;
    }

    public void setBelongOrgName(String belongOrgName) {
        this.belongOrgName = belongOrgName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public List<SecurityRoleT> getRoles() {
        return roles;
    }

    public void setRoles(List<SecurityRoleT> roles) {
        this.roles = roles;
    }

    @Transient
    private List<SecurityRoleT> roles;

    public String getUserId() {
        return this.userId;
    }

    public SecurityUserT setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return this.userName;
    }

    public SecurityUserT setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getLinkWay() {
        return this.linkWay;
    }

    public SecurityUserT setLinkWay(String linkWay) {
        this.linkWay = linkWay;
        return this;
    }

    public String getUserAccount() {
        return this.userAccount;
    }

    public SecurityUserT setUserAccount(String userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    public String getPasswd() {
        return this.passwd;
    }

    public SecurityUserT setPasswd(String passwd) {
        this.passwd = passwd;
        return this;
    }

    public List<SecurityUserUnitR> getSecurityUserUnitRs() {
        return securityUserUnitRs;
    }

    public void setSecurityUserUnitRs(List<SecurityUserUnitR> securityUserUnitRs) {
        this.securityUserUnitRs = securityUserUnitRs;
    }

    public String getBelongOrg() {
        return this.belongOrg;
    }

    public SecurityUserT setBelongOrg(String belongOrg) {
        this.belongOrg = belongOrg;
        return this;
    }

    public SecurityUnitT getBelongCompany() {
        return belongCompany;
    }

    public void setBelongCompany(SecurityUnitT belongCompany) {
        this.belongCompany = belongCompany;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getBelongResources() {
        return belongResources;
    }

    public void setBelongResources(String belongResources) {
        this.belongResources = belongResources;
    }

    public String getBelongResourcesName() {
        return belongResourcesName;
    }

    public void setBelongResourcesName(String belongResourcesName) {
        this.belongResourcesName = belongResourcesName;
    }

    public String getLoginVerification() {
        return loginVerification;
    }

    public SecurityUserT setLoginVerification(String loginVerification) {
        this.loginVerification = loginVerification;
        return this;
    }

    public String getOpen_id() {
        return open_id;
    }

    public SecurityUserT setOpen_id(String open_id) {
        this.open_id = open_id;
        return this;
    }
}
