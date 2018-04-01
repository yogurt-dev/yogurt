package com.github.jyoghurt.common.payment.business.cash.service.impl;

import com.github.jyoghurt.common.payment.business.cash.service.CashPayService;
import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.exception.CashPayVerifyException;
import com.github.jyoghurt.common.payment.common.exception.PaymentRefundedException;
import com.github.jyoghurt.common.payment.common.constants.PaymentCommonConstants;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentStateEnum;
import com.github.jyoghurt.common.payment.common.exception.PaymentClosedException;
import com.github.jyoghurt.common.payment.common.exception.PaymentRepeatException;
import com.github.jyoghurt.common.payment.common.service.PaymentRecordsService;
import com.github.jyoghurt.common.payment.common.service.PaymentValidateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;


/**
 * user:dell
 * data:2016/4/27.
 */
@Service("cashPayService")
public class CashPayServiceImpl implements CashPayService {
    private static Logger logger = LoggerFactory.getLogger(CashPayServiceImpl.class);


    @Autowired
    private PaymentRecordsService paymentRecordsService;
    @Autowired
    private PaymentValidateService paymentValidateService;

    /**
     * 现金支付
     *
     * @param paymentId     支付Id
     * @param paymentAmount 支付金额
     * @return 支付状态
     * @throws CashPayVerifyException
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     */
    public PaymentStateEnum cashPay(String paymentId, BigDecimal paymentAmount) throws CashPayVerifyException, PaymentClosedException, PaymentRepeatException, PaymentRefundedException {
        PaymentRecordsT paymentRecordsT = paymentRecordsService.find(paymentId);
        this.cashPay(paymentRecordsT, paymentAmount);
        return PaymentStateEnum.SUCCESS;
    }

    /**
     * 现金支付
     *
     * @param paymentRecordsT 支付记录
     * @param paymentAmount   支付金额
     * @return 支付状态
     * @throws CashPayVerifyException
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     */
    @Override
    public PaymentStateEnum cashPay(PaymentRecordsT paymentRecordsT, BigDecimal paymentAmount) throws CashPayVerifyException, PaymentClosedException, PaymentRepeatException, PaymentRefundedException {
        paymentRecordsT.setPaymentMethod(PaymentGatewayEnum.CASH_PAY);
        if (!paymentValidateService.checkCashPay(paymentRecordsT, paymentAmount)) {
            throw new CashPayVerifyException();
        }
        //将找零存入相关数据区
        Map<String, Object> dataArea = paymentRecordsT.getDataAreaMap();
        dataArea.put(PaymentCommonConstants.PAID_IN_PRICE, paymentAmount);
        dataArea.put(PaymentCommonConstants.TOTAL_CHANGE_PRICE, paymentAmount.subtract(paymentRecordsT.getTotleFee()));
        paymentRecordsT.setDataAreaMap(dataArea);
        //验证是否可支付
        try {
            paymentValidateService.verifyPaymentState(paymentRecordsT);
        } catch (PaymentClosedException e) {
            logger.info("关联支付记录中存在已关闭支付记录");
        }
        paymentRecordsT.setPaymentMethod(PaymentGatewayEnum.CASH_PAY);
        try {
            paymentRecordsService.finishPaymentRecord(paymentRecordsT);
        } catch (PaymentRepeatException e) {
            return PaymentStateEnum.SUCCESS;
        }
        return PaymentStateEnum.SUCCESS;
    }


}
