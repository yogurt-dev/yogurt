package com.github.jyoghurt.common.payment.common.service.impl;


import com.github.jyoghurt.common.payment.business.alipay.support.AliPayPaymentSupport;
import com.github.jyoghurt.common.payment.business.card.support.CardPaySupport;
import com.github.jyoghurt.common.payment.business.cash.support.CashPaySupport;
import com.github.jyoghurt.common.payment.business.tencent.support.TencentAppletSupport;
import com.github.jyoghurt.common.payment.business.tencent.support.TencentJsSupport;
import com.github.jyoghurt.common.payment.business.tencent.support.TencentPaymentSupport;
import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.domain.PaymentRefundT;
import com.github.jyoghurt.common.payment.common.enums.PaymentCloseEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentRefundEnum;
import com.github.jyoghurt.common.payment.common.exception.PaymentPreRepeatException;
import com.github.jyoghurt.common.payment.common.exception.PaymentPreviousErrorException;
import com.github.jyoghurt.common.payment.common.exception.PaymentRefundErrorException;
import com.github.jyoghurt.common.payment.common.service.PaymentRecordsService;
import com.github.jyoghurt.common.payment.common.service.PaymentRefundService;
import com.github.jyoghurt.common.payment.common.service.PaymentService;
import com.github.jyoghurt.common.payment.common.enums.PaymentStateEnum;
import com.github.jyoghurt.common.payment.common.module.RefundRequest;
import com.github.jyoghurt.core.exception.BaseErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("paymentService")
public class PaymentServiceImpl implements PaymentService {
    //注入支付接口（支付宝，微信等）
    @Autowired
    private TencentPaymentSupport tencentPaymentSupport;//微信支付接口
    @Autowired
    private TencentJsSupport tencentJsSupport;//微信公众平台支付接口
    @Autowired
    private TencentAppletSupport tencentAppletSupport;//微信小程序支付接口
    @Autowired
    private AliPayPaymentSupport aliPayPaymentSupport;//支付宝支付接口
    @Autowired
    private CashPaySupport cashPaySupport;//现金支付接口
    @Autowired
    private CardPaySupport cardPaySupport;//刷卡支付接口
    @Autowired
    private PaymentRecordsService paymentRecordsService;
    @Autowired
    private PaymentRefundService paymentRefundService;

    @Override
    public Object createPreviousOrder(PaymentGatewayEnum paymentGatewayEnum, PaymentRecordsT paymentRecordsT) throws PaymentPreRepeatException, PaymentPreviousErrorException {
        //选择应该调用的支付接口 引入已写好的各自接口
        //若已做过预支付的支付记录 不可重复进行预支付
        if (null != paymentRecordsT.getPaymentMethod()) {
            throw new PaymentPreRepeatException();
        }
        switch (paymentGatewayEnum) {
            case TENCENT_PAY: {//微信支付
                return tencentPaymentSupport.createPreviousOrder(paymentRecordsT);
            }
            case TENCENT_JSAPI: {//微信支付 微官网
                return tencentJsSupport.createPreviousOrder(paymentRecordsT);
            }
            case TENCENT_APPLET: {//微信支付 微官网
                return tencentAppletSupport.createPreviousOrder(paymentRecordsT);
            }
            case ALI_PAY: {//支付宝
                return aliPayPaymentSupport.createFaceToFace(paymentRecordsT);
            }
            case CASH_PAY: {//现金支付
                return cashPaySupport.createPreviousOrder(paymentRecordsT);
            }
            case CARD_PAY: {//刷卡支付
                return cardPaySupport.createPreviousOrder(paymentRecordsT);
            }
            default:
                throw new BaseErrorException("未知的支付网关,详细信息:{0},支付网关:{1}",
                        paymentRecordsT.toString(),
                        paymentGatewayEnum.name());
        }
    }


    /**
     * 查询支付结果
     *
     * @param paymentRecordsT 查询参数对象
     * @return PaymentResult<Boolean>  true 为已支付 false为未支付
     */
    @Override
    public PaymentStateEnum queryPaymentResult(PaymentGatewayEnum paymentGatewayEnum, PaymentRecordsT paymentRecordsT) {
        switch (paymentGatewayEnum) {
            case TENCENT_PAY: {
                return tencentPaymentSupport.queryPaymentResult(paymentRecordsT);
            }
            case TENCENT_JSAPI: {
                return tencentJsSupport.queryPaymentResult(paymentRecordsT);
            }
            case TENCENT_APPLET: {
                return tencentAppletSupport.queryPaymentResult(paymentRecordsT);
            }
            case ALI_PAY: {
                return aliPayPaymentSupport.queryPaymentResult(paymentRecordsT);
            }
            case CASH_PAY: {
                return cashPaySupport.queryPaymentResult(paymentRecordsT);
            }
            case CARD_PAY: {
                return cardPaySupport.queryPaymentResult(paymentRecordsT);
            }
            case ONLY_LUCK_OR_BALANCE_PAY:
                return paymentRecordsT.getPaymentState() ? PaymentStateEnum.SUCCESS : PaymentStateEnum.NOTPAY;
            default:
                throw new BaseErrorException("未知的支付网关,详细信息:{0},支付网关:{1}",
                        paymentRecordsT.toString(),
                        paymentGatewayEnum.name());
        }
    }

