package com.github.jyoghurt.common.payment.common.enums;

/**
 * user:zjl
 * date: 2016/12/23.
 */
public enum  RefundPaymentGatewayEnum {

    /**
     * 银联支付
     */
    UNION_PAY(PaymentPlatFormEnum.UNION_PAY),
    /**
     * 微信支付
     */
    TENCENT_PAY(PaymentPlatFormEnum.TENCENT_PAY),
    /**
     * 微信公众号支付
     */
    TENCENT_JSAPI(PaymentPlatFormEnum.TENCENT_PAY),
    /**
     * 支付宝
     */
    ALI_PAY(PaymentPlatFormEnum.ALI_PAY),
    /**
     * 现金支付
     */
    CASH_PAY(PaymentPlatFormEnum.CASH_PAY),
    /**
     * 刷卡支付
     */
    CARD_PAY(PaymentPlatFormEnum.CARD_PAY),
    /**
     * 仅红包或余额支付
     */
    ONLY_LUCK_OR_BALANCE_PAY(PaymentPlatFormEnum.ONLY_LUCK_OR_BALANCE_PAY);

    private PaymentPlatFormEnum paymentPlatForm;

    RefundPaymentGatewayEnum(PaymentPlatFormEnum paymentPlatForm) {
        this.paymentPlatForm = paymentPlatForm;
    }

    public PaymentPlatFormEnum getPaymentPlatForm() {
        return paymentPlatForm;
    }

    public void setPaymentPlatForm(PaymentPlatFormEnum paymentPlatForm) {
        this.paymentPlatForm = paymentPlatForm;
    }
}
