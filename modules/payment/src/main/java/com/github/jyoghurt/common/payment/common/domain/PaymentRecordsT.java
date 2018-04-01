package com.github.jyoghurt.common.payment.common.domain;

import com.alibaba.fastjson.JSONObject;
import com.github.jyoghurt.common.payment.common.enums.PaymentBusinessTypeEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@javax.persistence.Table(name = "PaymentRecordsT")
public class PaymentRecordsT extends BasePaymentParamsEntity<PaymentRecordsT> {
    /**
     * 支付单号
     */
    @javax.persistence.Id
    private String paymentId;
    /**
     * 交易Id
     */
    private String transactionId;
    /**
     * 支付状态 0：未支付 1：已支付
     */
    private Boolean paymentState;
    /**
     * 总金额
     */
    private BigDecimal totleFee;
    /**
     * 预支付Id
     */
    private String payperId;
    /**
     * 支付方式
     */
    private PaymentGatewayEnum paymentMethod;
    /**
     * 支付详情
     */
    private String paymentDetil;
    /**
     * 支付业务类型
     */
    private PaymentBusinessTypeEnum paymentBusinessType;
    /**
     * 接受回调状态
     */
    private Boolean responseState;
    /**
     * 业务数量  因为合并所以此处记录数量
     */
    private Integer bussinessNum;
    /**
     * 相关数据区
     */
    private String dataArea;
    /**
     * 已退款金额
     */
    private BigDecimal refundedMoney;

    public int getBussinessNum() {
        return bussinessNum;
    }

    public void setBussinessNum(int bussinessNum) {
        this.bussinessNum = bussinessNum;
    }

    public Boolean getResponseState() {
        return responseState;
    }

    public void setResponseState(Boolean responseState) {
        this.responseState = responseState;
    }

    public PaymentGatewayEnum getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentDetil() {
        return paymentDetil;
    }

    public PaymentRecordsT setPaymentDetil(String paymentDetil) {
        this.paymentDetil = paymentDetil;
        return this;
    }

    public PaymentBusinessTypeEnum getPaymentBusinessType() {
        return paymentBusinessType;
    }

    public void setPaymentBusinessType(PaymentBusinessTypeEnum paymentBusinessType) {
        this.paymentBusinessType = paymentBusinessType;
    }


    public String getPaymentId() {
        return this.paymentId;
    }

    public PaymentRecordsT setPaymentId(String paymentId) {
        this.paymentId = paymentId;
        return this;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public PaymentRecordsT setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public Boolean getPaymentState() {
        return this.paymentState;
    }

    public PaymentRecordsT setPaymentState(Boolean paymentState) {
        this.paymentState = paymentState;
        return this;
    }

    public BigDecimal getTotleFee() {
        return totleFee;
    }

    public PaymentRecordsT setTotleFee(BigDecimal totleFee) {
        this.totleFee = totleFee;
        return this;
    }

    public String getPayperId() {
        return this.payperId;
    }

    public PaymentRecordsT setPayperId(String payperId) {
        this.payperId = payperId;
        return this;
    }

    public PaymentRecordsT setPaymentMethod(PaymentGatewayEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public String getDataArea() {
        return dataArea;
    }

    public void setDataArea(String dataArea) {
        this.dataArea = dataArea;
    }

    public Map<String, Object> getDataAreaMap() {
        if (StringUtils.isEmpty(this.dataArea)) {
            return new HashMap<>();
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> dataArea = new HashMap<>();
        try {
            dataArea = mapper.readValue(this.dataArea, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataArea;
    }

    public void setDataAreaMap(String key, Object value) {
        Map<String, Object> vars = null;
        try {
            vars = this.getDataAreaMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        vars.put(key, value);
        this.dataArea = JSONObject.toJSONString(vars);
    }

    public void setDataAreaMap(Map<String, Object> vars) {
        this.dataArea = JSONObject.toJSONString(vars);
    }

    public void setBussinessNum(Integer bussinessNum) {
        this.bussinessNum = bussinessNum;
    }

    public BigDecimal getRefundedMoney() {
        return refundedMoney;
    }

    public void setRefundedMoney(BigDecimal refundedMoney) {
        this.refundedMoney = refundedMoney;
    }

    @Override
    public String toString() {
        return "PaymentRecordsT{" +
                "paymentId='" + paymentId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", paymentState=" + paymentState +
                ", totleFee=" + totleFee +
                ", payperId='" + payperId + '\'' +
                ", paymentMethod=" + paymentMethod +
                ", paymentDetil='" + paymentDetil + '\'' +
                ", paymentBusinessType=" + paymentBusinessType +
                ", responseState=" + responseState +
                ", bussinessNum=" + bussinessNum +
                '}';
    }
}
