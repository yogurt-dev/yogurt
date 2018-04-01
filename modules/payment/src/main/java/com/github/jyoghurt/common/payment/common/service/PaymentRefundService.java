package com.github.jyoghurt.common.payment.common.service;


import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.domain.PaymentRefundT;
import com.github.jyoghurt.common.payment.common.exception.PaymentRefundErrorException;
import com.github.jyoghurt.common.payment.common.module.RefundRequest;
import com.github.jyoghurt.core.service.BaseService;

/**
 * 退款表服务层
 */
public interface PaymentRefundService extends BaseService<PaymentRefundT> {
    /**
     * @param refundRequest  退款请求
     * @param paymentRecordT 退款支付记录
     */
    void savePaymentRefund(RefundRequest refundRequest, PaymentRecordsT paymentRecordT) throws PaymentRefundErrorException;
}
