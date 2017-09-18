package com.github.jyoghurt.common.payment.business.cash.service;

import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.enums.PaymentStateEnum;
import com.github.jyoghurt.common.payment.common.exception.CashPayVerifyException;
import com.github.jyoghurt.common.payment.common.exception.PaymentClosedException;
import com.github.jyoghurt.common.payment.common.exception.PaymentRefundedException;
import com.github.jyoghurt.common.payment.common.exception.PaymentRepeatException;

import java.math.BigDecimal;


/**
 * user:dell
 * data:2016/4/27.
 */
public interface CashPayService {
    /**
     * 现金支付
     *
     * @param paymentId     支付Id
     * @param paymentAmount 支付金额
     * @return 支付状态
     * @throws CashPayVerifyException
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     */
    PaymentStateEnum cashPay(String paymentId, BigDecimal paymentAmount) throws CashPayVerifyException, PaymentClosedException, PaymentRepeatException, PaymentRefundedException;

    /**
     * 现金支付
     *
     * @param paymentRecordsT 支付记录
     * @param paymentAmount   支付金额
     * @return 支付状态
     * @throws CashPayVerifyException
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     */
    PaymentStateEnum cashPay(PaymentRecordsT paymentRecordsT, BigDecimal paymentAmount) throws CashPayVerifyException, PaymentClosedException, PaymentRepeatException, PaymentRefundedException;
}
