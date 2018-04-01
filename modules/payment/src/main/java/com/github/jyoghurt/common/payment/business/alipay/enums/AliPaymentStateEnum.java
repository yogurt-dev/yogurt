package com.github.jyoghurt.common.payment.business.alipay.enums;

/**
 * user:dell
 * date: 2016/6/8.
 */
public enum AliPaymentStateEnum {
    WAIT_BUYER_PAY,//("交易创建，等待买家付")
    TRADE_CLOSED,//（未付款交易超时关闭，或支付完成后全额退款）
    TRADE_SUCCESS,//（交易支付成功）
    TRADE_FINISHED,//（交易结束，不可退款）
    CANT_FIND_TRADE//(未找到本次交易)
}
