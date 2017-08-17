package com.github.jyoghurt.security.securitySyslogT.domain;

import com.github.jyoghurt.security.securitySyslogT.enums.AccessSystem;
import com.github.jyoghurt.security.securitySyslogT.enums.ClientType;
import com.github.jyoghurt.security.securitySyslogT.enums.SystemType;
import com.github.jyoghurt.core.domain.BaseEntity;


public class SecuritySyslogT extends BaseEntity {

    /**
     * 日志ID
     */
    private String logId;
    /**
     * IP地址
     */
    private String ipAddress;
    /**
     * 模块名称
     */
    private String moduleName;
    /**
     * 状态
     */
    private Boolean state;
    /**
     * 日志内容
     */
    private String LogMessage;
    /**
     * 异常信息
     */
    private String errorMessage;
    /**
     * 类方法名
     */
    private String classMethodName;
    /**
     * 参数列表
     */
    private String methodParameterValues;
    /**
     * 客户端系统类型
     */
    private SystemType systemType;
    /**
     * 客户端类型
     */
    private ClientType clientType;
    /**
     * 调用时长
     */
    private Long invokeDuration;
    /**
     * 访问系统类型
     */
    private AccessSystem accessSystem;

    /**
     * 违法访问判断标识
     */
    private String abnormalLogContent;


    /**
     * 查询条件
     */

    @javax.persistence.Transient
    private String searchCondition;
    /**
     * 时长
     */
    @javax.persistence.Transient
    private String duration;
    /**
     * 查询起始时间
     */
    @javax.persistence.Transient
    private String createDateTime_start;
    /**
     * 查询结束时间
     */
    @javax.persistence.Transient
    private String createDateTime_end;



    public String getLogId() {
        return this.logId;
    }

    public SecuritySyslogT setLogId(String logId) {
        this.logId = logId;
        return this;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public SecuritySyslogT setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public SecuritySyslogT setModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public Boolean getState() {
        return this.state;
    }

    public SecuritySyslogT setState(Boolean state) {
        this.state = state;
        return this;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public SecuritySyslogT setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public String getClassMethodName() {
        return this.classMethodName;
    }

    public SecuritySyslogT setClassMethodName(String classMethodName) {
        this.classMethodName = classMethodName;
        return this;
    }

    public String getMethodParameterValues() {
        return this.methodParameterValues;
    }

    public SecuritySyslogT setMethodParameterValues(String methodParameterValues) {
        this.methodParameterValues = methodParameterValues;
        return this;
    }

    public SystemType getSystemType() {
        return this.systemType;
    }

    public SecuritySyslogT setSystemType(SystemType systemType) {
        this.systemType = systemType;
        return this;
    }

    public ClientType getClientType() {
        return this.clientType;
    }

    public SecuritySyslogT setClientType(ClientType clientType) {
        this.clientType = clientType;
        return this;
    }

    public Long getInvokeDuration() {
        return this.invokeDuration;
    }

    public SecuritySyslogT setInvokeDuration(Long invokeDuration) {
        this.invokeDuration = invokeDuration;
        return this;
    }

    public String getLogMessage() {
        return LogMessage;
    }

    public SecuritySyslogT setLogMessage(String logMessage) {
        LogMessage = logMessage;
        return this;
    }

    public String getAbnormalLogContent() {
        return abnormalLogContent;
    }

    public SecuritySyslogT setAbnormalLogContent(String abnormalLogContent) {
        this.abnormalLogContent = abnormalLogContent;
        return this;
    }

    public AccessSystem getAccessSystem() {
        return accessSystem;
    }

    public SecuritySyslogT setAccessSystem(AccessSystem accessSystem) {
        this.accessSystem = accessSystem;
        return this;
    }

    public String getSearchCondition() {
        return searchCondition;
    }

    public SecuritySyslogT setSearchCondition(String searchCondition) {
        this.searchCondition = searchCondition;
        return this;
    }
    public String getDuration() {
        return duration;
    }

    public SecuritySyslogT setDuration(String duration) {
        this.duration = duration;
        return this;
    }

    public String getCreateDateTime_start() {
        return createDateTime_start;
    }

    public SecuritySyslogT setCreateDateTime_start(String createDateTime_start) {
        this.createDateTime_start = createDateTime_start;
        return this;
    }

    public String getCreateDateTime_end() {
        return createDateTime_end;
    }

    public SecuritySyslogT setCreateDateTime_end(String createDateTime_end) {
        this.createDateTime_end = createDateTime_end;
        return this;
    }
}
