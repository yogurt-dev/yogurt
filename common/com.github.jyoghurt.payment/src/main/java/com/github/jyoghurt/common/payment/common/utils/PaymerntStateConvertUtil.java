package com.github.jyoghurt.common.payment.common.utils;


import com.github.jyoghurt.common.payment.business.alipay.enums.AliPaymentStateEnum;
import com.github.jyoghurt.common.payment.business.tencent.enums.TencentPaymentStateEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentStateEnum;
import com.github.jyoghurt.core.exception.BaseErrorException;

/**
 * user: dell
 * date:2016/3/28.
 */
public class PaymerntStateConvertUtil {
    public static PaymentStateEnum convertTencentPaymentState(String state) {
        switch (Enum.valueOf(TencentPaymentStateEnum.class, state)) {
            case SUCCESS: {//—支付成功
                return PaymentStateEnum.SUCCESS;
            }
            case REFUND: {//—转入退款
                return PaymentStateEnum.REFUND;
            }
            case NOTPAY: {//—未支付
                return PaymentStateEnum.NOTPAY;
            }
            case CLOSED: {//—已关闭
                return PaymentStateEnum.CLOSED;
            }
            case REVOKED: {//—已撤销（刷卡支付）
                return PaymentStateEnum.CLOSED;
            }
            default:
                throw new BaseErrorException("微信支付状态转换异常，微信返回状态:{0}", state);
        }
    }

    public static PaymentStateEnum convertAlipayState(String state) {

        switch (Enum.valueOf(AliPaymentStateEnum.class, state)) {
            case TRADE_SUCCESS: {//—支付成功
                return PaymentStateEnum.SUCCESS;
            }
            case TRADE_FINISHED: {//（交易结束，不可退款）
                return PaymentStateEnum.CLOSED;
            }
            case TRADE_CLOSED: {//—支付关闭
                return PaymentStateEnum.CLOSED;
            }
            case WAIT_BUYER_PAY: {
                return PaymentStateEnum.NOTPAY;
            }
            default: {
                throw new BaseErrorException("支付宝支付状态转换异常，支付宝返回状态:{0}", state);
            }
        }
    }

}
