package com.github.jyoghurt.common.payment.business.alipay.common.trade.model.builder;


import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.ExtendParams;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.GoodsDetail;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by liuyangkly on 15/7/1.
 */
public class AlipayTradePrecreateContentBuilder extends RequestBuilder {
    // 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
    // 需保证商户系统端不能重复，建议通过数据库sequence生成，
    @SerializedName("out_trade_no")
    private String outTradeNo;

    // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
    // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
    @SerializedName("seller_id")
    private String sellerId;

    // 订单总金额，整形，此处单位为元，精确到小数点后2位，不能超过1亿元
    // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
    @SerializedName("total_amount")
    private String totalAmount;

    // 订单可打折金额，此处单位为元，精确到小数点后2位
    // 可以配合商家平台配置折扣活动，如果订单部分商品参与打折，可以将部分商品总价填写至此字段，默认全部商品可打折
    // 如果该值未传入,但传入了【订单总金额】,【不可打折金额】 则该值默认为【订单总金额】- 【不可打折金额】
    @SerializedName("discountable_amount")
    private String discountableAmount;

    // 订单不可打折金额，此处单位为元，精确到小数点后2位，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
    // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
    @SerializedName("undiscountable_amount")
    private String undiscountableAmount;

    // 订单标题，粗略描述用户的支付目的。如“喜士多（浦东店）消费”
    private String subject;

    // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
    private String body;

    // 商品明细列表，需填写购买商品详细信息，
    @SerializedName("goods_detail")
    private List<GoodsDetail> goodsDetailList;

    // 商户操作员编号，添加此参数可以为商户操作员做销售统计
    @SerializedName("operator_id")
    private String operatorId;

    // 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
    @SerializedName("store_id")
    private String storeId;

    // 支付宝商家平台中配置的商户门店号，详询支付宝技术支持
    @SerializedName("alipay_store_id")
    private String alipayStoreId;

    // 商户机具终端编号，当以机具方式接入支付宝时必传，详询支付宝技术支持
    @SerializedName("terminal_id")
    private String terminalId;

    // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
    @SerializedName("extend_params")
    private ExtendParams extendParams;

    // (不推荐使用，绝对时间不准确) 支付超时时间，该笔订单允许的最晚付款时间,逾期支付宝将主动关闭交易,格式为: yyyy-MM-dd HH:mm:ss
    // 建议不超过交易发生后30分钟，如果为空则使用支付宝默认的超时时间，时间较长
    @SerializedName("time_expire")
    private String timeExpire;

    // (推荐使用，相对时间) 支付超时时间，5m 5分钟
    @SerializedName("timeout_express")
    private String timeExpress;
    //回调链接
    @SerializedName("notify_url")
    private String notifyUrl;
    @Override
    public boolean validate() {
        if (StringUtils.isEmpty(outTradeNo)) {
            throw new NullPointerException("out_trade_no should not be NULL!");
        }
        if (StringUtils.isEmpty(totalAmount)) {
            throw new NullPointerException("total_amount should not be NULL!");
        }
        if (StringUtils.isEmpty(subject)) {
            throw new NullPointerException("subject should not be NULL!");
        }
        if (StringUtils.isEmpty(storeId)) {
            throw new NullPointerException("store_id should not be NULL!");
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlipayTradePrecreateContentBuilder{");
        sb.append("outTradeNo='").append(outTradeNo).append('\'');
        sb.append(", sellerId='").append(sellerId).append('\'');
        sb.append(", totalAmount='").append(totalAmount).append('\'');
        sb.append(", discountableAmount='").append(discountableAmount).append('\'');
        sb.append(", undiscountableAmount='").append(undiscountableAmount).append('\'');
        sb.append(", subject='").append(subject).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append(", goodsDetailList=").append(goodsDetailList);
        sb.append(", operatorId='").append(operatorId).append('\'');
        sb.append(", storeId='").append(storeId).append('\'');
        sb.append(", alipayStoreId='").append(alipayStoreId).append('\'');
        sb.append(", terminalId='").append(terminalId).append('\'');
        sb.append(", extendParams=").append(extendParams);
        if (StringUtils.isNotEmpty(timeExpire)) {
            sb.append(", timeExpire='").append(timeExpire).append('\'');
        }
        if (StringUtils.isNotEmpty(notifyUrl)) {
            sb.append(", notifyUrl='").append(notifyUrl).append('\'');
        }
        sb.append(", timeExpress='").append(timeExpress).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public AlipayTradePrecreateContentBuilder setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
        return this;
    }

    public String getSellerId() {
        return sellerId;
    }

    public AlipayTradePrecreateContentBuilder setSellerId(String sellerId) {
        this.sellerId = sellerId;
        return this;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public AlipayTradePrecreateContentBuilder setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public String getDiscountableAmount() {
        return discountableAmount;
    }

    public AlipayTradePrecreateContentBuilder setDiscountableAmount(String discountableAmount) {
        this.discountableAmount = discountableAmount;
        return this;
    }

    public String getUndiscountableAmount() {
        return undiscountableAmount;
    }

    public AlipayTradePrecreateContentBuilder setUndiscountableAmount(String undiscountableAmount) {
        this.undiscountableAmount = undiscountableAmount;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public AlipayTradePrecreateContentBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getBody() {
        return body;
    }

    public AlipayTradePrecreateContentBuilder setBody(String body) {
        this.body = body;
        return this;
    }

    public List<GoodsDetail> getGoodsDetailList() {
        return goodsDetailList;
    }

    public AlipayTradePrecreateContentBuilder setGoodsDetailList(List<GoodsDetail> goodsDetailList) {
        this.goodsDetailList = goodsDetailList;
        return this;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public AlipayTradePrecreateContentBuilder setOperatorId(String operatorId) {
        this.operatorId = operatorId;
        return this;
    }

    public String getStoreId() {
        return storeId;
    }

    public AlipayTradePrecreateContentBuilder setStoreId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public String getAlipayStoreId() {
        return alipayStoreId;
    }

    public AlipayTradePrecreateContentBuilder setAlipayStoreId(String alipayStoreId) {
        this.alipayStoreId = alipayStoreId;
        return this;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public AlipayTradePrecreateContentBuilder setTerminalId(String terminalId) {
        this.terminalId = terminalId;
        return this;
    }

    public ExtendParams getExtendParams() {
        return extendParams;
    }

    public AlipayTradePrecreateContentBuilder setExtendParams(ExtendParams extendParams) {
        this.extendParams = extendParams;
        return this;
    }

    public String getTimeExpire() {
        return timeExpire;
    }

    public AlipayTradePrecreateContentBuilder setTimeExpire(String timeExpire) {
        this.timeExpire = timeExpire;
        return this;
    }

    public String getTimeExpress() {
        return timeExpress;
    }

    public AlipayTradePrecreateContentBuilder setTimeExpress(String timeExpress) {
        this.timeExpress = timeExpress;
        return this;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public AlipayTradePrecreateContentBuilder setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
        return this;
    }
}
