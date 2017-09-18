package com.github.jyoghurt.common.payment.business.tencent.common.support;

import com.alibaba.fastjson.JSON;
import com.github.jyoghurt.common.payment.business.tencent.common.config.TencentConfigure;
import com.github.jyoghurt.common.payment.business.tencent.common.constants.TencentPaymentConstants;
import com.github.jyoghurt.common.payment.business.tencent.enums.TencentTypeEnum;
import com.github.jyoghurt.common.payment.business.tencent.module.TencentNativePaymentParams;
import com.github.jyoghurt.common.payment.business.tencent.module.TencentRefundParams;
import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.service.PaymentRecordsService;
import com.github.jyoghurt.common.payment.common.utils.PaymerntStateConvertUtil;
import com.github.jyoghurt.common.payment.common.utils.XMLParserUtil;
import com.github.jyoghurt.http.util.HttpClientUtils;
import com.github.jyoghurt.common.payment.business.tencent.common.utils.TencentPayCommonUtil;
import com.github.jyoghurt.common.payment.business.tencent.common.utils.TencentSignatureUtil;
import com.github.jyoghurt.common.payment.business.tencent.module.TencentCancelParams;
import com.github.jyoghurt.common.payment.business.tencent.module.TencentQueryParams;
import com.github.jyoghurt.common.payment.business.tencent.support.TencentPaymentSupport;
import com.github.jyoghurt.common.payment.common.enums.PaymentCloseEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentRefundEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentStateEnum;
import com.github.jyoghurt.common.payment.common.exception.PaymentClosedException;
import com.github.jyoghurt.common.payment.common.exception.PaymentRepeatException;
import com.github.jyoghurt.common.payment.common.module.RefundRequest;
import com.github.jyoghurt.common.payment.common.utils.CommonUtil;
import com.github.jyoghurt.core.exception.BaseErrorException;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * user:zjl
 * date: 2016/5/31.
 */
@Component
public class TencentCommonSupport {
    private static Logger logger = LoggerFactory.getLogger(TencentPaymentSupport.class);

    @Autowired
    private PaymentRecordsService paymentRecordsService;
    @Value("${tencent.config.notify_url}")
    private String notify_url;

