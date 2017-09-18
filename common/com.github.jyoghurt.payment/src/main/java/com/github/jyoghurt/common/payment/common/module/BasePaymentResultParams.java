package com.github.jyoghurt.common.payment.common.module;

import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentStateEnum;

/**
 * user: dell
 * date:2016/3/13.
 */
public class BasePaymentResultParams {
    /**
     * 交易Id
     */
    private String transactionId;
    /**
     * 业务Id
     */
    private String businessId;
    /**
     * 支付结果
     */
    private String  paymentResultCode;
    /**
     * 支付状态
     */
    private PaymentStateEnum paymentState;
    /**
     * 支付接口
     */
    private PaymentGatewayEnum paymentCategory;
    /**
     * 返回信息
     */
    private String responseStr;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getPaymentResultCode() {
        return paymentResultCode;
    }

    public void setPaymentResultCode(String paymentResultCode) {
        this.paymentResultCode = paymentResultCode;
    }

    public PaymentGatewayEnum getPaymentCategory() {
        return paymentCategory;
    }

    public void setPaymentCategory(PaymentGatewayEnum paymentCategory) {
        this.paymentCategory = paymentCategory;
    }

    public String getResponseStr() {
        return responseStr;
    }

    public void setResponseStr(String responseStr) {
        this.responseStr = responseStr;
    }

    public PaymentStateEnum getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(PaymentStateEnum paymentState) {
        this.paymentState = paymentState;
    }
}
