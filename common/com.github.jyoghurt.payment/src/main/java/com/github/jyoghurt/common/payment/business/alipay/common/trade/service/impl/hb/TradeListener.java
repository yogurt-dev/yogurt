package com.github.jyoghurt.common.payment.business.alipay.common.trade.service.impl.hb;

/**
 * Created by liuyangkly on 15/10/27.
 */
public interface TradeListener {

    // 支付成功
    public void onPayTradeSuccess(final String outTradeNo, final long beforeCall);

    // 支付处理中
    public void onPayInProgress(final String outTradeNo, final long beforeCall);

    // 支付失败
    public void onPayFailed(final String outTradeNo, final long beforeCall);

    // 建立连接异常
    public void onConnectException(final String outTradeNo, final long beforeCall);

    // 报文上送异常
    public void onSendException(final String outTradeNo, final long beforeCall);

    // 报文接收异常
    public void onReceiveException(final String outTradeNo, final long beforeCall);
}
