package com.github.jyoghurt.common.payment.common.domain;


import com.github.jyoghurt.core.domain.BaseEntity;

/**
 * user: dell
 * date:2016/3/14.
 */
public class BasePaymentEntity<R extends BasePaymentEntity<R>>  extends BaseEntity<R> {
    /**
     * 支付订单Id
     */
    private String paymentId;

    public String getPaymentId() {
        return paymentId;
    }

    public BasePaymentEntity setPaymentId(String paymentId) {
        this.paymentId = paymentId;
        return this;
    }
}
