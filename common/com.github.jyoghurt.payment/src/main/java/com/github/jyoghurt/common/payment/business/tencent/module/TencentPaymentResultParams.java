package com.github.jyoghurt.common.payment.business.tencent.module;

import com.github.jyoghurt.common.payment.business.tencent.enums.TencentTypeEnum;
import com.github.jyoghurt.common.payment.common.module.BasePaymentResultParams;

/**
 * user: dell
 * date:2016/3/13.
 */
public class TencentPaymentResultParams extends BasePaymentResultParams {
    /**
     * 微信二维码
     */
    private String qrCode;
    /**
     * 交易类型
     */
    private TencentTypeEnum tradeType;
    /**
     * 预支付交易会话标识
     * 微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时
     */
    private String prepayId;

    public TencentTypeEnum getTradeType() {
        return tradeType;
    }

    public void setTradeType(TencentTypeEnum tradeType) {
        this.tradeType = tradeType;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
