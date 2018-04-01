package com.github.jyoghurt.common.payment.business.card.service.impl;

import com.github.jyoghurt.common.payment.business.card.service.CardPayService;
import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentStateEnum;
import com.github.jyoghurt.common.payment.common.exception.PaymentClosedException;
import com.github.jyoghurt.common.payment.common.exception.PaymentRefundedException;
import com.github.jyoghurt.common.payment.common.exception.PaymentRepeatException;
import com.github.jyoghurt.common.payment.common.service.PaymentRecordsService;
import com.github.jyoghurt.common.payment.common.service.PaymentValidateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * user:dell
 * data:2016/4/27.
 */
@Service("cardPayService")
public class CardPayServiceImpl implements CardPayService {
    private static Logger logger = LoggerFactory.getLogger(CardPayServiceImpl.class);

    @Autowired
    private PaymentRecordsService paymentRecordsService;
    @Autowired
    private PaymentValidateService paymentValidateService;

    /**
     * 刷卡支付
     *
     * @param paymentId 支付Id
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     */
    @Override
    public PaymentStateEnum cardPay(String paymentId) throws PaymentClosedException, PaymentRepeatException, PaymentRefundedException {
        PaymentRecordsT paymentRecordsT = paymentRecordsService.find(paymentId);
        //验证是否可支付
        //验证是否可支付
        try {
            paymentValidateService.verifyPaymentState(paymentRecordsT);
        } catch (PaymentClosedException e) {
            logger.info("关联支付记录中存在已关闭支付记录");
        }
        paymentRecordsT.setPaymentMethod(PaymentGatewayEnum.CARD_PAY);
        try {
            paymentRecordsService.finishPaymentRecord(paymentRecordsT);
        } catch (PaymentRepeatException e) {
            return PaymentStateEnum.SUCCESS;
        }
        return PaymentStateEnum.SUCCESS;
    }

    /**
     * 刷卡支付
     *
     * @param paymentRecordsT 支付记录
     * @return 支付状态
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     */
    @Override
    public PaymentStateEnum cardPay(PaymentRecordsT paymentRecordsT) throws PaymentClosedException, PaymentRepeatException, PaymentRefundedException {
        paymentRecordsT.setPaymentMethod(PaymentGatewayEnum.CARD_PAY);
        //验证是否可支付
        try {
            paymentValidateService.verifyPaymentState(paymentRecordsT);
        } catch (PaymentClosedException e) {
            logger.info("关联支付记录中存在已关闭支付记录");
        }
        paymentRecordsT.setPaymentMethod(PaymentGatewayEnum.CARD_PAY);
        try {
            paymentRecordsService.finishPaymentRecord(paymentRecordsT);
        } catch (PaymentRepeatException e) {
            return PaymentStateEnum.SUCCESS;
        }
        return PaymentStateEnum.SUCCESS;
    }
}
