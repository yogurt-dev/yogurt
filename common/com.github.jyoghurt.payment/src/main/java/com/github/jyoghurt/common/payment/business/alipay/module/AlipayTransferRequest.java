package com.github.jyoghurt.common.payment.business.alipay.module;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: DELL
 * Date: 2017/9/25
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
public class AlipayTransferRequest {
    /**
     * 支付宝分配给开发者的应用ID
     */
    private String app_id;
    /**
     * 私钥
     */
    private String privateKey;
    /**
     * 公钥
     */
    private String publicKey;
    /**
     * 接口名称
     */
    private String method = "alipay.fund.trans.toaccount.transfer";
    /**
     * 请求使用的编码格式，如utf-8,gbk,gb2312等
     */
    private String charset = "utf-8";
    /**
     * 商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2
     */
    private String sign_type = "RSA2";
    /**
     * 商户请求参数的签名串，详见签名，目前支持RSA2和RSA，推荐使用RSA2
     */
    private String sign;
    /**
     * 发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
     */
    private String timestamp;
    /**
     * 调用的接口版本，固定为：1.0
     */
    private String version = "1.0";
    /**
     * 商户转账唯一订单号。发起转账来源方定义的转账单据ID，用于将转账回执通知给来源方。
     * 不同来源方给出的ID可以重复，同一个来源方必须保证其ID的唯一性。
     * 只支持半角英文、数字，及“-”、“_”。
     */
    private String out_biz_no;
    /**
     * 收款方账户类型。可取值：
     * 1、ALIPAY_USERID：支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成。
     * 2、ALIPAY_LOGONID：支付宝登录号，支持邮箱和手机号格式。
     */
    private String payee_type = "ALIPAY_LOGONID";
    /**
     * 收款方账户。与payee_type配合使用。付款方和收款方不能是同一个账户
     */
    private String payee_account;
    /**
     * 转账金额，单位：元。
     * 只支持2位小数，小数点前最大支持13位，金额必须大于等于0.1元。
     * 最大转账金额以实际签约的限额为准。
     */
    private BigDecimal amount;
    /**
     * 付款方姓名（最长支持100个英文/50个汉字）。显示在收款方的账单详情页。如果该字段不传，则默认显示付款方的支付宝认证姓名或单位名称。
     */
    private String payer_show_name;
    /**
     * 收款方真实姓名（最长支持100个英文/50个汉字）。
     * 如果本参数不为空，则会校验该账户在支付宝登记的实名是否与收款方真实姓名一致。
     */
    private String payee_real_name;
    /**
     * 转账备注（支持200个英文/100个汉字）。
     * 当付款方为企业账户，且转账金额达到（大于等于）50000元，remark不能为空。收款方可见，会展示在收款用户的收支详情中。
     */
    private String remark;


    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOut_biz_no() {
        return out_biz_no;
    }

    public void setOut_biz_no(String out_biz_no) {
        this.out_biz_no = out_biz_no;
    }

    public String getPayee_type() {
        return payee_type;
    }

    public void setPayee_type(String payee_type) {
        this.payee_type = payee_type;
    }

    public String getPayee_account() {
        return payee_account;
    }

    public void setPayee_account(String payee_account) {
        this.payee_account = payee_account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPayer_show_name() {
        return payer_show_name;
    }

    public void setPayer_show_name(String payer_show_name) {
        this.payer_show_name = payer_show_name;
    }

    public String getPayee_real_name() {
        return payee_real_name;
    }

    public void setPayee_real_name(String payee_real_name) {
        this.payee_real_name = payee_real_name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
