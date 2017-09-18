package com.github.jyoghurt.common.payment.business.alipay.common.trade.service;

import com.alipay.api.response.MonitorHeartbeatSynResponse;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.builder.AlipayHeartbeatSynContentBuilder;


/**
 * Created by liuyangkly on 15/10/22.
 */
public interface AlipayMonitorService {

    // 交易保障接口 https://openhome.alipay.com/platform/document.htm#mobileApp-barcodePay-API-heartBeat

    // 可以提供给pos厂商使用，或者系统商在无需传递app_auth_token的时候使用
    public MonitorHeartbeatSynResponse heartbeatSyn(AlipayHeartbeatSynContentBuilder builder);

    // 系统商isv如果app_auth_token非空，则使用此接口
    public MonitorHeartbeatSynResponse heartbeatSyn(AlipayHeartbeatSynContentBuilder builder, String appAuthToken);
}
