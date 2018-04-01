package com.github.jyoghurt.common.payment.business.alipay.common.utils;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.github.jyoghurt.common.payment.business.alipay.module.AlipayTransferRequest;
import com.github.jyoghurt.core.exception.BaseErrorException;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: DELL
 * Date: 2017/9/25
 * Time: 14:37
 * To change this template use File | Settings | File Templates.
 */
public class AliPayTransferUtil {

    public static AlipayClient initClient(AlipayTransferRequest alipayTransferRequest) {
        AlipayClient client = new DefaultAlipayClient(
                "https://openapi.alipay.com/gateway.do",
                alipayTransferRequest.getApp_id(),
                alipayTransferRequest.getPrivateKey(),
                "json",
                "UTF-8",
                alipayTransferRequest.getPublicKey(),"RSA2");
        return client;
    }

    public static AlipayFundTransToaccountTransferRequest initRequest(AlipayTransferRequest alipayTransferRequest) {
        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        JSONObject content = new JSONObject();
        content.put("out_biz_no", alipayTransferRequest.getOut_biz_no());
        content.put("payee_type", alipayTransferRequest.getPayee_type());
        content.put("payee_account", alipayTransferRequest.getPayee_account());
        content.put("amount", alipayTransferRequest.getAmount());
        if (StringUtils.isNotEmpty(alipayTransferRequest.getPayer_show_name())) {
            content.put("payer_show_name", alipayTransferRequest.getPayer_show_name());
        }
        content.put("payee_real_name", alipayTransferRequest.getPayee_real_name());
        if (StringUtils.isNotEmpty(alipayTransferRequest.getRemark())) {
            content.put("remark", alipayTransferRequest.getRemark());
        }
        request.setBizContent(JSON.toJSONString(content));
        return request;
    }

    public static void transfer(AlipayTransferRequest alipayTransferRequest) {
        AlipayClient alipayClient = initClient(alipayTransferRequest);
        AlipayFundTransToaccountTransferRequest request = initRequest(alipayTransferRequest);
        try {
            AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);
           if(!response.isSuccess()){
               throw new BaseErrorException("支付宝接口调用异常,code:{0},msg:{1}",response.getSubCode(),response.getSubMsg());
           }
        } catch (AlipayApiException e) {
            throw new BaseErrorException(e);
        }
    }
}
