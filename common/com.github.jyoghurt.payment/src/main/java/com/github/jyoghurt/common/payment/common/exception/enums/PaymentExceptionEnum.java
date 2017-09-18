package com.github.jyoghurt.common.payment.common.exception.enums;

/**
 * user:dell
 * date: 2016/10/28.
 */
public enum PaymentExceptionEnum {
    /**
     * 支付异常划分
     * 6001-6200    支付模块内部接口间调用异常
     * 6201-6300    微信支付异常
     * 6301-6400    支付宝支付异常
     * 6401-6500    现金支付异常
     * 6501-6600    刷卡支付异常
     * 6601-6700    银联支付异常
     * 6701-6999    备用异常 从后至前进行备用
     */
    ERROR_6001("您好，您本次支付已成功，请不要重复支付"),//重复支付
    ERROR_6002("您好，您本次支付已取消"),//重复取消支付
    ERROR_6003("您好，请不要重复进行退款"),//已退款
    ERROR_6004("您好，您的支付请求因为异常终止，请重新发起支付"),//重复申请预支付
    ERROR_6005("您好，支付前请先选择收银台"),//todo 这个异常以后会改造 不属于支付 包括支付页面均应挪到community-payment-plugin中
    ERROR_6006("退款失败"),//退款失败
    ERROR_6007("预支付失败，请检查订单是否已支付或已取消"),//预支付失败
    ERROR_6401("您好，现金收款金额小于应收金额，请您核对收款金额！"),//现金支付 收款金额小于应收金额
    ERROR_6999("");

    private String message;

    PaymentExceptionEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
