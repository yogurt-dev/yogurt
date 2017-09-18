package com.github.jyoghurt.common.payment.business.tencent.module;

import com.github.jyoghurt.common.payment.common.enums.CurrencySpeciesEnum;

/**
 * user:zjl
 * date: 2016/12/22.
 */
public class TencentRefundParams {
    /**
     * 公众账号ID
     * 微信分配的公众账号ID（企业号corpid即为此appId）
     * 必填
     */
    private String appid;
    /**
     * 子商户公众账号ID
     * 微信分配的子商户公众账号ID，如需在支付完成后获取sub_openid则此参数必传。
     */
    private String sub_appid;
    /**
     * 商户号
     * 微信支付分配的商户号
     * 必填
     */
    private String mch_id;
    /**
     * 子商户号
     * 微信支付分配的子商户号
     */
    private String sub_mch_id;
    /**
     * 设备号
     * 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
     */
    private String device_info;
    /**
     * 随机字符串
     * 随机字符串，不长于32位。推荐随机数生成算法
     * 用于生成签名 保证签名安全
     * 必填
     */
    private String nonce_str;
    /**
     * 签名
     * 签名，详见签名生成算法
     * 必填
     */
    private String sign;
    /**
     * 签名类型，目前支持HMAC-SHA256和MD5，默认为MD5
     */
    private String sign_type;
    /**
     * 微信生成的订单号，在支付通知中有返回 与商户订单号二选一
     */
    private String transaction_id;
    /**
     * 商户订单号
     * 商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号
     * 必填
     */
    private String out_trade_no;
    /**
     * 商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
     * 必填
     */
    private String out_refund_no;
    /**
     * 订单总金额，单位为分，只能为整数，详见支付金额
     * 必填
     */
    private Integer total_fee;
    /**
     * 退款总金额，订单总金额，单位为分，只能为整数，详见支付金额
     * 必填
     */
    private Integer refund_fee;
    /**
     * 货币类型
     * 符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见
     */
    private CurrencySpeciesEnum refund_fee_type;
    /**
     * 操作员帐号, 默认为商户号
     */
    private String op_user_id;
    /**
     * 仅针对老资金流商户使用
     * REFUND_SOURCE_UNSETTLED_FUNDS---未结算资金退款（默认使用未结算资金退款）
     * REFUND_SOURCE_RECHARGE_FUNDS---可用余额退款
     */
    private String refund_account;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSub_appid() {
        return sub_appid;
    }

    public void setSub_appid(String sub_appid) {
        this.sub_appid = sub_appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getSub_mch_id() {
        return sub_mch_id;
    }

    public void setSub_mch_id(String sub_mch_id) {
        this.sub_mch_id = sub_mch_id;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getOut_refund_no() {
        return out_refund_no;
    }

    public void setOut_refund_no(String out_refund_no) {
        this.out_refund_no = out_refund_no;
    }

    public Integer getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(Integer total_fee) {
        this.total_fee = total_fee;
    }

    public Integer getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(Integer refund_fee) {
        this.refund_fee = refund_fee;
    }

    public CurrencySpeciesEnum getRefund_fee_type() {
        return refund_fee_type;
    }

    public void setRefund_fee_type(CurrencySpeciesEnum refund_fee_type) {
        this.refund_fee_type = refund_fee_type;
    }

    public String getOp_user_id() {
        return op_user_id;
    }

    public void setOp_user_id(String op_user_id) {
        this.op_user_id = op_user_id;
    }

    public String getRefund_account() {
        return refund_account;
    }

    public void setRefund_account(String refund_account) {
        this.refund_account = refund_account;
    }
}
