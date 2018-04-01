package com.github.jyoghurt.common.payment.business.tencent.module;

/**
 * user:dell
 * date: 2016/6/9.
 */
public class TencentCancelParams {
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
     * 商户订单号
     * 商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号
     * 必填
     */
    private String out_trade_no;

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

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    @Override
    public String toString() {
        return "TencentCancelParams{" +
                "appid='" + appid + '\'' +
                ", sub_appid='" + sub_appid + '\'' +
                ", mch_id='" + mch_id + '\'' +
                ", sub_mch_id='" + sub_mch_id + '\'' +
                ", nonce_str='" + nonce_str + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