    /**
     * 关闭支付接口
     *
     * @param paymentRecordsT 取消支付对象
     * @return PaymentResult<Boolean>  true 为已支付 false为未支付
     */
    @Override
    public PaymentCloseEnum closePayment(PaymentRecordsT paymentRecordsT) {
        if (null == paymentRecordsT.getPaymentMethod()) {
            return PaymentCloseEnum.SUCCESS;
        }
        switch (paymentRecordsT.getPaymentMethod()) {
            case TENCENT_PAY:
                return tencentPaymentSupport.closePayment(paymentRecordsT);
            case TENCENT_JSAPI:
                return tencentPaymentSupport.closePayment(paymentRecordsT);
            case TENCENT_APPLET:
                return tencentAppletSupport.closePayment(paymentRecordsT);
            case ALI_PAY:
                return aliPayPaymentSupport.closePayment(paymentRecordsT);
            case CASH_PAY:
                return PaymentCloseEnum.FAIL;
            case CARD_PAY:
                return PaymentCloseEnum.FAIL;
            default:
                throw new BaseErrorException("未知的支付网关,详细信息:{0},支付网关:{1}",
                        paymentRecordsT.toString(),
                        paymentRecordsT.getPaymentMethod().name());
        }
    }

    /**
     * @param refundRequest 退款请求对象
     */
    @Override
    public void refundPayment(RefundRequest refundRequest) throws PaymentRefundErrorException {
        if (StringUtils.isEmpty(refundRequest.getOutRequestNo())) {
            throw new BaseErrorException("退款时未传退款单号");
        }
        if (StringUtils.isEmpty(refundRequest.getBusinessId())) {
            throw new BaseErrorException("退款时未传业务Id");
        }
        if (null == refundRequest.getPaymentBusinessType()) {
            throw new BaseErrorException("退款时未传退款业务类型");
        }
        //查找支付成功的支付记录
        List<PaymentRecordsT> paymentRecordsTs = paymentRecordsService.findSuccessPaymentRecordsByBusId(refundRequest.getBusinessId(),
                refundRequest.getPaymentBusinessType());
        if (paymentRecordsTs.size() == 0) {
            throw new BaseErrorException("退款异常，业务并未进行支付却申请退款,详细信息:{0}", refundRequest.toString());
        }
        if (paymentRecordsTs.size() > 1) {
            throw new BaseErrorException("退款异常，业务有1条以上的支付成功记录,详细信息:{0}", refundRequest.toString());
        }
        //获得退款对应的支付记录
        PaymentRecordsT paymentRecordsT = paymentRecordsTs.get(0);
        //校验是否退款单号已经退过款了
        PaymentRefundT paymentRefundTs = paymentRefundService.find(refundRequest.getOutRequestNo());
        if (null != paymentRefundTs && paymentRefundTs.getRefundPaymentState()) {
            throw new BaseErrorException("已经成功退款的退款单重复申请退款，参数信息：{0}", refundRequest.toString());
        }
        PaymentRefundEnum paymentRefundEnum;
        switch (paymentRecordsT.getPaymentMethod()) {
            case TENCENT_PAY:
                paymentRefundEnum = tencentPaymentSupport.refundPayment(refundRequest, paymentRecordsT);
                break;
            case TENCENT_JSAPI:
                paymentRefundEnum = tencentJsSupport.refundPayment(refundRequest, paymentRecordsT);
                break;
            case TENCENT_APPLET:
                paymentRefundEnum = tencentAppletSupport.refundPayment(refundRequest, paymentRecordsT);
                break;
            case ALI_PAY:
                paymentRefundEnum = aliPayPaymentSupport.refundPayment(refundRequest, paymentRecordsT);
                break;
            case CASH_PAY:
                paymentRefundEnum = cashPaySupport.refundPayment(refundRequest, paymentRecordsT);
                break;
            case CARD_PAY:
                paymentRefundEnum = cardPaySupport.refundPayment(refundRequest, paymentRecordsT);
                break;
            default:
                throw new BaseErrorException("未知的退款网关,详细信息:{0},退款网关:{1}",
                        refundRequest.toString(),
                        paymentRecordsT.getPaymentMethod().name());
        }
        if (PaymentRefundEnum.SUCCESS == paymentRefundEnum) {
            paymentRefundService.savePaymentRefund(refundRequest, paymentRecordsT);
            return;
        }
        throw new PaymentRefundErrorException();
    }
}