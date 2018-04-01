package com.github.jyoghurt.common.payment.business.tencent.module;




import com.github.jyoghurt.common.payment.common.enums.CurrencySpeciesEnum;
import com.github.jyoghurt.common.payment.business.tencent.enums.TencentTypeEnum;

/**
 * 微信支付实体
 */
public class TencentNativePaymentParams {
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
     * 商品描述
     * 商品或支付单简要描述
     * 会在微信凭证的商品详情中显示
     * 必填
     */
    private String body;
    /**
     * 商品详情
     * 商品名称明细列表
     */
    private String detail;
    /**
     * 附加数据
     * 附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
     */
    private String attach;
    /**
     * 商户订单号
     * 商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号
     * 必填
     */
    private String out_trade_no;
    /**
     * 货币类型
     * 符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见
     */
    private CurrencySpeciesEnum fee_type;
    /**
     * 总金额
     * 订单总金额，单位为分，详见支付金额
     * 必填
     */
    private Integer total_fee;
    /**
     * 终端IP
     * APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP
     * 必填
     */
    private String  spbill_create_ip;
    /**
     * 交易起始时间
     * 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010
     */
    private String time_start;
    /**
     * 交易结束时间
     * 订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010
     * 注意：最短失效时间间隔必须大于5分钟
     */
    private String time_expire;
    /**
     * 商品标记
     * 商品标记，代金券或立减优惠功能的参数
     */
    private String goods_tag;
    /**
     * 通知地址
     * 接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数
     * 必填
     */
    private String notify_url;
    /**
     * 交易类型
     * 必填
     */
    private TencentTypeEnum trade_type;
    /**
     * 商品ID
     * trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义。
     */
    private String product_id;
    /**
     * 指定支付方式
     * no_credit--指定不能使用信用卡支付
     */
    private String limit_pay;
    /**
     * 用户标识
     * trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识
     */
    private String openid;

    public String getAppid() {
        return appid;
    }

    public TencentNativePaymentParams setAppid(String appid) {
        this.appid = appid;
        return this;
    }

    public String getDevice_info() {
        return device_info;
    }

    public TencentNativePaymentParams setDevice_info(String device_info) {
        this.device_info = device_info;
        return this;
    }

    public String getMch_id() {
        return mch_id;
    }

    public TencentNativePaymentParams setMch_id(String mch_id) {
        this.mch_id = mch_id;
        return this;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public TencentNativePaymentParams setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public TencentNativePaymentParams setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public String getDetail() {
        return detail;
    }

    public TencentNativePaymentParams setDetail(String detail) {
        this.detail = detail;
        return this;
    }

    public String getBody() {
        return body;
    }

    public TencentNativePaymentParams setBody(String body) {
        this.body = body;
        return this;
    }

    public String getAttach() {
        return attach;
    }

    public TencentNativePaymentParams setAttach(String attach) {
        this.attach = attach;
        return this;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public TencentNativePaymentParams setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
        return this;
    }

    public Integer getTotal_fee() {
        return total_fee;
    }

    public TencentNativePaymentParams setTotal_fee(Integer total_fee) {
        this.total_fee = total_fee;
        return this;
    }

    public CurrencySpeciesEnum getFee_type() {
        return fee_type;
    }

    public TencentNativePaymentParams setFee_type(CurrencySpeciesEnum fee_type) {
        this.fee_type = fee_type;
        return this;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public TencentNativePaymentParams setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
        return this;
    }

    public String getTime_expire() {
        return time_expire;
    }

    public TencentNativePaymentParams setTime_expire(String time_expire) {
        this.time_expire = time_expire;
        return this;
    }

    public String getTime_start() {
        return time_start;
    }

    public TencentNativePaymentParams setTime_start(String time_start) {
        this.time_start = time_start;
        return this;
    }

    public String getGoods_tag() {
        return goods_tag;
    }

    public TencentNativePaymentParams setGoods_tag(String goods_tag) {
        this.goods_tag = goods_tag;
        return this;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public TencentNativePaymentParams setNotify_url(String notify_url) {
        this.notify_url = notify_url;
        return this;
    }

    public TencentTypeEnum getTrade_type() {
        return trade_type;
    }

    public TencentNativePaymentParams setTrade_type(TencentTypeEnum trade_type) {
        this.trade_type = trade_type;
        return this;
    }

    public String getProduct_id() {
        return product_id;
    }

    public TencentNativePaymentParams setProduct_id(String product_id) {
        this.product_id = product_id;
        return this;
    }

    public String getLimit_pay() {
        return limit_pay;
    }

    public TencentNativePaymentParams setLimit_pay(String limit_pay) {
        this.limit_pay = limit_pay;
        return this;
    }

    public String getOpenid() {
        return openid;
    }

    public TencentNativePaymentParams setOpenid(String openid) {
        this.openid = openid;
        return this;
    }

    public String getSub_appid() {
        return sub_appid;
    }

    public void setSub_appid(String sub_appid) {
        this.sub_appid = sub_appid;
    }

    public String getSub_mch_id() {
        return sub_mch_id;
    }

    public void setSub_mch_id(String sub_mch_id) {
        this.sub_mch_id = sub_mch_id;
    }


    @Override
    public String toString() {
        return "TencentNativePaymentParams{" +
                "appid='" + appid + '\'' +
                ", sub_appid='" + sub_appid + '\'' +
                ", mch_id='" + mch_id + '\'' +
                ", sub_mch_id='" + sub_mch_id + '\'' +
                ", device_info='" + device_info + '\'' +
                ", nonce_str='" + nonce_str + '\'' +
                ", sign='" + sign + '\'' +
                ", body='" + body + '\'' +
                ", detail='" + detail + '\'' +
                ", attach='" + attach + '\'' +
                ", out_trade_no='" + out_trade_no + '\'' +
                ", fee_type=" + fee_type +
                ", total_fee=" + total_fee +
                ", spbill_create_ip='" + spbill_create_ip + '\'' +
                ", time_start='" + time_start + '\'' +
                ", time_expire='" + time_expire + '\'' +
                ", goods_tag='" + goods_tag + '\'' +
                ", notify_url='" + notify_url + '\'' +
                ", trade_type=" + trade_type +
                ", product_id='" + product_id + '\'' +
                ", limit_pay='" + limit_pay + '\'' +
                ", openid='" + openid + '\'' +
                '}';
    }
}