    /**
     * 微信扫码支付方法
     *
     * @param paymentRecordsT 支付记录
     * @return 支付结果
     */
    public Map<String, Object> tencentPayment(TencentTypeEnum tencentType, PaymentRecordsT paymentRecordsT) {
        String callbackServiceName = paymentRecordsT.getPaymentBusinessType().getServiceName();
        TencentConfigure tencentConfigure = TencentPayCommonUtil.initTencentPayConfigs(paymentRecordsT);
        //根据支付记录封装微信扫码支付实体
        TencentNativePaymentParams tencentNativePaymentParams = new TencentNativePaymentParams();
        if (!StringUtils.isEmpty(paymentRecordsT.getPaymentDetil())) {
            tencentNativePaymentParams.setBody(paymentRecordsT.getPaymentDetil());
        }
        tencentNativePaymentParams.setTotal_fee(paymentRecordsT.getTotleFee().multiply(new BigDecimal(100)).intValue());
        tencentNativePaymentParams.setOut_trade_no(paymentRecordsT.getPaymentId());
        //从数据字典中获取微信扫码支付数据
        tencentNativePaymentParams.setAppid(tencentConfigure.getAppID());
        tencentNativePaymentParams.setTrade_type(TencentTypeEnum.APPLET == tencentType
                ? TencentTypeEnum.JSAPI
                : tencentType);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TencentPaymentConstants.ATTACH_CALL_BACK_SERVICE, callbackServiceName);
        tencentNativePaymentParams.setAttach(JSON.toJSONString(jsonObject));
        tencentNativePaymentParams.setMch_id(tencentConfigure.getMchID());
        tencentNativePaymentParams.setNonce_str(CommonUtil.getRandomStr());
        tencentNativePaymentParams.setSub_mch_id(tencentConfigure.getSubMchID());
        if (TencentTypeEnum.JSAPI == tencentType) {
            Map<String, Object> vars = paymentRecordsT.getDataAreaMap();
            if (null == vars.get("tencentOpenId")) {
                throw new BaseErrorException("初始化微官网微信支付失败,相关数据区中缺少参数tencentOpenId:{0}", paymentRecordsT.toString());
            }
            tencentNativePaymentParams.setOpenid(vars.get("tencentOpenId").toString());
        }
        if (TencentTypeEnum.APPLET == tencentType) {
            Map<String, Object> vars = paymentRecordsT.getDataAreaMap();
            if (null == vars.get("appletOpenId")) {
                throw new BaseErrorException("初始化微官网微信支付失败,appletOpenId:{0}", paymentRecordsT.toString());
            }
            tencentNativePaymentParams.setOpenid(vars.get("appletOpenId").toString());
        }
        //获取回调地址
        if (StringUtils.isEmpty(notify_url)) {
            throw new BaseErrorException("从数据字典中获取微信回调地址失败:{0}", paymentRecordsT.toString());
        }
        tencentNativePaymentParams.setNotify_url(notify_url);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        //填写支付时限
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis() + 60 * 1000;
        tencentNativePaymentParams.setTime_start(dateFormat.format(new Date(startTime)));
        tencentNativePaymentParams.setTime_expire(dateFormat.format(new Date(endTime)));
        //获取微信支付签名
        String sign = TencentSignatureUtil.getSign(tencentNativePaymentParams, tencentConfigure);
        tencentNativePaymentParams.setSign(sign);
        //http请求微信扫描二维码支付API,返回xml
        String responseStr = HttpClientUtils.parseResponse(HttpClientUtils.post(TencentConfigure.NATIVE_PAY, tencentNativePaymentParams));
        //将微信返回的数据转换成map
        Map<String, Object> responseMap = XMLParserUtil.getMapFromXML(responseStr);
        logger.info("请求微信预支付返回数据:{},请求参数详情:{}", responseStr, tencentNativePaymentParams.toString());
        if (!TencentPaymentConstants.COMMUNICATE_SUCCESS.equals(responseMap.get(TencentPaymentConstants.RETURN_CODE))) {
            throw new BaseErrorException(
                    "请求微信预支付失败，CODE:{0},MSG:{1}",
                    responseMap.get(TencentPaymentConstants.RETURN_CODE).toString(),
                    responseMap.get(TencentPaymentConstants.RESULT_MSG).toString());
        }
        //根据返回信息判断预支付结果
        if (!TencentPaymentConstants.TRANSACTION_SUCCESS.equals(responseMap.get(TencentPaymentConstants.RESULT_CODE))) {
            //若交易失败则获取微信异常信息并抛出支付异常
            throw new BaseErrorException(
                    "请求微信预支付失败，CODE:{0},MSG:{1}",
                    responseMap.get(TencentPaymentConstants.ERR_CODE).toString(),
                    responseMap.get(TencentPaymentConstants.ERR_CODE_DES).toString());
        }
        //反向验证微信签名
        if (!TencentSignatureUtil.checkIsSignValidFromResponseString(responseMap, tencentConfigure)) {
            throw new BaseErrorException("反向验证微信签名失败:{0}", tencentNativePaymentParams.toString());
        }
        responseMap.put("sign", sign);
        return responseMap;
    }

    /**
     * 查询支付结果
     *
     * @param paymentRecordsT 查询参数对象
     * @return PaymentStateEnum  支付结果
     */
    public PaymentStateEnum queryPaymentResult(PaymentRecordsT paymentRecordsT) {
        TencentQueryParams tencentQueryParams = new TencentQueryParams();
        TencentConfigure tencentConfigure = TencentPayCommonUtil.initTencentPayConfigs(paymentRecordsT);
        tencentQueryParams.setAppid(tencentConfigure.getAppID());
        tencentQueryParams.setMch_id(tencentConfigure.getMchID());
        tencentQueryParams.setSub_mch_id(tencentConfigure.getSubMchID());
        tencentQueryParams.setNonce_str(CommonUtil.getRandomStr());
        tencentQueryParams.setOut_trade_no(paymentRecordsT.getPaymentId());
        //获取微信支付签名
        String sign = TencentSignatureUtil.getSign(tencentQueryParams, tencentConfigure);
        tencentQueryParams.setSign(sign);
        String responseStr = HttpClientUtils.parseResponse(HttpClientUtils.post(TencentConfigure.PAY_QUERY_API, tencentQueryParams));
        //将微信返回的数据转换成map
        Map<String, Object> responseMap = XMLParserUtil.getMapFromXML(responseStr);
        logger.info("微信支付结果查询，paymentId：{}，resultMap:{}",paymentRecordsT.getPaymentId(),responseMap.toString());
        if (!TencentPaymentConstants.COMMUNICATE_SUCCESS.equals(responseMap.get(TencentPaymentConstants.RETURN_CODE))) {
            throw new BaseErrorException(
                    "请求微信查询支付失败，CODE:{0},MSG:{1}",
                    responseMap.get(TencentPaymentConstants.RETURN_CODE).toString(),
                    responseMap.get(TencentPaymentConstants.RESULT_MSG).toString());
        }
        //判断业务结果
        if (!TencentPaymentConstants.RESULT_CODE_SUCCESS.equals(responseMap.get(TencentPaymentConstants.RESULT_CODE).toString())
                &&TencentPaymentConstants.ORDER_NOT_EXIST.equals(responseMap.get(TencentPaymentConstants.ERR_CODE))) {
            return PaymentStateEnum.NOTPAY;
        }
        //判断业务结果
        if (!TencentPaymentConstants.RESULT_CODE_SUCCESS.equals(responseMap.get(TencentPaymentConstants.RESULT_CODE).toString())) {
            throw new BaseErrorException(
                    "请求微信查询支付失败，CODE:{0},MSG:{1}",
                    responseMap.get(TencentPaymentConstants.RESULT_CODE).toString(),
                    responseMap.get(TencentPaymentConstants.RESULT_MSG).toString());
        }
        //判断支付记录是否已经超时关闭
        if (paymentRecordsService.checkPaymentClose(paymentRecordsT)) {
            this.closePayment(paymentRecordsT);
        }
        return PaymerntStateConvertUtil.convertTencentPaymentState(responseMap.get(TencentPaymentConstants.TRADE_STATE).toString());
    }

    /**
     * 取消微信支付订单
     *
     * @param paymentRecordsT 支付记录
     * @return PaymentCloseEnum 关闭支付状态枚举
     */
    public PaymentCloseEnum closePayment(PaymentRecordsT paymentRecordsT) {
        //初始化微信支付参数
        TencentConfigure tencentConfigure = TencentPayCommonUtil.initTencentPayConfigs(paymentRecordsT);
        //初始化取消接口参数
        TencentCancelParams tencentCancelParams = new TencentCancelParams();
        tencentCancelParams.setAppid(tencentConfigure.getAppID());
        tencentCancelParams.setMch_id(tencentConfigure.getMchID());
        tencentCancelParams.setSub_mch_id(tencentConfigure.getSubMchID());
        tencentCancelParams.setNonce_str(CommonUtil.getRandomStr());
        tencentCancelParams.setOut_trade_no(paymentRecordsT.getPaymentId());
        //获取微信支付签名
        String sign = TencentSignatureUtil.getSign(tencentCancelParams, tencentConfigure);
        tencentCancelParams.setSign(sign);
        String responseStr = HttpClientUtils.parseResponse(
                HttpClientUtils.post(TencentConfigure.CLOSE_ORDER, tencentCancelParams));
        //将微信返回的数据转换成map
        Map<String, Object> responseMap = XMLParserUtil.getMapFromXML(responseStr);
        logger.info("请求微信取消支付返回数据:{},请求参数详情:{}", responseStr, tencentCancelParams.toString());
        //根据返回信息判断请求取消接口结果
        //若请求未成功
        if (!TencentPaymentConstants.COMMUNICATE_SUCCESS.equals(responseMap.get(TencentPaymentConstants.RETURN_CODE))) {
            if ("ORDERCLOSED".equals(responseMap.get(TencentPaymentConstants.RETURN_MSG).toString())) {
                return PaymentCloseEnum.SUCCESS;
            }
            throw new BaseErrorException(
                    "请求取消微信支付失败，CODE:{0},MSG:{1}",
                    responseMap.get(TencentPaymentConstants.RETURN_CODE).toString(),
                    responseMap.get(TencentPaymentConstants.RETURN_MSG).toString(),
                    paymentRecordsT.toString());
        }
        //若请求成功，结果未成功
        if (!TencentPaymentConstants.COMMUNICATE_SUCCESS.equals(responseMap.get(TencentPaymentConstants.RESULT_CODE))) {
            return PaymentCloseEnum.FAIL;
        }
        //反向验证微信签名
        if (!TencentSignatureUtil.checkIsSignValidFromResponseString(responseMap, tencentConfigure)) {
            throw new BaseErrorException("反向验证微信签名失败:{0}", tencentCancelParams.toString());
        }
        try {
            paymentRecordsService.closePayment(paymentRecordsT);
        } catch (PaymentClosedException e) {
            return PaymentCloseEnum.SUCCESS;
        } catch (PaymentRepeatException e) {
            throw new BaseErrorException("微信端支付取消成功，但支付记录显示已支付,paymentId:{0}", paymentRecordsT.getPaymentId());
        }
        return PaymentCloseEnum.SUCCESS;
    }

    /**
     * @param refundRequest   退款金额
     * @param paymentRecordsT 退款的支付记录
     * @return 退款结果枚举
     */
    public PaymentRefundEnum refundPayment(RefundRequest refundRequest, PaymentRecordsT paymentRecordsT) {
        //初始化微信支付参数
        TencentConfigure tencentConfigure = TencentPayCommonUtil.initTencentPayConfigs(paymentRecordsT);
        //拼装微信退款请求
        TencentRefundParams tencentRefundParams = new TencentRefundParams();
        //初始化取消接口参数
        tencentRefundParams.setAppid(tencentConfigure.getAppID());
        tencentRefundParams.setMch_id(tencentConfigure.getMchID());
        tencentRefundParams.setSub_mch_id(tencentConfigure.getSubMchID());
        tencentRefundParams.setNonce_str(CommonUtil.getRandomStr());
        tencentRefundParams.setOut_trade_no(paymentRecordsT.getPaymentId());
        tencentRefundParams.setOut_refund_no(refundRequest.getOutRequestNo());
        tencentRefundParams.setTotal_fee(paymentRecordsT.getTotleFee().multiply(new BigDecimal(100)).intValue());
        tencentRefundParams.setRefund_fee(refundRequest.getRefundAmount().multiply(new BigDecimal(100)).intValue());
        tencentRefundParams.setOp_user_id(refundRequest.getOperatorId());
        tencentRefundParams.setRefund_account("REFUND_SOURCE_RECHARGE_FUNDS");
        //获取微信支付签名
        String sign = TencentSignatureUtil.getSign(tencentRefundParams, tencentConfigure);
        tencentRefundParams.setSign(sign);
        tencentRefundParams.setOp_user_id(refundRequest.getOperatorId());
        String responseStr = HttpClientUtils.parseResponse(HttpClientUtils.post(TencentConfigure.REFUND_API,
                tencentConfigure.getCertLocalPath(), tencentConfigure.getMchID(), tencentRefundParams));
        //将微信返回的数据转换成map
        Map<String, Object> responseMap = XMLParserUtil.getMapFromXML(responseStr);
        logger.info("请求微信取消支付返回数据:{},请求参数详情:{}", responseStr, tencentRefundParams.toString());
        //根据返回信息判断请求取消接口结果
        if (!TencentPaymentConstants.COMMUNICATE_SUCCESS.equals(responseMap.get(TencentPaymentConstants.RETURN_CODE))) {
            throw new BaseErrorException(
                    "请求退款微信支付失败，CODE:{0},MSG:{1}",
                    responseMap.get(TencentPaymentConstants.RETURN_CODE).toString(),
                    responseMap.get(TencentPaymentConstants.RETURN_MSG).toString(),
                    paymentRecordsT.toString());
        }
        if (null != responseMap.get(TencentPaymentConstants.RESULT_CODE)) {
            if (!TencentPaymentConstants.RESULT_CODE_SUCCESS.equals(responseMap.get(TencentPaymentConstants
                    .RESULT_CODE).toString())) {
                throw new BaseErrorException(
                        "请求退款微信支付失败，CODE:{0},MSG:{1}",
                        responseMap.get(TencentPaymentConstants.ERR_CODE).toString(),
                        responseMap.get(TencentPaymentConstants.ERR_CODE_DES).toString(),
                        paymentRecordsT.toString());
            }
        }
        //反向验证微信签名
        if (!TencentSignatureUtil.checkIsSignValidFromResponseString(responseMap, tencentConfigure)) {
            throw new BaseErrorException("反向验证微信签名失败:{0}", tencentRefundParams.toString());
        }
        return PaymentRefundEnum.SUCCESS;
    }
}
