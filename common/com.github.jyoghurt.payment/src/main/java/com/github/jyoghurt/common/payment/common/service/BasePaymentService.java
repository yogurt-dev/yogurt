package com.github.jyoghurt.common.payment.common.service;

import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;

import java.util.List;

/**
 * user:dell
 * date: 2016/5/30.
 */
public interface BasePaymentService {
    /**
     * 扩展方法.
     * 各业务实现.
     *
     * @param businessIds    业务Id集合
     * @param paymentRecords 支付记录
     * @return Object 扩展信息对象
     */
    Object extensionMethods(List<String> businessIds, PaymentRecordsT paymentRecords);
}
