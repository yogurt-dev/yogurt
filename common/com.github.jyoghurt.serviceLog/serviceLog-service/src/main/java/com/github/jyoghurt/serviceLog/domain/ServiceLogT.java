package com.github.jyoghurt.serviceLog.domain;

import com.github.jyoghurt.core.domain.BaseEntity;
import com.github.jyoghurt.serviceLog.enums.ServiceTypeEnum;


@javax.persistence.Table(name = "ServiceLogT")
public class ServiceLogT extends BaseEntity<ServiceLogT> {
    @javax.persistence.Id
    private String serviceLogId;
    /**
     * 接口类型
     */
    private ServiceTypeEnum serviceType;
    /**
     * 调用类型 0：为本地调用服务 1：为服务调用本地
     */
    private Boolean callType;
    /**
     * 调用内容
     */
    private String serviceContent;
    /**
     * 模块名称
     */
    private String moduleName;
    /**
     * 接口名称
     */
    private String serviceName;

    public String getServiceLogId() {
        return serviceLogId;
    }

    public ServiceLogT setServiceLogId(String serviceLogId) {
        this.serviceLogId = serviceLogId;
        return this;
    }

    public ServiceTypeEnum getServiceType() {
        return this.serviceType;
    }

    public ServiceLogT setServiceType(ServiceTypeEnum serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    public Boolean getCallType() {
        return this.callType;
    }

    public ServiceLogT setCallType(Boolean callType) {
        this.callType = callType;
        return this;
    }

    public String getServiceContent() {
        return this.serviceContent;
    }

    public ServiceLogT setServiceContent(String serviceContent) {
        this.serviceContent = serviceContent;
        return this;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public ServiceLogT setModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public ServiceLogT setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }
}
