package com.github.jyoghurt.common.payment.common.module;

import com.github.jyoghurt.common.payment.common.enums.PaymentPlatFormEnum;

import java.math.BigDecimal;

/**
 * user: dell
 * date:2016/3/18.
 */
public class PaymentRecordResult {
    /**
     * 支付单号
     */
    private String paymentId;
    /**
     * 支付平台
     */
    private PaymentPlatFormEnum paymentPlatForm;
    /**
     * 总金额
     */
    private BigDecimal totleFee;
    /**
     * 支付详情
     */
    private String paymentDetil;
    /**
     * 预支付结果
     */
    private Object prePaymentMsg;
    /**
     * 合并订单数目
     */
    private int orderNum;


    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getTotleFee() {
        return totleFee;
    }

    public void setTotleFee(BigDecimal totleFee) {
        this.totleFee = totleFee;
    }

    public String getPaymentDetil() {
        return paymentDetil;
    }

    public void setPaymentDetil(String paymentDetil) {
        this.paymentDetil = paymentDetil;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public Object getPrePaymentMsg() {
        return prePaymentMsg;
    }

    public void setPrePaymentMsg(Object prePaymentMsg) {
        this.prePaymentMsg = prePaymentMsg;
    }

    public PaymentPlatFormEnum getPaymentPlatForm() {
        return paymentPlatForm;
    }

    public void setPaymentPlatForm(PaymentPlatFormEnum paymentPlatForm) {
        this.paymentPlatForm = paymentPlatForm;
    }

    @Override
    public String toString() {
        return "PaymentRecordResult{" +
                "paymentId='" + paymentId + '\'' +
                ", totleFee=" + totleFee +
                ", paymentDetil='" + paymentDetil + '\'' +
                ", orderNum=" + orderNum +
                '}';
    }
}
