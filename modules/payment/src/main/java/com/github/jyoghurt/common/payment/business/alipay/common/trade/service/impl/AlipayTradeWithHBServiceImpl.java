package com.github.jyoghurt.common.payment.business.alipay.common.trade.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.TradeStatus;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.builder.AlipayTradePayContentBuilder;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.result.AlipayF2FPayResult;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.config.AliPayConfigs;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.config.Constants;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.service.impl.hb.HbListener;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.service.impl.hb.TradeListener;
import org.apache.commons.lang.StringUtils;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;


/**
 * Created by liuyangkly on 15/7/29.
 *
 *  一定要在创建AlipayTradeService之前调用Configs.init("alipayrisk10");设置参数
 *
 */
public class AlipayTradeWithHBServiceImpl extends AbsAlipayTradeService {
    private TradeListener listener;

    public static class ClientBuilder {
        private String gatewayUrl;
        private String appid;
        private String privateKey;
        private String format;
        private String charset;
        private String alipayPublicKey;
        private TradeListener listener;

        public AlipayTradeWithHBServiceImpl build(AliPayConfigs aliPayConfigs) {
            if (StringUtils.isEmpty(gatewayUrl)) {
                gatewayUrl = aliPayConfigs.getOpenApiDomain(); // 与mcloudmonitor网关地址不同
            }
            if (StringUtils.isEmpty(appid)) {
                appid = aliPayConfigs.getAppid();
            }
            if (StringUtils.isEmpty(privateKey)) {
                privateKey = aliPayConfigs.getPrivateKey();
            }
            if (StringUtils.isEmpty(format)) {
                format = "json";
            }
            if (StringUtils.isEmpty(charset)) {
                charset = "utf-8";
            }
            if (StringUtils.isEmpty(alipayPublicKey)) {
                alipayPublicKey = aliPayConfigs.getAlipayPublicKey();
            }
            if (listener == null) {
                listener = new HbListener();  // 默认监听器
            }

            return new AlipayTradeWithHBServiceImpl(this);
        }

        public TradeListener getListener() {
            return listener;
        }

        public ClientBuilder setListener(TradeListener listener) {
            this.listener = listener;
            return this;
        }

        public ClientBuilder setAlipayPublicKey(String alipayPublicKey) {
            this.alipayPublicKey = alipayPublicKey;
            return this;
        }

        public ClientBuilder setAppid(String appid) {
            this.appid = appid;
            return this;
        }

        public ClientBuilder setCharset(String charset) {
            this.charset = charset;
            return this;
        }

        public ClientBuilder setFormat(String format) {
            this.format = format;
            return this;
        }

        public ClientBuilder setGatewayUrl(String gatewayUrl) {
            this.gatewayUrl = gatewayUrl;
            return this;
        }

        public ClientBuilder setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public String getAlipayPublicKey() {
            return alipayPublicKey;
        }

        public String getAppid() {
            return appid;
        }

        public String getCharset() {
            return charset;
        }

        public String getFormat() {
            return format;
        }

        public String getGatewayUrl() {
            return gatewayUrl;
        }

        public String getPrivateKey() {
            return privateKey;
        }
    }

    public AlipayTradeWithHBServiceImpl(ClientBuilder builder) {
        if (StringUtils.isEmpty(builder.getGatewayUrl())) {
            throw new NullPointerException("gatewayUrl should not be NULL!");
        }
        if (StringUtils.isEmpty(builder.getAppid())) {
            throw new NullPointerException("appid should not be NULL!");
        }
        if (StringUtils.isEmpty(builder.getPrivateKey())) {
            throw new NullPointerException("privateKey should not be NULL!");
        }
        if (StringUtils.isEmpty(builder.getFormat())) {
            throw new NullPointerException("format should not be NULL!");
        }
        if (StringUtils.isEmpty(builder.getCharset())) {
            throw new NullPointerException("charset should not be NULL!");
        }
        if (StringUtils.isEmpty(builder.getAlipayPublicKey())) {
            throw new NullPointerException("alipayPublicKey should not be NULL!");
        }
        if (builder.getListener() == null) {
            throw new NullPointerException("listener should not be NULL!");
        }

        listener = builder.getListener();
        client = new DefaultAlipayClient(builder.getGatewayUrl(), builder.getAppid(), builder.getPrivateKey(),
                builder.getFormat(), builder.getCharset(), builder.getAlipayPublicKey());
    }

