package com.github.jyoghurt.common.payment.business.alipay.common.trade.service;


import com.github.jyoghurt.common.payment.business.alipay.common.trade.config.AliPayConfigs;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.builder.AlipayTradePayContentBuilder;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.builder.AlipayTradePrecreateContentBuilder;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.builder.AlipayTradeRefundContentBuilder;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.result.AlipayF2FPayResult;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.result.AlipayF2FPrecreateResult;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.result.AlipayF2FQueryResult;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.result.AlipayF2FRefundResult;

/**
 * Created by liuyangkly on 15/7/29.
 */
public interface AlipayTradeService {

    // 当面付2.0流程支付
    public AlipayF2FPayResult tradePay(AlipayTradePayContentBuilder builder, AliPayConfigs aliPayConfigs);

    // 当面付2.0消费查询
    public AlipayF2FQueryResult queryTradeResult(String outTradeNo);

    // 当面付2.0消费退款
    public AlipayF2FRefundResult tradeRefund(AlipayTradeRefundContentBuilder builder);

    // 当面付2.0预下单(生成二维码)
    public AlipayF2FPrecreateResult tradePrecreate(AlipayTradePrecreateContentBuilder builder);
}
