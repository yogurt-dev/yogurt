package com.github.jyoghurt.common.payment.business.tencent.common.constants;

/**
 * user: dell
 * date:2016/3/14.
 */
public class TencentPaymentConstants {
    /**
     * 微信通信返回值
     */
    public static String RETURN_CODE = "return_code";
    /**
     * 微信通信返回异常结果
     */
    public static String RETURN_MSG = "return_msg";
    /**
     * 微信通信返回结果
     */
    public static String RESULT_MSG = "result_msg";
    /**
     * 通信成功
     */
    public static String COMMUNICATE_SUCCESS = "SUCCESS";
    /**
     * 微信交易返回值
     */
    public static String RESULT_CODE = "result_code";
    /**
     * 业务结果
     */
    public static String RESULT_CODE_SUCCESS = "SUCCESS";
    /**
     * 交易成功
     */
    public static String TRANSACTION_SUCCESS = "SUCCESS";
    /**
     * 异常
     */
    /**
     * 错误代码
     */
    public static String ERR_CODE = "err_code";
    /**
     * 错误代码描述
     */
    public static String ERR_CODE_DES = "err_code_des";
    /**
     * 预支付会话标识
     */
    public static String PREPAY_ID = "prepay_id";
    /**
     * 二维码链接
     */
    public static String CODE_URL = "code_url";
    /**
     * attach信息
     */
    public static String ATTACH = "attach";
    /**
     * 支付金额
     */
    public static String TOTAL_FEE = "total_fee";
    /**
     * 交易Id
     */
    public static String TRANSACTION_ID = "transaction_id";
    /**
     * 交易单号
     */
    public static String OUT_TRADE_NO = "out_trade_no";
    /**
     * 交易结果
     */
    public static String TRADE_STATE = "trade_state";
    /**
     * 接收成功
     */
    public static String RESPONSE_SUCCESS = "<xml>\n" +
            "<return_code><![CDATA[SUCCESS]]></return_code>\n" +
            "<return_msg><![CDATA[OK]]></return_msg>\n" +
            "</xml>";
    /**
     * 订单不存在
     */
    public static String ORDER_NOT_EXIST="ORDERNOTEXIST";
    /**
     * 签名
     */
    public static String SIGN = "sign";
    /**
     * 微信支付附加值
     */
    public static String ATTACH_CALL_BACK_SERVICE = "PaymentCallBackService";

}
