package com.github.jyoghurt.common.payment.common.service;

import com.github.jyoghurt.common.payment.business.card.service.CardPayService;
import com.github.jyoghurt.common.payment.business.cash.service.CashPayService;
import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.enums.PaymentCloseEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentStateEnum;
import com.github.jyoghurt.common.payment.common.exception.*;
import com.github.jyoghurt.common.payment.common.factory.PaymentExtensionFactory;
import com.github.jyoghurt.common.payment.common.factory.PaymentListenerFactory;
import com.github.jyoghurt.common.payment.common.listener.PaymentListener;
import com.github.jyoghurt.common.payment.common.module.PaymentRecordResult;
import com.github.jyoghurt.common.payment.common.module.PaymentRequest;
import com.github.jyoghurt.common.payment.common.module.RefundRequest;
import com.github.jyoghurt.core.exception.BaseErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * user:dell
 * date: 2016/5/17.
 */
@Service
public class PaymentManagerService {
    private static Logger logger = LoggerFactory.getLogger(PaymentManagerService.class);

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentValidateService paymentValidateService;
    @Autowired
    private CardPayService cardPayService;
    @Autowired
    private CashPayService cashPayService;
    @Autowired
    private PaymentRecordsService paymentRecordsService;

    /**
     * 创建支付记录
     *
     * @param paymentRequest 支付请求
     * @return 预支付页所需信息
     */
    public PaymentRecordResult prePaymentRecords(PaymentRequest paymentRequest) {
        return paymentRecordsService.prePaymentRecords(paymentRequest);
    }

    /**
     * 创建支付记录并支付
     *
     * @param paymentId          支付记录Id
     * @param paymentGatewayEnum 支付网关
     * @return 支付记录结果
     * @throws PaymentRefundedException
     * @throws PaymentPreRepeatException
     * @throws PaymentRepeatException
     * @throws PaymentClosedException
     */
    public PaymentRecordResult createPaymentRecordAndPay(String paymentId, PaymentGatewayEnum paymentGatewayEnum) throws PaymentRefundedException, PaymentPreRepeatException, PaymentRepeatException, PaymentClosedException, PaymentPreviousErrorException {
        PaymentRecordResult paymentRecordResult = paymentRecordsService.reBuildPaymentRecords(paymentId);
        switch (paymentGatewayEnum) {
            case CASH_PAY:
            case CARD_PAY:
                return paymentRecordResult;
            case TENCENT_JSAPI:
            case TENCENT_PAY:
            case TENCENT_APPLET:
            case ALI_PAY:
                paymentRecordResult.setPrePaymentMsg(createPreviousOrder(paymentGatewayEnum, paymentRecordResult.getPaymentId()));
                return paymentRecordResult;
            default:
                throw new BaseErrorException("");
        }
    }

    /**
     * 预支付订单
     *
     * @param paymentGatewayEnum 支付平台
     * @param paymentId          支付记录Id
     * @return 预支付结果
     */
    public Object createPreviousOrder(PaymentGatewayEnum paymentGatewayEnum, String paymentId) throws PaymentRefundedException, PaymentPreRepeatException, PaymentRepeatException, PaymentClosedException, PaymentPreviousErrorException {
        PaymentRecordsT paymentRecordsT = paymentRecordsService.find(paymentId);
        return createPreviousOrder(paymentGatewayEnum, paymentRecordsT);
    }

