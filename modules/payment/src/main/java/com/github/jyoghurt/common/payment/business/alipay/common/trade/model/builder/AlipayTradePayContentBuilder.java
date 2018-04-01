package com.github.jyoghurt.common.payment.business.alipay.common.trade.model.builder;


import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.ExtendParams;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.GoodsDetail;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by liuyangkly on 15/6/26.
 */
public class AlipayTradePayContentBuilder extends AlipayTradePrecreateContentBuilder {
    // 支付场景，条码支付场景为bar_code
    private String scene;

    // 付款条码，用户支付宝钱包手机app点击“付款”产生的付款条码
    @SerializedName("auth_code")
    private String authCode;

    @Override
    public boolean validate() {
        if (StringUtils.isEmpty(scene)) {
            throw new NullPointerException("scene should not be NULL!");
        }
        if (StringUtils.isEmpty(authCode)) {
            throw new NullPointerException("auth_code should not be NULL!");
        }
        if (!Pattern.matches("^\\d{10,}$", authCode)) {
            throw new IllegalStateException("invalid auth_code!");
        }
        return super.validate();
    }

    public AlipayTradePayContentBuilder() {
        // 条码支付，场景为bar_code
        this.scene = "bar_code";
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlipayTradePayContentBuilder{");
        sb.append("scene='").append(scene).append('\'');
        sb.append(", authCode='").append(authCode).append('\'');
        sb.append(", outTradeNo='").append(getOutTradeNo()).append('\'');
        sb.append(", sellerId='").append(getSellerId()).append('\'');
        sb.append(", totalAmount='").append(getTotalAmount()).append('\'');
        sb.append(", discountableAmount='").append(getDiscountableAmount()).append('\'');
        sb.append(", undiscountableAmount='").append(getUndiscountableAmount()).append('\'');
        sb.append(", subject='").append(getSubject()).append('\'');
        sb.append(", body='").append(getBody()).append('\'');
        sb.append(", goodsDetailList=").append(getGoodsDetailList());
        sb.append(", operatorId='").append(getOperatorId()).append('\'');
        sb.append(", storeId='").append(getStoreId()).append('\'');
        sb.append(", alipayStoreId='").append(getAlipayStoreId()).append('\'');
        sb.append(", terminalId='").append(getTerminalId()).append('\'');
        sb.append(", extendParams=").append(getExtendParams());
        if (StringUtils.isNotEmpty(getTimeExpress())) {
            sb.append(", timeExpire='").append(getTimeExpire()).append('\'');
        }
        sb.append(", timeExpress='").append(getTimeExpire()).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getScene() {
        return scene;
    }

    public AlipayTradePayContentBuilder setScene(String scene) {
        this.scene = scene;
        return this;
    }

    public String getAuthCode() {
        return authCode;
    }

    public AlipayTradePayContentBuilder setAuthCode(String authCode) {
        this.authCode = authCode;
        return this;
    }

    @Override
    public AlipayTradePayContentBuilder setAlipayStoreId(String alipayStoreId) {
        return (AlipayTradePayContentBuilder) super.setAlipayStoreId(alipayStoreId);
    }

    @Override
    public AlipayTradePayContentBuilder setBody(String body) {
        return (AlipayTradePayContentBuilder) super.setBody(body);
    }

    @Override
    public AlipayTradePayContentBuilder setDiscountableAmount(String discountableAmount) {
        return (AlipayTradePayContentBuilder) super.setDiscountableAmount(discountableAmount);
    }

    @Override
    public AlipayTradePayContentBuilder setExtendParams(ExtendParams extendParams) {
        return (AlipayTradePayContentBuilder) super.setExtendParams(extendParams);
    }

    @Override
    public AlipayTradePayContentBuilder setGoodsDetailList(List<GoodsDetail> goodsDetailList) {
        return (AlipayTradePayContentBuilder) super.setGoodsDetailList(goodsDetailList);
    }

    @Override
    public AlipayTradePayContentBuilder setOperatorId(String operatorId) {
        return (AlipayTradePayContentBuilder) super.setOperatorId(operatorId);
    }

    @Override
    public AlipayTradePayContentBuilder setOutTradeNo(String outTradeNo) {
        return (AlipayTradePayContentBuilder) super.setOutTradeNo(outTradeNo);
    }

    @Override
    public AlipayTradePayContentBuilder setSellerId(String sellerId) {
        return (AlipayTradePayContentBuilder) super.setSellerId(sellerId);
    }

    @Override
    public AlipayTradePayContentBuilder setStoreId(String storeId) {
        return (AlipayTradePayContentBuilder) super.setStoreId(storeId);
    }

    @Override
    public AlipayTradePayContentBuilder setSubject(String subject) {
        return (AlipayTradePayContentBuilder) super.setSubject(subject);
    }

    @Override
    public AlipayTradePayContentBuilder setTerminalId(String terminalId) {
        return (AlipayTradePayContentBuilder) super.setTerminalId(terminalId);
    }

    @Override
    public AlipayTradePayContentBuilder setTimeExpire(String timeExpire) {
        return (AlipayTradePayContentBuilder) super.setTimeExpire(timeExpire);
    }

    @Override
    public AlipayTradePayContentBuilder setTotalAmount(String totalAmount) {
        return (AlipayTradePayContentBuilder) super.setTotalAmount(totalAmount);
    }

    @Override
    public AlipayTradePayContentBuilder setUndiscountableAmount(String undiscountableAmount) {
        return (AlipayTradePayContentBuilder) super.setUndiscountableAmount(undiscountableAmount);
    }

    @Override
    public AlipayTradePayContentBuilder setTimeExpress(String timeExpress) {
        return (AlipayTradePayContentBuilder) super.setTimeExpress(timeExpress);
    }
}