    private AlipayTradePayResponse getResponse(AlipayClient client, AlipayTradePayRequest request,
                                                 final String outTradeNo, final long beforeCall) {
        try {
            AlipayTradePayResponse response = client.execute(request);
            if (response != null) {
                log.info(response.getBody());
            }
            return response;

        } catch (AlipayApiException e) {
            // 获取异常真实原因
            Throwable cause = e.getCause();

            if (cause instanceof ConnectException ||
                    cause instanceof NoRouteToHostException) {
                // 建立连接异常
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        listener.onConnectException(outTradeNo, beforeCall);
                    }
                });

            } else if (cause instanceof SocketException) {
                // 报文上送异常
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSendException(outTradeNo, beforeCall);
                    }
                });

            } else if (cause instanceof SocketTimeoutException) {
                // 报文接收异常
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        listener.onReceiveException(outTradeNo, beforeCall);
                    }
                });
            }

            e.printStackTrace();
            return null;
        }
    }

    // 商户可以直接使用的pay方法，并且集成了监控代码
    @Override
    public AlipayF2FPayResult tradePay(AlipayTradePayContentBuilder builder, AliPayConfigs aliPayConfigs) {
        validateBuilder(builder);

        final String outTradeNo = builder.getOutTradeNo();

        AlipayTradePayRequest request = new AlipayTradePayRequest();
        request.setBizContent(builder.toJsonString());
        log.info("trade.pay bizContent:" + request.getBizContent());

        // 首先调用支付api
        final long beforeCall = System.currentTimeMillis();
        AlipayTradePayResponse response = getResponse(client, request, outTradeNo, beforeCall);

        AlipayF2FPayResult result = new AlipayF2FPayResult(response);
        if (response != null && Constants.SUCCESS.equals(response.getCode())) {
            // 支付交易明确成功
            result.setTradeStatus(TradeStatus.SUCCESS);

            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    listener.onPayTradeSuccess(outTradeNo, beforeCall);
                }
            });

        } else if (response != null && Constants.PAYING.equals(response.getCode())) {
            // 返回支付中，同步一次交易耗时，后续会根据最终结果再同步一次交易耗时
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    listener.onPayInProgress(outTradeNo, beforeCall);
                }
            });

            // 返回用户处理中，则轮询查询交易是否成功，如果查询超时，则调用撤销
            AlipayTradeQueryResponse loopQueryResponse = loopQueryResult(outTradeNo,aliPayConfigs);
            return checkQueryAndCancel(outTradeNo, result, loopQueryResponse, beforeCall,aliPayConfigs);

        } else if (tradeError(response)) {
            // 因为系统错误，不清楚是失败还是成功，所以此处不同步交易信息

            // 系统错误，则查询一次交易，如果交易没有支付成功，则调用撤销
            AlipayTradeQueryResponse queryResponse = tradeQuery(outTradeNo);
            return checkQueryAndCancel(outTradeNo, result, queryResponse, beforeCall,aliPayConfigs);

        } else {
            // 其他情况表明该订单支付明确失败
            result.setTradeStatus(TradeStatus.FAILED);

            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    listener.onPayFailed(outTradeNo, beforeCall);
                }
            });
        }

        return result;
    }

    // 根据查询结果queryResponse判断交易是否支付成功，如果支付成功则更新result并返回，如果不成功则调用撤销
    private AlipayF2FPayResult checkQueryAndCancel(final String outTradeNo, AlipayF2FPayResult result,
                                                   AlipayTradeQueryResponse queryResponse, final long beforeCall,
                                                   AliPayConfigs aliPayConfigs) {
        if (querySuccess(queryResponse)) {
            // 如果查询返回支付成功，则返回相应结果
            result.setTradeStatus(TradeStatus.SUCCESS);
            result.setResponse(toPayResponse(queryResponse));

            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    listener.onPayTradeSuccess(outTradeNo, beforeCall);
                }
            });
            return result;
        }

        // 支付不成功，同步失败交易信息
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                listener.onPayFailed(outTradeNo, beforeCall);
            }
        });

        // 如果查询结果不为成功，则调用撤销
        AlipayTradeCancelResponse cancelResponse = cancelPayResult(outTradeNo,aliPayConfigs);
        if (tradeError(cancelResponse)) {
            // 如果第一次同步撤销返回异常，则标记支付交易为未知状态
            result.setTradeStatus(TradeStatus.UNKNOWN);

        } else {
            // 标记支付为失败，如果撤销未能成功，产生的单边帐由人工处理
            result.setTradeStatus(TradeStatus.FAILED);
        }

        return result;
    }
}
