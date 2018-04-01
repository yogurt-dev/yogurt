package com.github.jyoghurt.common.payment.business.alipay.service.impl;


import com.github.jyoghurt.common.payment.business.alipay.common.constants.AliPayConstants;
import com.github.jyoghurt.common.payment.business.alipay.common.utils.AlipayCommonUtil;
import com.github.jyoghurt.common.payment.business.alipay.service.AlipayCallBackService;
import com.github.jyoghurt.common.payment.business.tencent.common.constants.TencentPaymentConstants;
import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.exception.PaymentRepeatException;
import com.github.jyoghurt.common.payment.common.module.BaseCallBackParam;
import com.github.jyoghurt.common.payment.common.service.PaymentRecordsService;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.config.AliPayConfigs;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentResultTypeEnum;
import com.github.jyoghurt.core.exception.BaseErrorException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

/**
 * user: dell
 * date:2016/3/14.
 */
@Service("alipayCallBackService")
public class AlipayCallBackServiceImpl implements AlipayCallBackService {
    private static Logger logger = LoggerFactory.getLogger(AlipayCallBackServiceImpl.class);
    @Autowired
    private PaymentRecordsService paymentRecordsService;
    @Override
    public void parserAliPayCallBack(HttpServletRequest request, HttpServletResponse response) {
        String logStr;
        String name;
        JSONObject requestParamObj = new JSONObject();
        // 参数Map
        Map properties = request.getParameterMap();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (String eachValue : values) {
                    value = eachValue + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();
            }
            requestParamObj.put(name, value);
        }
        logStr = requestParamObj.toString();
        if (StringUtils.isEmpty(requestParamObj.get(AliPayConstants.notify_id)) ||
                StringUtils.isEmpty(requestParamObj.get(AliPayConstants.out_trade_no)) ||
                StringUtils.isEmpty(requestParamObj.get(AliPayConstants.trade_no)) ||
                StringUtils.isEmpty(requestParamObj.get("total_amount"))) {
            logger.info("支付宝回调接口验证失败，记录内容:{},失败原因：{}", logStr, "支付宝回调缺少参数");
            return;
        }
        //查找该订单是否已经接收过数据如果接收过则不再接收
        PaymentRecordsT hisPaymentRecords = paymentRecordsService.find(requestParamObj
                .get(AliPayConstants.out_trade_no).toString());
        //封装回调response
        response.setContentType("text/xml");
        response.reset();
        PrintWriter writer;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            throw new BaseErrorException("解析支付宝返回消息异常,微信返回信息{}", e.getMessage());
        }
        //验证签名 若签名出错则说明为被篡改的信息 不接受
        //验证订单并不存在  返回给微信支付  不再接收消息
        if (null == hisPaymentRecords) {
            logger.error("支付宝回调工单不存在,参数详细信息:{}", logStr);
            writer.write(AliPayConstants.RESPONSE_SUCCESS);
            return;
        }
        AliPayConfigs aliPayConfigs = new AliPayConfigs();
        aliPayConfigs = AlipayCommonUtil.initAliPayConfigs(hisPaymentRecords);
        //验证报文
        if (!AlipayCommonUtil.checkCallBack(requestParamObj.get(AliPayConstants.notify_id).toString(), aliPayConfigs)) {
            return;
        }

        //验证是否接收过 若接收过则返回 不再接收
        if (hisPaymentRecords.getResponseState()) {
            writer.write(AliPayConstants.RESPONSE_SUCCESS);
            return;
        }
        //若已被轮询同步 则response.SUCCESS
        if (hisPaymentRecords.getPaymentState()) {
            writer.write(TencentPaymentConstants.RESPONSE_SUCCESS);
            return;
        }
        //封装基础返回数据
        BaseCallBackParam baseCallBackParam = new BaseCallBackParam();
        baseCallBackParam.setPaymentResultTypeEnum(PaymentResultTypeEnum.SUCCESS);
        baseCallBackParam.setTotalFee(new BigDecimal(requestParamObj.get("total_amount").toString()));
        baseCallBackParam.setPaymentGatewayEnum(PaymentGatewayEnum.ALI_PAY);
        baseCallBackParam.setPaymentId(requestParamObj.get(AliPayConstants.out_trade_no).toString());
        baseCallBackParam.setDataArea(hisPaymentRecords.getDataAreaMap());
        hisPaymentRecords.setTransactionId(requestParamObj.get(AliPayConstants.trade_no).toString());
        if (null == hisPaymentRecords.getPaymentBusinessType()) {
            logger.error("支付记录中未保存业务类型,无法调用业务接口,参数详细信息:{}", logStr);
            return;
        }
        baseCallBackParam.setCallBackService(hisPaymentRecords.getPaymentBusinessType().getServiceName());
        //调用支付完成
        try {
            paymentRecordsService.finishPaymentRecord(hisPaymentRecords);
        } catch (PaymentRepeatException e) {
            logger.info("支付已完成,支付记录Id:{}", hisPaymentRecords.getPaymentId());
        }
    }

}
