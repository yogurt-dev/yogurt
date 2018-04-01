package com.github.jyoghurt.common.payment.business.alipay.support;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.service.AlipayTradeService;
import com.github.jyoghurt.common.payment.business.alipay.common.constants.AliPayConstants;
import com.github.jyoghurt.common.payment.business.alipay.common.direct.util.AlipaySubmit;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.config.AliPayConfigs;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.ExtendParams;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.builder.AlipayTradePrecreateContentBuilder;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.builder.AlipayTradeRefundContentBuilder;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.result.AlipayF2FPrecreateResult;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.result.AlipayF2FQueryResult;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.model.result.AlipayF2FRefundResult;
import com.github.jyoghurt.common.payment.business.alipay.common.trade.service.impl.AlipayTradeServiceImpl;
import com.github.jyoghurt.common.payment.business.alipay.common.utils.AlipayCommonUtil;
import com.github.jyoghurt.common.payment.business.alipay.enums.AliPaymentStateEnum;
import com.github.jyoghurt.common.payment.common.constants.PaymentCommonConstants;
import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.enums.PaymentCloseEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentRefundEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentStateEnum;
import com.github.jyoghurt.common.payment.common.exception.PaymentClosedException;
import com.github.jyoghurt.common.payment.common.exception.PaymentPreviousErrorException;
import com.github.jyoghurt.common.payment.common.exception.PaymentRepeatException;
import com.github.jyoghurt.common.payment.common.module.RefundRequest;
import com.github.jyoghurt.common.payment.common.service.PaymentRecordsService;
import com.github.jyoghurt.common.payment.common.support.BasePaymentSupport;
import com.github.jyoghurt.common.payment.common.utils.PaymerntStateConvertUtil;
import com.github.jyoghurt.common.payment.common.utils.QrCodeUtil;
import com.github.jyoghurt.core.exception.BaseErrorException;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * user: zjl
 * date:2016/4/11.
 */
@Component
public class AliPayPaymentSupport extends BasePaymentSupport {

    private static Logger logger = LoggerFactory.getLogger(AliPayPaymentSupport.class);
    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;
    @Autowired
    private PaymentRecordsService paymentRecordsService;
    @Autowired
    private QrCodeUtil qrCodeUtil;
    @Value("${alipay.config.notify_url}")
    private String notify_url;
    @Value("${alipay.config.return_url}")
    private String return_url;

    /**
     * 支付宝即时到付支付方法
     *
     * @param paymentRecordsT 支付记录
     * @return Object  支付结果
     */
    @Override
    public Object createPreviousOrder(PaymentRecordsT paymentRecordsT) {
        //初始化配置
        AliPayConfigs aliPayConfigs = AlipayCommonUtil.initAliPayConfigs(paymentRecordsT);
        //获取回调地址
        if (StringUtils.isEmpty(notify_url)) {
            throw new BaseErrorException("从数据字典中获取支付宝回调地址失败,{0}", paymentRecordsT.toString());
        }
        //获取跳转地址
        if (StringUtils.isEmpty(return_url)) {
            throw new BaseErrorException("从数据字典中获取支付宝获取跳转地址失败,{0}", paymentRecordsT.toString());

        }
        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<>();
        sParaTemp.put("service", AliPayConfigs.CREATE_DIRECT_PAY_BY_USER);
        sParaTemp.put("partner", aliPayConfigs.getPid());
        sParaTemp.put("_input_charset", AliPayConfigs.input_charset);
        sParaTemp.put("seller_id", aliPayConfigs.getPid());
        sParaTemp.put("sign_type", AliPayConfigs.sign_type);
        sParaTemp.put("payment_type", AliPayConfigs.payment_type);
        sParaTemp.put("notify_url", notify_url);
        sParaTemp.put("return_url", return_url);
        sParaTemp.put("out_trade_no", paymentRecordsT.getPaymentId());
        sParaTemp.put("subject", paymentRecordsT.getPaymentDetil());
        sParaTemp.put("total_fee", paymentRecordsT.getTotleFee().toString());
        sParaTemp.put("it_b_pay", PaymentCommonConstants.PAYMENT_TIME_LIMIT + "m");
        paymentRecordsT.setPaymentMethod(PaymentGatewayEnum.ALI_PAY);
        paymentRecordsService.updateForSelective(paymentRecordsT);
        return AlipaySubmit.buildRequest(sParaTemp, "get", "确认", aliPayConfigs);
    }

