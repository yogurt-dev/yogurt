package com.github.jyoghurt.common.payment.common.enums;

/**
 * user: dell
 * date:2016/3/17.
 */
public enum PaymentBusinessTypeEnum {
    ORDER("orderClientService"), //订单模块 ;
    RECHARGE("memberRechargeService");//账户充值


    private String serviceName;


    PaymentBusinessTypeEnum(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

}