    /**
     * 预支付订单
     *
     * @param paymentGatewayEnum 支付平台
     * @param paymentRecordsT    支付记录
     * @return 预支付结果
     */
    public Object createPreviousOrder(PaymentGatewayEnum paymentGatewayEnum, PaymentRecordsT paymentRecordsT) throws PaymentRefundedException, PaymentPreRepeatException, PaymentRepeatException, PaymentClosedException, PaymentPreviousErrorException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes())
                .getRequest();
        java.util.Enumeration e = request.getHeaderNames();
        while (e.hasMoreElements()) {
            String headerName = (String) e.nextElement();
            logger.info("预支付微信，获得requestName:{},获得requestValue:{}",
                    headerName, request.getHeader(headerName));
        }

        //若支付方式为微信公众号支付 则需要将session的openId赋予支付记录
        if (PaymentGatewayEnum.TENCENT_JSAPI == paymentGatewayEnum) {
            //从Session中获取OpenId
            String openId = WebUtils.getCookie(request, "openId") == null
                    ? null
                    : WebUtils.getCookie(request, "openId").getValue();
            logger.info("预支付微信，获得openId:{}", openId);//若未获取到openId
            if (StringUtils.isEmpty(openId)) {
                throw new BaseErrorException("微官网支付参数异常,SESSION中缺少openId，paymentId:{0}", paymentRecordsT.getPaymentId
                        ());
            }
            paymentRecordsT.setDataAreaMap("tencentOpenId", openId);
        }
        //构造业务监听器
        PaymentListener paymentListener = PaymentListenerFactory.produce(paymentRecordsT
                .getPaymentBusinessType()
                .getServiceName());
        //根据支付记录获取业务集合
        List<String> businessIds = paymentRecordsService.findBusinessIdsByPaymentId(paymentRecordsT.getPaymentId());
        //调用业务验证方法
        assert paymentListener != null;
        paymentListener.beforePreviousPayment(paymentGatewayEnum, businessIds);
        PaymentStateEnum paymentState = paymentValidateService.verifyAdvancePayment(paymentRecordsT);
        //根据验证结果决定是否进行预支付
        switch (paymentState) {
            case SUCCESS:
                throw new PaymentRepeatException();
            case CLOSED:
                throw new PaymentClosedException();
            case NOTPAY:
                return paymentService.createPreviousOrder(paymentGatewayEnum, paymentRecordsT);
            default:
                throw new BaseErrorException("validateRealPaymentAndSync,检查支付状态异常，枚举无法找到,枚举类型:{0},paymentId:{1}",
                        paymentState.name(), paymentRecordsT.getPaymentId());
        }
    }

    /**
     * 取消支付
     *
     * @param paymentRecordsT 取消支付对象
     * @return PaymentResult<Boolean>  true 为已支付 false为未支付
     */
    private PaymentCloseEnum closePayment(PaymentRecordsT paymentRecordsT) {
        return paymentService.closePayment(paymentRecordsT);
    }


    /**
     * 根据支付记录关闭所有相关支付
     * 支付记录关联业务集合，业务集合对应多个支付记录，均取消
     *
     * @param paymentId 支付记录Id
     */
    public void closePaymentsByPaymentId(String paymentId) {
        List<PaymentRecordsT> paymentRecords = paymentRecordsService.findPaymentRecords(paymentId);
        for (PaymentRecordsT paymentRecordsT : paymentRecords) {
            //若支付记录为其本身则跳过
            if (paymentRecordsT.getPaymentId().equals(paymentId)) {
                continue;
            }
            //根据支付平台类型及时限决定是否进行详细查询
            //微信支付及支付宝支付外的支付记录均无超时边界需要进行查询
            if (paymentRecordsT.getPaymentMethod() != PaymentGatewayEnum.TENCENT_PAY &&
                    paymentRecordsT.getPaymentMethod() != PaymentGatewayEnum.TENCENT_JSAPI &&
                    paymentRecordsT.getPaymentMethod() != PaymentGatewayEnum.TENCENT_APPLET &&
                    paymentRecordsT.getPaymentMethod() != PaymentGatewayEnum.ALI_PAY) {
                continue;
            }
            //只有微信及支付宝支付需要根据时限判断是否查询
            if (!paymentRecordsT.getPaymentState()) {
                //满足时间边界条件且未支付的进行取消操作
                closePayment(paymentRecordsT);
            }
        }
    }

    /**
     * 支付订单
     * 本地支付平台接口 现金支付
     *
     * @param paymentId     支付记录Id
     * @param paymentAmount 实际支付金额
     * @return 支付结果
     * @throws CashPayVerifyException
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     */
    public PaymentStateEnum createCashPayment(String paymentId, BigDecimal paymentAmount) throws CashPayVerifyException, PaymentRepeatException, PaymentRefundedException, PaymentClosedException {
        PaymentRecordsT paymentRecordsT = paymentRecordsService.find(paymentId);
        return createCashPayment(paymentRecordsT, paymentAmount);
    }

    /**
     * 支付订单
     * 本地支付平台接口 现金支付
     *
     * @param paymentRecordsT 支付记录
     * @param paymentAmount   实际支付金额
     * @return 支付结果
     * @throws CashPayVerifyException
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     */
    public PaymentStateEnum createCashPayment(PaymentRecordsT paymentRecordsT, BigDecimal paymentAmount) throws CashPayVerifyException, PaymentRepeatException, PaymentRefundedException, PaymentClosedException {
        if (paymentRecordsT.getDeleteFlag()) {
            throw new PaymentClosedException();
        }
        //根据支付记录获取业务集合
        List<String> businessIds = paymentRecordsService.findBusinessIdsByPaymentId(paymentRecordsT.getPaymentId());
        //构造业务监听器
        PaymentListener paymentListener = PaymentListenerFactory.produce(paymentRecordsT
                .getPaymentBusinessType()
                .getServiceName());
        //调用业务验证方法
        assert paymentListener != null;
        paymentListener.beforePayment(PaymentGatewayEnum.CARD_PAY, businessIds);
        //取消所有跟当前预支付相关的支付工单
        closePaymentsByPaymentId(paymentRecordsT.getPaymentId());
        return cashPayService.cashPay(paymentRecordsT, paymentAmount);
    }

    /**
     * 支付订单
     * 本地支付平台接口
     *
     * @param paymentId 支付记录Id
     * @return 支付结果
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     */
    public PaymentStateEnum cardPayment(String paymentId) throws PaymentClosedException, PaymentRepeatException, PaymentRefundedException {
        PaymentRecordsT paymentRecordsT = paymentRecordsService.find(paymentId);
        return cardPayment(paymentRecordsT);
    }

    /**
     * 支付订单
     * 本地支付平台接口
     *
     * @param paymentRecordsT 支付记录
     * @return 支付结果
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     */
    public PaymentStateEnum cardPayment(PaymentRecordsT paymentRecordsT) throws PaymentClosedException, PaymentRepeatException, PaymentRefundedException {
        if (paymentRecordsT.getDeleteFlag()) {
            throw new PaymentClosedException();
        }
        //根据支付记录获取业务集合
        List<String> businessId = paymentRecordsService.findBusinessIdsByPaymentId(paymentRecordsT.getPaymentId());
        //构造业务监听器
        PaymentListener paymentListener = PaymentListenerFactory.produce(paymentRecordsT
                .getPaymentBusinessType()
                .getServiceName());
        //调用业务验证方法
        assert paymentListener != null;
        paymentListener.beforePayment(PaymentGatewayEnum.CARD_PAY, businessId);
        //取消所有跟当前预支付相关的支付工单
        closePaymentsByPaymentId(paymentRecordsT.getPaymentId());
        return cardPayService.cardPay(paymentRecordsT);
    }

    /**
     * 退款接口
     *
     * @param refundRequest 退款请求对象
     */
    public void submitRefundPayment(RefundRequest refundRequest) throws PaymentRefundErrorException {
        paymentService.refundPayment(refundRequest);
    }

    /**
     * 根据支付业务 执行支付扩展方法
     *
     * @param paymentId 支付记录Id
     * @return 支付扩展对象
     */
    public Object extensionMethods(String paymentId) {
        PaymentRecordsT paymentRecordsT = paymentRecordsService.findSuccessPaymentRecordsByPaymentId(paymentId);
        if (null == paymentRecordsT) {
            logger.info("无支付成功记录", paymentId);
            return null;
        }
        //根据支付记录获取业务集合
        List<String> businessIds = paymentRecordsService.findBusinessIdsByPaymentId(paymentRecordsT.getPaymentId());
        BasePaymentService basePaymentService = PaymentExtensionFactory.produce(paymentRecordsT
                .getPaymentBusinessType()
                .getServiceName());
        //调用支付扩展方法并回调
        return basePaymentService.extensionMethods(businessIds, paymentRecordsT);
    }
}
