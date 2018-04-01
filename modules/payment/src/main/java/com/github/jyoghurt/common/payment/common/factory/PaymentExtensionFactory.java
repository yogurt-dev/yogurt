package com.github.jyoghurt.common.payment.common.factory;

import com.github.jyoghurt.common.payment.common.service.BasePaymentService;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.utils.SpringContextUtils;

/**
 * user:dell
 * date: 2016/5/30.
 */
public class PaymentExtensionFactory {
    public static BasePaymentService produce(String serviceName) {
        try {
            return (BasePaymentService) SpringContextUtils.getBean(serviceName);
        } catch (Exception e) {
            throw new BaseErrorException("获取业务扩展接口失败,接口名称：" + serviceName);
        }
    }
}
