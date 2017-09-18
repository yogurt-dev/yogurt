package com.github.jyoghurt.common.payment.common.module;

import com.github.jyoghurt.common.payment.common.enums.PaymentBusinessTypeEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentResultTypeEnum;

import java.util.List;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * user: dell
 * date:2016/3/14.
 */
public class BaseCallBackParam {
    /**
     * 交易Id
     */
    private String transactionId;
    /**
     * 交易类型
     */
    private PaymentGatewayEnum paymentGatewayEnum;
    /**
     * 交易总金额
     */
    private BigDecimal totalFee;
    /**
     * 交易结果
     */
    private PaymentResultTypeEnum paymentResultTypeEnum;
    /**
     * 支付Id
     */
    private String paymentId;
    /**
     * 模块名称
     */
    private String callBackService;
    /**
     * 业务Id集合
     */
    private List<String> buinessIds;
    /**
     * 业务类型
     */
    private PaymentBusinessTypeEnum paymentBusinessTypeEnum;

    /**
     * 相关数据区
     */
    private Map<String, Object> dataArea = new HashMap<>();

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentGatewayEnum getPaymentGatewayEnum() {
        return paymentGatewayEnum;
    }

    public void setPaymentGatewayEnum(PaymentGatewayEnum paymentGatewayEnum) {
        this.paymentGatewayEnum = paymentGatewayEnum;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentResultTypeEnum getPaymentResultTypeEnum() {
        return paymentResultTypeEnum;
    }

    public void setPaymentResultTypeEnum(PaymentResultTypeEnum paymentResultTypeEnum) {
        this.paymentResultTypeEnum = paymentResultTypeEnum;
    }

    public String getCallBackService() {
        return callBackService;
    }

    public void setCallBackService(String callBackService) {
        this.callBackService = callBackService;
    }

    public Map<String, Object> getDataArea() {
        return dataArea;
    }

    public void setDataArea(Map<String, Object> dataArea) {
        this.dataArea = dataArea;
    }

    public List<String> getBuinessIds() {
        return buinessIds;
    }

    public void setBuinessIds(List<String> buinessIds) {
        this.buinessIds = buinessIds;
    }

    public PaymentBusinessTypeEnum getPaymentBusinessTypeEnum() {
        return paymentBusinessTypeEnum;
    }

    public void setPaymentBusinessTypeEnum(PaymentBusinessTypeEnum paymentBusinessTypeEnum) {
        this.paymentBusinessTypeEnum = paymentBusinessTypeEnum;
    }

    @Override
    public String toString() {
        return "BaseCallBackParam{" +
                "transactionId='" + transactionId + '\'' +
                ", paymentGatewayEnum=" + paymentGatewayEnum +
                ", totalFee=" + totalFee +
                ", paymentResultTypeEnum=" + paymentResultTypeEnum +
                ", paymentId='" + paymentId + '\'' +
                ", callBackService='" + callBackService + '\'' +
                ", buinessIds=" + buinessIds +
                ", paymentBusinessTypeEnum=" + paymentBusinessTypeEnum +
                ", dataArea=" + dataArea +
                '}';
    }
}
