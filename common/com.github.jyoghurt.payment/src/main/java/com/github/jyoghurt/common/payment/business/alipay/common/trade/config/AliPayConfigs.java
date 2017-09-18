//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.github.jyoghurt.common.payment.business.alipay.common.trade.config;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AliPayConfigs {
    private static Log log = LogFactory.getLog(AliPayConfigs.class);
    private String openApiDomain;
    /**
     * 交易保障心跳监测接口
     */
    private String mcloudApiDomain = "http://mcloudmonitor.com/gateway.do";
    /**
     * 合作者身份ID
     */
    private String pid;
    /**
     * APPID
     */
    private String appid;
    /**
     * 商户方的私钥
     */
    private String privateKey;
    /**
     * 验签公钥(支付宝公钥)
     */
    private String publicKey;
    /**
     * 开发者公钥
     */
    private String alipayPublicKey;
    // MD5密钥，安全检验码，由数字和字母组成的32位字符串
    private String md5Key;
    private int maxQueryRetry = 2000;
    private long queryDuration = 5000;
    private int maxCancelRetry = 3;
    private long cancelDuration = 2000;
    private long heartbeatDelay = 5;
    private long heartbeatDuration = 900;
    // 签名方式
    public static String sign_type = "RSA";

    // 调试用，创建TXT日志文件夹路径，见AlipayCore.java类中的logResult(String sWord)打印方法。
    public static String log_path = "C:\\";

    // 字符编码格式 目前支持 gbk 或 utf-8
    public static String input_charset = "utf-8";

    // 支付类型 ，无需修改
    public static String payment_type = "1";

    // 即时到账接口
    public static String CREATE_DIRECT_PAY_BY_USER = "create_direct_pay_by_user";
    // 即时到账接口
    public static String SINGLE_TRADE_QUERY = "single_trade_query";
//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

//↓↓↓↓↓↓↓↓↓↓ 请在这里配置防钓鱼信息，如果没开通防钓鱼功能，为空即可 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 防钓鱼时间戳  若要使用请调用类文件submit中的query_timestamp函数
    public static String anti_phishing_key = "";

    // 客户端的IP地址 非局域网的外网IP地址，如：221.0.0.1
    public static String exter_invoke_ip = "";

    //↑↑↑↑↑↑↑↑↑↑请在这里配置防钓鱼信息，如果没开通防钓鱼功能，为空即可 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
    public AliPayConfigs() {
    }

    public AliPayConfigs initAliPayConfigs(String openApiDomain, String pid, String md5Key, String appid, String
            privateKey, String publicKey, String alipayPublicKey) {
        this.openApiDomain = openApiDomain;
        this.pid = pid;
        this.appid = appid;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.alipayPublicKey = alipayPublicKey;
        this.md5Key = md5Key;
        return this;
    }

    public String description() {
        StringBuilder sb = new StringBuilder("AliPayConfigs{");
        sb.append("支付宝openapi网关: ").append(this.openApiDomain).append("\n");
        if (StringUtils.isNotEmpty(this.mcloudApiDomain)) {
            sb.append(", 支付宝mcloudapi网关域名: ").append(this.mcloudApiDomain).append("\n");
        }

        if (StringUtils.isNotEmpty(this.pid)) {
            sb.append(", pid: ").append(this.pid).append("\n");
        }

        sb.append(", appid: ").append(this.appid).append("\n");
        sb.append(", 商户RSA私钥: ").append(getKeyDescription(this.privateKey)).append("\n");
        sb.append(", 商户RSA公钥: ").append(getKeyDescription(this.publicKey)).append("\n");
        sb.append(", 支付宝RSA公钥: ").append(getKeyDescription(this.alipayPublicKey)).append("\n");
        sb.append(", 查询重试次数: ").append(this.maxQueryRetry).append("\n");
        sb.append(", 查询间隔(毫秒): ").append(this.queryDuration).append("\n");
        sb.append(", 撤销尝试次数: ").append(this.maxCancelRetry).append("\n");
        sb.append(", 撤销重试间隔(毫秒): ").append(this.cancelDuration).append("\n");
        sb.append(", 交易保障调度延迟(秒): ").append(this.heartbeatDelay).append("\n");
        sb.append(", 交易保障调度间隔(秒): ").append(this.heartbeatDuration).append("\n");
        sb.append("}");
        return sb.toString();
    }

    public String getKeyDescription(String key) {
        byte showLength = 10;
        return StringUtils.isNotEmpty(key) ? key.substring(0, showLength) + "******" + key.substring(key.length() - showLength) : null;
    }


    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        AliPayConfigs.log = log;
    }


    public String getOpenApiDomain() {
        return openApiDomain;
    }

    public void setOpenApiDomain(String openApiDomain) {
        this.openApiDomain = openApiDomain;
    }

    public String getMcloudApiDomain() {
        return mcloudApiDomain;
    }

    public void setMcloudApiDomain(String mcloudApiDomain) {
        this.mcloudApiDomain = mcloudApiDomain;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }

    public int getMaxQueryRetry() {
        return maxQueryRetry;
    }

    public void setMaxQueryRetry(int maxQueryRetry) {
        this.maxQueryRetry = maxQueryRetry;
    }

    public long getQueryDuration() {
        return queryDuration;
    }

    public void setQueryDuration(long queryDuration) {
        this.queryDuration = queryDuration;
    }

    public int getMaxCancelRetry() {
        return maxCancelRetry;
    }

    public void setMaxCancelRetry(int maxCancelRetry) {
        this.maxCancelRetry = maxCancelRetry;
    }

    public long getCancelDuration() {
        return cancelDuration;
    }

    public void setCancelDuration(long cancelDuration) {
        this.cancelDuration = cancelDuration;
    }

    public long getHeartbeatDelay() {
        return heartbeatDelay;
    }

    public void setHeartbeatDelay(long heartbeatDelay) {
        this.heartbeatDelay = heartbeatDelay;
    }

    public long getHeartbeatDuration() {
        return heartbeatDuration;
    }

    public void setHeartbeatDuration(long heartbeatDuration) {
        this.heartbeatDuration = heartbeatDuration;
    }

    public String getMd5Key() {
        return md5Key;
    }

    public void setMd5Key(String md5Key) {
        this.md5Key = md5Key;
    }
}
