package com.github.jyoghurt.common.payment.business.tencent.module;

import com.github.jyoghurt.common.payment.common.module.BaseQueryParams;

/**
 * Created by dell on 2016/3/10.
 */
public class TencentQueryParams extends BaseQueryParams {
    /**
     * 公众账号ID
     * 微信分配的公众账号ID（企业号corpid即为此appId）
     */
    private String appid;
    /**
     * 商户号
     * 微信支付分配的商户号
     */
    private String mch_id;
    /**
     * 子商户号
     * 微信支付分配的子商户号
     */
    private String sub_mch_id;
    /**
     * 微信订单号
     * 微信的订单号，优先使用
     */
    private String transaction_id;
    /**
     * 商户订单号
     * 商户系统内部的订单号，当没提供transaction_id时需要传这个
     */
    private String out_trade_no;
    /**
     * 随机字符串
     * 随机字符串，不长于32位。推荐随机数生成算法
     */
    private String nonce_str;
    /**
     * 签名
     */
    private String sign;
    /**
     * 交易开始时间
     */
    private String time_start;
    /**
     * 交易结束时间
     */
    private String time_expire;

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_expire() {
        return time_expire;
    }

    public void setTime_expire(String time_expire) {
        this.time_expire = time_expire;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
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

    public String getSub_mch_id() {
        return sub_mch_id;
    }

    public void setSub_mch_id(String sub_mch_id) {
        this.sub_mch_id = sub_mch_id;
    }

    @Override
    public String toString() {
        return "TencentQueryParams{" +
                "appid='" + appid + '\'' +
                ", mch_id='" + mch_id + '\'' +
                ", sub_mch_id='" + sub_mch_id + '\'' +
                ", transaction_id='" + transaction_id + '\'' +
                ", out_trade_no='" + out_trade_no + '\'' +
                ", nonce_str='" + nonce_str + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
