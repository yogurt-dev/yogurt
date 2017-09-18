package com.github.jyoghurt.common.payment.common.service;


import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.exception.PaymentPreRepeatException;
import com.github.jyoghurt.common.payment.common.exception.PaymentPreviousErrorException;
import com.github.jyoghurt.common.payment.common.enums.PaymentCloseEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentStateEnum;
import com.github.jyoghurt.common.payment.common.exception.PaymentRefundErrorException;
import com.github.jyoghurt.common.payment.common.module.RefundRequest;

/**
 * @author zhangjl
 */
public interface PaymentService {
    /**
     * 生成消费订单
     *
     * @param paymentGatewayEnum 支付平台
     * @param paymentRecordsT    支付记录
     * @return 预支付结果
     */
    Object createPreviousOrder(PaymentGatewayEnum paymentGatewayEnum, PaymentRecordsT paymentRecordsT) throws PaymentPreRepeatException, PaymentPreviousErrorException;

    /**
     * 查询消费订单
     *
     * @param paymentGatewayEnum 支付平台
     * @param paymentRecordsT    支付记录
     * @return 查询结果 支付结果
     */
    PaymentStateEnum queryPaymentResult(PaymentGatewayEnum paymentGatewayEnum, PaymentRecordsT paymentRecordsT);

    /**
     * 关闭支付接口
     *
     * @param paymentRecordsT 取消支付对象
     * @return PaymentResult<Boolean>  true 为已支付 false为未支付
     */
    PaymentCloseEnum closePayment(PaymentRecordsT paymentRecordsT);

    /**
     * 退款接口
     *
     * @param refundRequest 退款请求对象
     */
    void refundPayment(RefundRequest refundRequest) throws PaymentRefundErrorException;
}
