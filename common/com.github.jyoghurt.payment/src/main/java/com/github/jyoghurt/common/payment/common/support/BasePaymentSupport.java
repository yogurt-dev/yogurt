package com.github.jyoghurt.common.payment.common.support;


import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.enums.PaymentCloseEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentRefundEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentStateEnum;
import com.github.jyoghurt.common.payment.common.exception.PaymentPreviousErrorException;
import com.github.jyoghurt.common.payment.common.module.RefundRequest;


public abstract class BasePaymentSupport {
    /**
     * 创建预支付
     * 具体业务需继承者实现
     *
     * @param paymentRecordsT 预支付需要的参数对象
     * @return Object 返回预支付结果
     */
    public abstract Object createPreviousOrder(PaymentRecordsT paymentRecordsT) throws PaymentPreviousErrorException;

    /**
     * 根据支付单据查询
     * 具体业务需继承者实现
     *
     * @param paymentRecordsT 查询参数对象
     * @return PaymentStateEnum  支付状态枚举
     */
    public abstract PaymentStateEnum queryPaymentResult(PaymentRecordsT paymentRecordsT);

    /**
     * 取消支付接口
     *
     * @param paymentRecordsT 取消支付对象
     * @return PaymentCloseEnum  取消状态枚举
     */
    public abstract PaymentCloseEnum closePayment(PaymentRecordsT paymentRecordsT);

    /**
     * 退款接口
     *
     * @param refundRequest   退款请求
     * @param paymentRecordsT 支付记录* @return 退款结果枚举
     */
    public abstract PaymentRefundEnum refundPayment(RefundRequest refundRequest, PaymentRecordsT paymentRecordsT);

}
