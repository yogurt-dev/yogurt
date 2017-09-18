package com.github.jyoghurt.common.payment.business.alipay.common.trade.model;

import com.google.gson.annotations.SerializedName;

/**
 * user: dell
 * date: 2016/4/19.
 */
public class ExtendParams {
    @SerializedName("sys_service_provider_id")
    private String sysServiceProviderId;
    @SerializedName("service_name")
    private String serviceName;
    public ExtendParams() {
    }

    public String getSysServiceProviderId() {
        return this.sysServiceProviderId;
    }

    public ExtendParams setSysServiceProviderId(String sysServiceProviderId) {
        this.sysServiceProviderId = sysServiceProviderId;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ExtendParams setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    @Override
    public String toString() {
        return "ExtendParams{" +
                "sysServiceProviderId='" + sysServiceProviderId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }
}
