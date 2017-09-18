package com.github.jyoghurt.common.payment.business.card.service;

import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.exception.PaymentRefundedException;
import com.github.jyoghurt.common.payment.common.enums.PaymentStateEnum;
import com.github.jyoghurt.common.payment.common.exception.PaymentClosedException;
import com.github.jyoghurt.common.payment.common.exception.PaymentRepeatException;


/**
 * user:dell
 * data:2016/4/27.
 */
public interface CardPayService {
    /**
     * 刷卡支付
     *
     * @param paymentId 支付Id
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     */
    PaymentStateEnum cardPay(String paymentId) throws PaymentClosedException, PaymentRepeatException, PaymentRefundedException;

    /**
     * 刷卡支付
     *
     * @param paymentRecordsT 支付记录
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     */
    PaymentStateEnum cardPay(PaymentRecordsT paymentRecordsT) throws PaymentClosedException, PaymentRepeatException, PaymentRefundedException;
}
