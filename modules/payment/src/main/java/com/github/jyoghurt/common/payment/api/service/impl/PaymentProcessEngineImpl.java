package com.github.jyoghurt.common.payment.api.service.impl;

import com.github.jyoghurt.common.payment.api.service.PaymentProcessEngine;
import com.github.jyoghurt.common.payment.common.service.PaymentManagerService;
import com.github.jyoghurt.common.payment.common.service.PaymentSearchService;
import com.github.jyoghurt.common.payment.common.service.PaymentValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * user:dell
 * date: 2016/8/12.
 */
@Service("paymentProcessEngine")
public class PaymentProcessEngineImpl implements PaymentProcessEngine {
    //驴鱼支付api汇总引擎

    @Autowired //驴鱼支付操作API
    public PaymentManagerService paymentManagerService;
    @Autowired//驴鱼支付验证支付API
    public PaymentValidateService paymentValidateService;
    @Autowired//驴鱼支付查询支付API
    public PaymentSearchService paymentSearchService;

    @Override
    public PaymentManagerService getPaymentManagerService() {
        return this.paymentManagerService;
    }

    @Override
    public PaymentValidateService getPaymentValidateService() {
        return this.paymentValidateService;
    }

    @Override
    public PaymentSearchService getPaymentSearchService() {
        return this.paymentSearchService;
    }
}
