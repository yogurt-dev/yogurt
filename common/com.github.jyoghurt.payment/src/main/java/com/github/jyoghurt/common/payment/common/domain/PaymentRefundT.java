package com.github.jyoghurt.common.payment.common.domain;

import com.github.jyoghurt.common.payment.common.enums.PaymentBusinessTypeEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.core.domain.BaseEntity;

import java.math.BigDecimal;


@javax.persistence.Table(name = "PaymentRefundT")
public class PaymentRefundT extends BaseEntity<PaymentRefundT> {
    /**
     * 退款单号 每个退款单号只能申请一次退款 对于业务来讲相当于退款业务的主键
     */
    @javax.persistence.Id
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
     * 退款方式
     */
    private PaymentGatewayEnum refundPaymentGateway;
    /**
     * 退款状态 0 失败 1成功
     */
    private Boolean refundPaymentState;
    /**
     * 退款原因
     */
    private String refundPaymentReason;
    /**
     * 退款金额
     */
    private BigDecimal refundedMoney;

    public String getOutRequestNo() {
        return this.outRequestNo;
    }

    public PaymentRefundT setOutRequestNo(String outRequestNo) {
        this.outRequestNo = outRequestNo;
        return this;
    }

    public String getBusinessId() {
        return this.businessId;
    }

    public PaymentRefundT setBusinessId(String businessId) {
        this.businessId = businessId;
        return this;
    }

    public PaymentBusinessTypeEnum getPaymentBusinessType() {
        return this.paymentBusinessType;
    }

    public PaymentRefundT setPaymentBusinessType(PaymentBusinessTypeEnum paymentBusinessType) {
        this.paymentBusinessType = paymentBusinessType;
        return this;
    }

    public PaymentGatewayEnum getRefundPaymentGateway() {
        return this.refundPaymentGateway;
    }

    public PaymentRefundT setRefundPaymentGateway(PaymentGatewayEnum refundPaymentGateway) {
        this.refundPaymentGateway = refundPaymentGateway;
        return this;
    }

    public Boolean getRefundPaymentState() {
        return this.refundPaymentState;
    }

    public PaymentRefundT setRefundPaymentState(Boolean refundPaymentState) {
        this.refundPaymentState = refundPaymentState;
        return this;
    }

    public String getRefundPaymentReason() {
        return this.refundPaymentReason;
    }

    public PaymentRefundT setRefundPaymentReason(String refundPaymentReason) {
        this.refundPaymentReason = refundPaymentReason;
        return this;
    }

    public BigDecimal getRefundedMoney() {
        return refundedMoney;
    }

    public PaymentRefundT setRefundedMoney(BigDecimal refundedMoney) {
        this.refundedMoney = refundedMoney;
        return this;
    }
}
