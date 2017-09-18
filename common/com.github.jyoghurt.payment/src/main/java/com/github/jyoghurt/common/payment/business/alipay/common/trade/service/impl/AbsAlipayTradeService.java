package com.github.jyoghurt.common.payment.business.alipay.common.trade.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.*;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.TradeStatus;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.builder.AlipayTradeQueryCententBuilder;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.result.AlipayF2FRefundResult;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.service.AlipayTradeService;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.config.AliPayConfigs;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.config.Constants;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.builder.AlipayTradePrecreateContentBuilder;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.builder.AlipayTradeRefundContentBuilder;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.result.AlipayF2FPrecreateResult;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.result.AlipayF2FQueryResult;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.utils.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Created by liuyangkly on 15/10/28.
 */
abstract class AbsAlipayTradeService extends AbsAlipayService implements AlipayTradeService {
    protected static ExecutorService executorService = Executors.newCachedThreadPool();
    protected AlipayClient client;

    @Override
    public AlipayF2FQueryResult queryTradeResult(String outTradeNo) {
        AlipayTradeQueryResponse response = tradeQuery(outTradeNo);

        AlipayF2FQueryResult result = new AlipayF2FQueryResult(response);
        if (querySuccess(response)) {
            // 查询返回该订单交易支付成功
            result.setTradeStatus(TradeStatus.SUCCESS);

        } else if (tradeError(response)) {
            // 查询发生异常，交易状态未知
            result.setTradeStatus(TradeStatus.UNKNOWN);

        } else {
            // 其他情况均表明该订单号交易失败
            result.setTradeStatus(TradeStatus.FAILED);
        }
        return result;
    }

    protected AlipayTradeQueryResponse tradeQuery(String outTradeNo) {
        AlipayTradeQueryCententBuilder builder = new AlipayTradeQueryCententBuilder()
                .setOutTradeNo(outTradeNo);
        validateBuilder(builder);

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent(builder.toJsonString());
        log.info("trade.query bizContent:" + request.getBizContent());

        return (AlipayTradeQueryResponse) getResponse(client, request);
    }

    @Override
    public AlipayF2FRefundResult tradeRefund(AlipayTradeRefundContentBuilder builder) {
        validateBuilder(builder);

        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizContent(builder.toJsonString());
        log.info("trade.refund bizContent:" + request.getBizContent());

        AlipayTradeRefundResponse response = (AlipayTradeRefundResponse) getResponse(client, request);

        AlipayF2FRefundResult result = new AlipayF2FRefundResult(response);
        if (response != null && Constants.SUCCESS.equals(response.getCode())) {
            // 退货交易成功
            result.setTradeStatus(TradeStatus.SUCCESS);

        } else if (tradeError(response)) {
            // 退货发生异常，退货状态未知
            result.setTradeStatus(TradeStatus.UNKNOWN);

        } else {
            // 其他情况表明该订单退货明确失败
            result.setTradeStatus(TradeStatus.FAILED);
        }
        return result;
    }

    @Override
    public AlipayF2FPrecreateResult tradePrecreate(AlipayTradePrecreateContentBuilder builder) {
        validateBuilder(builder);

        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setBizContent(builder.toJsonString());
        request.setNotifyUrl(builder.getNotifyUrl());
        log.info("trade.precreate bizContent:" + request.getBizContent());

        AlipayTradePrecreateResponse response = (AlipayTradePrecreateResponse) getResponse(client, request);

        AlipayF2FPrecreateResult result = new AlipayF2FPrecreateResult(response);
        if (response != null && Constants.SUCCESS.equals(response.getCode())) {
            // 预下单交易成功
            result.setTradeStatus(TradeStatus.SUCCESS);

        } else if (tradeError(response)) {
            // 预下单发生异常，状态未知
            result.setTradeStatus(TradeStatus.UNKNOWN);

        } else {
            // 其他情况表明该预下单明确失败
            result.setTradeStatus(TradeStatus.FAILED);
        }
        return result;
    }

    // 根据外部订单号outTradeNo撤销订单
    protected AlipayTradeCancelResponse tradeCancel(String outTradeNo) {
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        String bizContent = new StringBuilder("{'out_trade_no':'").append(outTradeNo).append("'}").toString();
        request.setBizContent(bizContent);
        log.info("trade.cancel bizContent:" + request.getBizContent());

        return (AlipayTradeCancelResponse) getResponse(client, request);
    }

