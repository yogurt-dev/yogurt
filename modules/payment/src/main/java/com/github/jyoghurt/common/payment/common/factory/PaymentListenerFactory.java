package com.github.jyoghurt.common.payment.common.factory;

import com.github.jyoghurt.common.payment.common.listener.PaymentListener;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * user: dell
 * date:2016/3/24.
 */
public class PaymentListenerFactory {

    private static Logger logger = LoggerFactory.getLogger(PaymentListenerFactory.class);

    public static PaymentListener produce(String serviceName) {
        try {
            return (PaymentListener) SpringContextUtils.getBean(serviceName);
        } catch (Exception e) {
            return null;
        }
    }
}
