package com.github.jyoghurt.common.payment.business.tencent.service.impl;


import com.github.jyoghurt.common.payment.business.tencent.common.constants.TencentPaymentConstants;
import com.github.jyoghurt.common.payment.business.tencent.service.TencentCallBackService;
import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.exception.PaymentRepeatException;
import com.github.jyoghurt.common.payment.common.module.BaseCallBackParam;
import com.github.jyoghurt.common.payment.common.service.PaymentRecordsService;
import com.github.jyoghurt.common.payment.common.utils.XMLParserUtil;
import com.github.jyoghurt.core.exception.BaseErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;

/**
 * user: dell
 * date:2016/3/14.
 */
@Service("tencentCallBackService")
public class TencentCallBackServiceImpl implements TencentCallBackService {
    private static Logger logger = LoggerFactory.getLogger(TencentCallBackServiceImpl.class);
    @Autowired
    private PaymentRecordsService paymentRecordsService;

    @Override
    public void parserTencentCallBack(HttpServletRequest request, HttpServletResponse response) {
        String logStr = "";
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            while ((logStr = br.readLine()) != null) {
                sb.append(logStr);
            }
            logStr = sb.toString();
        } catch (IOException e) {
            throw new BaseErrorException("解析微信回调异常", e);
        }
        //解析报文  将报文转为Map
        Map<String, Object> callBackMap = XMLParserUtil.getMapFromXML(sb.toString());
        //封装基础返回数据
        BaseCallBackParam baseCallBackParam = new BaseCallBackParam();
        if (!TencentPaymentConstants.COMMUNICATE_SUCCESS.equals(callBackMap.get(TencentPaymentConstants.RETURN_CODE).toString())) {
            //通信失败
            logger.error("微信支付回调通信失败，参数详情:{}", logStr);
            return;
        }
        if (null == callBackMap.get(TencentPaymentConstants.OUT_TRADE_NO)) {
            logger.error("微信支付回调工单不存在，参数详情:{}", logStr);
            return;
        }
        //查找该订单是否已经接收过数据如果接收过则不再接收
        PaymentRecordsT hisPaymentRecords = paymentRecordsService.find(callBackMap.get(TencentPaymentConstants.OUT_TRADE_NO).toString());
        //验证签名 若签名出错则说明为被篡改的信息 不接受
        //封装回调response
        response.setContentType("text/xml");
        response.reset();
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            throw new BaseErrorException("获取微信回调response", e);
        }
        //验证订单并不存在  返回给微信支付  不再接收消息
        if (null == hisPaymentRecords) {
            logger.error("微信支付回调工单不存在,参数详细信息:{}", logStr);
            writer.write(TencentPaymentConstants.RESPONSE_SUCCESS);
            return;
        }
        //验证是否接收过 若接收过则返回 不再接收
        if (hisPaymentRecords.getResponseState()) {
            writer.write(TencentPaymentConstants.RESPONSE_SUCCESS);
            return;
        }
        //若已被轮询同步 则response.SUCCESS
        if (hisPaymentRecords.getPaymentState()) {
            writer.write(TencentPaymentConstants.RESPONSE_SUCCESS);
            return;
        }
        if (!TencentPaymentConstants.TRANSACTION_SUCCESS.equals(callBackMap.get(TencentPaymentConstants
                .RESULT_CODE).toString())) {
            return;
        }
        //调用支付完成
        try {
            paymentRecordsService.finishPaymentRecord(hisPaymentRecords);
        } catch (PaymentRepeatException e) {
            logger.info("支付已完成,支付记录Id:{}", hisPaymentRecords.getPaymentId());
        }
    }
}
