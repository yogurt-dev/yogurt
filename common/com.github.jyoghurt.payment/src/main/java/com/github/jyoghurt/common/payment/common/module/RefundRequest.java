package com.github.jyoghurt.common.payment.common.module;

import com.github.jyoghurt.common.payment.common.enums.PaymentBusinessTypeEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * user:zjl
 * date: 2016/12/23.
 */
public class RefundRequest {
    /**
     * 退款单号 每个退款单号只能申请一次退款 对于业务来讲相当于退款业务的主键
     */
    private String outRequestNo;
    /**
     * 对应支付记录的业务Id
     */
    private String businessId;
    /**
     * 退款业务类型
     */
    private PaymentBusinessTypeEnum paymentBusinessType;
    /**
     * 退款金额
     */
    private BigDecimal RefundAmount;
    /**
     * 退款原因
     */
    private String refundPaymentReason;

    /**
     * 当前操作人Id
     */
    private String operatorId;

    public String getOutRequestNo() {
        return outRequestNo;
    }

    public RefundRequest setOutRequestNo(String outRequestNo) {
        this.outRequestNo = outRequestNo;
        return this;
    }

    public String getBusinessId() {
        return businessId;
    }

    public RefundRequest setBusinessId(String businessId) {
        this.businessId = businessId;
        return this;
    }

    public PaymentBusinessTypeEnum getPaymentBusinessType() {
        return paymentBusinessType;
    }

    public RefundRequest setPaymentBusinessType(PaymentBusinessTypeEnum paymentBusinessType) {
        this.paymentBusinessType = paymentBusinessType;
        return this;
    }

    public String getRefundPaymentReason() {
        return refundPaymentReason;
    }

    public RefundRequest setRefundPaymentReason(String refundPaymentReason) {
        this.refundPaymentReason = refundPaymentReason;
        return this;
    }

    public BigDecimal getRefundAmount() {
        return RefundAmount;
    }

    public RefundRequest setRefundAmount(BigDecimal refundAmount) {
        RefundAmount = refundAmount.setScale(2, RoundingMode.DOWN);
        return this;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public RefundRequest setOperatorId(String operatorId) {
        this.operatorId = operatorId;
        return this;
    }

    @Override
    public String toString() {
        return "RefundRequest{" +
                "outRequestNo='" + outRequestNo + '\'' +
                ", businessId='" + businessId + '\'' +
                ", paymentBusinessType=" + paymentBusinessType +
                ", RefundAmount=" + RefundAmount +
                ", refundPaymentReason='" + refundPaymentReason + '\'' +
                ", operatorId='" + operatorId + '\'' +
                '}';
    }
}