    /**
     * 支付宝 当面付
     *
     * @param paymentRecordsT 支付记录
     * @return 预支付二维码图片链接
     */
    public Object createFaceToFace(PaymentRecordsT paymentRecordsT) throws PaymentPreviousErrorException {
        //初始化配置
        AliPayConfigs aliPayConfigs = AlipayCommonUtil.initAliPayConfigs(paymentRecordsT);
        //初始化支付接口
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build(aliPayConfigs);
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = paymentRecordsT.getPaymentId();

        // (必填) 订单标题，粗略描述用户的支付目的。如“喜士多（浦东店）消费”
        String subject = paymentRecordsT.getPaymentDetil();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = paymentRecordsT.getTotleFee().toEngineeringString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(aliPayConfigs.getPid());
        extendParams.setServiceName(paymentRecordsT.getPaymentBusinessType().getServiceName());
        // 支付超时，定义为1分钟
        String timeExpress = PaymentCommonConstants.PAYMENT_TIME_LIMIT + "m";
        //获取回调地址
        if (StringUtils.isEmpty(notify_url)) {
            throw new BaseErrorException("从数据字典中获取支付宝回调地址失败,{0}", paymentRecordsT.toString());
        }
        //获取跳转地址
        if (StringUtils.isEmpty(return_url)) {
            throw new BaseErrorException("从数据字典中获取支付宝获取跳转地址失败,{0}", paymentRecordsT.toString());
        }
        AlipayTradePrecreateContentBuilder builder = new AlipayTradePrecreateContentBuilder()
                .setSubject(subject)
                .setTotalAmount(totalAmount)
                .setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount)
                .setSellerId(sellerId)
                .setBody(body)
                .setOperatorId(operatorId)
                .setStoreId(storeId)
                .setExtendParams(extendParams)
                .setTimeExpress(timeExpress)
                .setNotifyUrl(notify_url);
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                AlipayTradePrecreateResponse response = result.getResponse();
                //输出支付宝回调参数
                AlipayCommonUtil.logResponse(response);
                paymentRecordsT.setPaymentMethod(PaymentGatewayEnum.ALI_PAY);
                paymentRecordsService.previousPayment(PaymentGatewayEnum.ALI_PAY, paymentRecordsT.getPaymentId());
                return qrCodeUtil.createQrCode(response.getQrCode(), paymentRecordsT.getPaymentId());
            case FAILED:
                throw new BaseErrorException("申请支付宝面对面支付异常,异常返回码:FAILED,paymentId:{0}", paymentRecordsT.getPaymentId());
            case UNKNOWN:
                throw new BaseErrorException("申请支付宝面对面支付异常,异常返回码:UNKNOWN,paymentId:{0}", paymentRecordsT.getPaymentId());
            default:
                throw new BaseErrorException("申请支付宝面对面支付异常,异常返回码:" + result.getTradeStatus() + ",paymentId:{}", paymentRecordsT.getPaymentId());
        }
    }

    /**
     * 查询支付结果
     *
     * @param paymentRecordsT 查询参数对象
     * @return PaymentStateEnum  支付结果
     */
    @Override
    public PaymentStateEnum queryPaymentResult(PaymentRecordsT paymentRecordsT) {
        //初始化配置
        AliPayConfigs aliPayConfigs = AlipayCommonUtil.initAliPayConfigs(paymentRecordsT);
        //初始化查询接口
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build(aliPayConfigs);
        // (必填) 商户订单号，通过此商户订单号查询当面付的交易状态
        AlipayF2FQueryResult result = tradeService.queryTradeResult(paymentRecordsT.getPaymentId());
        logger.info("支付宝查询，paymentId:{},resultCode：{}，", paymentRecordsT.getPaymentId(), result.getResponse().getCode());
        //若支付宝未进行扫码 则即时过了两分钟也不超时  此时进行手动超时控制
        if (AliPayConstants.CANT_FIND_TRADE.equals(result.getResponse().getCode())) {
            if (paymentRecordsService.checkPaymentClose(paymentRecordsT)) {
                this.closePayment(paymentRecordsT);
                return PaymerntStateConvertUtil.convertAlipayState(AliPaymentStateEnum.TRADE_CLOSED.name());
            }
            return PaymerntStateConvertUtil.convertAlipayState(AliPaymentStateEnum.WAIT_BUYER_PAY.name());
        }
        //若同步支付宝状态为已取消 则本地也取消
        if (AliPaymentStateEnum.TRADE_CLOSED.name().equals(result.getResponse().getTradeStatus())) {
            try {
                this.paymentRecordsService.closePayment(paymentRecordsT);
            } catch (PaymentClosedException e) {
                return PaymentStateEnum.CLOSED;
            } catch (PaymentRepeatException e) {
                return PaymentStateEnum.SUCCESS;
            }
        }
        return PaymerntStateConvertUtil.convertAlipayState(result.getResponse().getTradeStatus());
    }


    /**
     * 关闭支付
     *
     * @param paymentRecordsT 取消支付对象
     * @return 取消支付结果
     */
    @Override
    public PaymentCloseEnum closePayment(PaymentRecordsT paymentRecordsT) {
        //查询参数
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        JSONObject obj = new JSONObject();
        obj.put("out_trade_no", paymentRecordsT.getPaymentId());
        request.setBizContent(com.alibaba.fastjson.JSON.toJSONString(obj));
        //请求支付宝关闭订单接口
        AlipayTradeCancelResponse response;
        try {
            response = initAliPayClient(paymentRecordsT).execute(request);
        } catch (AlipayApiException e) {
            throw new BaseErrorException("申请支付宝关闭支付异常,paymentId:{0}", e, paymentRecordsT.getPaymentId());
        }
        //验证支付宝关闭支付结果
        if (AliPayConstants.SUCCESS_CODE.equals(response.getCode()) && response.getOutTradeNo().equals(paymentRecordsT.getPaymentId())) {
            //关闭成功  更新支付记录
            try {
                paymentRecordsService.closePayment(paymentRecordsT);
            } catch (PaymentClosedException e) {
                return PaymentCloseEnum.SUCCESS;
            } catch (PaymentRepeatException e) {
                throw new BaseErrorException("支付宝支付取消成功，但支付记录显示已支付,paymentId:{0}", paymentRecordsT.getPaymentId());
            }
            return PaymentCloseEnum.SUCCESS;
        }
        return PaymentCloseEnum.FAIL;
    }

    @Override
    public PaymentRefundEnum refundPayment(RefundRequest refundRequest, PaymentRecordsT paymentRecordsT) {
        //初始化配置
        AliPayConfigs aliPayConfigs = AlipayCommonUtil.initAliPayConfigs(paymentRecordsT);
        //初始化支付接口
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build(aliPayConfigs);
        AlipayTradeRefundContentBuilder builder = new AlipayTradeRefundContentBuilder()
                .setOutTradeNo(paymentRecordsT.getPaymentId())
                .setRefundAmount(refundRequest.getRefundAmount().toEngineeringString())
                .setOutRequestNo(refundRequest.getOutRequestNo())
                .setRefundReason(StringUtils.isEmpty(refundRequest.getRefundPaymentReason())
                        ? "用户退款"
                        : refundRequest.getRefundPaymentReason());
        AlipayF2FRefundResult alipayF2FRefundResult = tradeService.tradeRefund(builder);
        if (alipayF2FRefundResult.isTradeSuccess()) {
            return PaymentRefundEnum.SUCCESS;
        }
        return PaymentRefundEnum.FAIL;
    }

    /**
     * 初始化支付宝客户端
     *
     * @param paymentRecordsT 支付记录
     * @return 支付宝客户端
     */
    private AlipayClient initAliPayClient(PaymentRecordsT paymentRecordsT) {
        // (必填) 商户订单号，通过此商户订单号查询当面付的交易状态
        AliPayConfigs aliPayConfigs = AlipayCommonUtil.initAliPayConfigs(paymentRecordsT);
        //初始化支付宝客户端
        return new DefaultAlipayClient(
                "https://openapi.alipay.com/gateway.do",
                aliPayConfigs.getAppid(),
                aliPayConfigs.getPrivateKey(),
                "json",
                "utf-8",
                aliPayConfigs.getAlipayPublicKey());
    }


}