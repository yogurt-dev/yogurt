package com.github.jyoghurt.common.payment.common.service.impl;

import com.github.jyoghurt.common.payment.common.module.BaseCallBackParam;
import com.github.jyoghurt.common.payment.common.service.PaymentCallBackService;
import com.github.jyoghurt.common.payment.common.service.PaymentRecordsService;
import com.github.jyoghurt.common.payment.common.factory.PaymentListenerFactory;
import com.github.jyoghurt.common.payment.common.listener.PaymentListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("paymentCallBackService")
public class PaymentCallBackServiceImpl implements PaymentCallBackService {
    private static Logger logger = LoggerFactory.getLogger(PaymentCallBackServiceImpl.class);
    @Autowired
    private PaymentRecordsService paymentRecordsService;

    /**
     * 分发给业务
     *
     * @param baseCallBackParam 分发参数
     */
    @Override
    public void assignListener(BaseCallBackParam baseCallBackParam) {
        if (StringUtils.isEmpty(baseCallBackParam.getCallBackService())) {
            return;
        }
        PaymentListener paymentListener = PaymentListenerFactory.produce(baseCallBackParam.getCallBackService());
        if (null == paymentListener) {
            logger.error("支付接口回调后分发业务异常，回调信息:{}", baseCallBackParam.toString());
            return;
        }
        List<String> list;
        if (null != baseCallBackParam.getBuinessIds() && baseCallBackParam.getBuinessIds().size() != 0) {
            list = baseCallBackParam.getBuinessIds();
        } else {
            list = paymentRecordsService.findBusinessIdsByPaymentId(baseCallBackParam.getPaymentId());
        }
        try {
            //回调支付监听
            paymentListener.afterPayment(baseCallBackParam, list);
        } catch (Exception e) {
            logger.error("支付接口回调后分发业务异常，回调信息:{}", baseCallBackParam.toString(), e);
        }
    }


}
