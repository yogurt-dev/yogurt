package com.github.jyoghurt.common.payment.common.service.impl;


import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.domain.PaymentRefundT;
import com.github.jyoghurt.common.payment.common.service.PaymentRecordsService;
import com.github.jyoghurt.common.payment.common.dao.PaymentRefundMapper;
import com.github.jyoghurt.common.payment.common.exception.PaymentRefundErrorException;
import com.github.jyoghurt.common.payment.common.module.RefundRequest;
import com.github.jyoghurt.common.payment.common.service.PaymentRefundService;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("paymentRefundService")
public class PaymentRefundServiceImpl extends ServiceSupport<PaymentRefundT, PaymentRefundMapper> implements PaymentRefundService {
    @Autowired
    private PaymentRefundMapper paymentRefundMapper;
    @Autowired
    private PaymentRecordsService paymentRecordsService;

    @Override
    public PaymentRefundMapper getMapper() {
        return paymentRefundMapper;
    }

    @Override
    public void logicDelete(Serializable id) {
        getMapper().logicDelete(PaymentRefundT.class, id);
    }

    @Override
    public PaymentRefundT find(Serializable id) {
        return getMapper().selectById(PaymentRefundT.class, id);
    }

    /**
     * @param refundRequest  退款请求
     * @param paymentRecordT 退款支付记录
     */
    @Override
    public void savePaymentRefund(RefundRequest refundRequest, PaymentRecordsT paymentRecordT) throws PaymentRefundErrorException {
        paymentRecordsService.refundMoney(refundRequest.getRefundAmount(), paymentRecordT.getPaymentId());
        this.save(convert(refundRequest, paymentRecordT).setRefundPaymentState(true));
    }

    private PaymentRefundT convert(RefundRequest refundRequest, PaymentRecordsT paymentRecordT) {
        PaymentRefundT paymentRefundT = new PaymentRefundT();
        paymentRefundT.setRefundedMoney(refundRequest.getRefundAmount())
                .setBusinessId(refundRequest.getBusinessId())
                .setOutRequestNo(refundRequest.getOutRequestNo())
                .setPaymentBusinessType(refundRequest.getPaymentBusinessType())
                .setRefundPaymentGateway(paymentRecordT.getPaymentMethod())
                .setRefundPaymentState(true)
                .setRefundPaymentReason(refundRequest.getRefundPaymentReason());
        return paymentRefundT;
    }
}
