package com.github.jyoghurt.common.payment.business.alipay.common.constants;

/**
 * user: dell
 * date: 2016/4/20.
 */
public class AliPayConstants {
    /**
     * 支付宝验证回调真实性Api
     */
    public static String checkCallBackApi = "https://mapi.alipay.com/gateway" +
            ".do?service=notify_verify&partner=pid&notify_id=notifyId";
    /**
     * 验证结果
     */
    public static String checkPase = "true";
    /**
     * 回调Id
     */
    public static String notify_id = "notify_id";
    /**
     * 商户订单号
     */
    public static String out_trade_no = "out_trade_no";
    /**
     * 交易Id
     */
    public static String trade_no = "trade_no";
    /**
     * 订单金额
     */
    public static String total_fee = "total_amount";
    /**
     * 接收成功
     */
    public static String RESPONSE_SUCCESS = "success";
    /**
     * 阿里交互成功编码
     */
    public static String SUCCESS_CODE="10000";
    /**
     * 阿里无法找到支付记录编码
     */
    public static String CANT_FIND_TRADE="40004";
}