    // 轮询查询订单支付结果
    protected AlipayTradeQueryResponse loopQueryResult(String outTradeNo,AliPayConfigs aliPayConfigs) {
        AlipayTradeQueryResponse queryResult = null;
        for (int i = 0; i < aliPayConfigs.getMaxQueryRetry(); i++) {
            Utils.sleep(aliPayConfigs.getQueryDuration());

            AlipayTradeQueryResponse response = tradeQuery(outTradeNo);
            if (response != null) {
                if (stopQuery(response)) {
                    return response;
                }
                queryResult = response;
            }
        }
        return queryResult;
    }

    // 判断是否停止查询
    protected boolean stopQuery(AlipayTradeQueryResponse response) {
        if (Constants.SUCCESS.equals(response.getCode())) {
            if ("TRADE_FINISHED".equals(response.getTradeStatus()) ||
                    "TRADE_SUCCESS".equals(response.getTradeStatus()) ||
                    "TRADE_CLOSED".equals(response.getTradeStatus())) {
                // 如果查询到交易成功、交易结束、交易关闭，则返回对应结果
                return true;
            }
        }
        return false;
    }

    // 根据外部订单号outTradeNo撤销订单
    protected AlipayTradeCancelResponse cancelPayResult(String outTradeNo,AliPayConfigs aliPayConfigs) {
        AlipayTradeCancelResponse response = tradeCancel(outTradeNo);
        if (cancelSuccess(response)) {
            // 如果撤销成功，则返回撤销结果
            return response;
        }

        // 撤销失败
        if (needRetry(response)) {
            // 如果需要重试，首先记录日志，然后调用异步撤销
            log.warn("begin async cancel outTradeNo:" + outTradeNo);
            asyncCancel(outTradeNo,aliPayConfigs);
        }
        return response;
    }

    // 异步撤销
    protected void asyncCancel(final String outTradeNo,AliPayConfigs aliPayConfigs) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < aliPayConfigs.getMaxCancelRetry(); i++) {
                    Utils.sleep(aliPayConfigs.getCancelDuration());

                    AlipayTradeCancelResponse response = tradeCancel(outTradeNo);
                    if (cancelSuccess(response) ||
                            !needRetry(response)) {
                        // 如果撤销成功或者应答告知不需要重试撤销，则返回撤销结果（无论撤销是成功失败，失败人工处理）
                        return ;
                    }
                }
            }
        });
    }

    // 将查询应答转换为支付应答
    protected AlipayTradePayResponse toPayResponse(AlipayTradeQueryResponse response) {
        AlipayTradePayResponse payResponse = new AlipayTradePayResponse();
        // 只有查询明确返回成功才能将返回码设置为10000，否则均为失败
        payResponse.setCode(querySuccess(response) ? Constants.SUCCESS : Constants.FAILED);
        // 补充交易状态信息
        StringBuilder msg = new StringBuilder(response.getMsg())
                .append(" tradeStatus:")
                .append(response.getTradeStatus());
        payResponse.setMsg(msg.toString());
        payResponse.setSubCode(response.getSubCode());
        payResponse.setSubMsg(response.getSubMsg());
        payResponse.setBody(response.getBody());
        payResponse.setParams(response.getParams());

        // payResponse应该是交易支付时间，但是response里是本次交易打款给卖家的时间,是否有问题
        // payResponse.setGmtPayment(response.getSendPayDate());
        payResponse.setBuyerLogonId(response.getBuyerLogonId());
        payResponse.setFundBillList(response.getFundBillList());
        payResponse.setOpenId(response.getOpenId());
        payResponse.setOutTradeNo(response.getOutTradeNo());
        payResponse.setReceiptAmount(response.getReceiptAmount());
        payResponse.setTotalAmount(response.getTotalAmount());
        payResponse.setTradeNo(response.getTradeNo());
        return payResponse;
    }

    // 撤销需要重试
    protected boolean needRetry(AlipayTradeCancelResponse response) {
        return response == null ||
                "Y".equals(response.getRetryFlag());
    }

    // 查询返回“支付成功”
    protected boolean querySuccess(AlipayTradeQueryResponse response) {
        return response != null &&
                Constants.SUCCESS.equals(response.getCode()) &&
                ("TRADE_SUCCESS".equals(response.getTradeStatus()) ||
                        "TRADE_FINISHED".equals(response.getTradeStatus())
                );
    }

    // 撤销返回“撤销成功”
    protected boolean cancelSuccess(AlipayTradeCancelResponse response) {
        return response != null &&
                Constants.SUCCESS.equals(response.getCode());
    }

    // 交易异常，或发生系统错误
    protected boolean tradeError(AlipayResponse response) {
        return response == null ||
                Constants.ERROR.equals(response.getCode());
    }
}
