package com.github.jyoghurt.common.payment.api.service;

import com.github.jyoghurt.common.payment.common.service.PaymentManagerService;
import com.github.jyoghurt.common.payment.common.service.PaymentSearchService;
import com.github.jyoghurt.common.payment.common.service.PaymentValidateService;

/**
 * user:dell
 * date: 2016/8/12.
 */
public interface PaymentProcessEngine {
    //驴鱼支付操作API
    PaymentManagerService getPaymentManagerService();

    //驴鱼支付验证支付API
    PaymentValidateService getPaymentValidateService();

    //驴鱼支付查询支付API
    PaymentSearchService getPaymentSearchService();
}
