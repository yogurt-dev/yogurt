package com.github.jyoghurt.common.payment.business.alipay.common.trade.model.builder;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.StringUtils;

/**
 * Created by liuyangkly on 15/6/27.
 */
public class AlipayTradeQueryCententBuilder extends RequestBuilder {
    // 支付宝交易号,和商户订单号不能同时为空, 如果同时存在则通过tradeNo查询支付宝交易
    @SerializedName("trade_no")
    private String tradeNo;

    // 商户订单号，通过此商户订单号查询当面付的交易状态
    @SerializedName("out_trade_no")
    private String outTradeNo;

    @Override
    public boolean validate() {
        if (StringUtils.isEmpty(tradeNo) &&
                StringUtils.isEmpty(outTradeNo)) {
            throw new IllegalStateException("tradeNo and outTradeNo can not both be NULL!");
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlipayTradeQueryCententBuilder{");
        sb.append("tradeNo='").append(tradeNo).append('\'');
        sb.append(", outTradeNo='").append(outTradeNo).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public AlipayTradeQueryCententBuilder setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
        return this;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public AlipayTradeQueryCententBuilder setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
        return this;
    }
}

