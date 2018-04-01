package com.github.jyoghurt.common.payment.business.tencent.support;


import com.github.jyoghurt.common.payment.business.tencent.common.constants.TencentPaymentConstants;
import com.github.jyoghurt.common.payment.business.tencent.common.support.TencentCommonSupport;
import com.github.jyoghurt.common.payment.business.tencent.enums.TencentTypeEnum;
import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.enums.PaymentCloseEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.common.payment.common.exception.PaymentPreviousErrorException;
import com.github.jyoghurt.common.payment.common.module.PaymentResult;
import com.github.jyoghurt.common.payment.common.service.PaymentRecordsService;
import com.github.jyoghurt.common.payment.common.support.BasePaymentSupport;
import com.github.jyoghurt.common.payment.common.utils.QrCodeUtil;
import com.github.jyoghurt.common.payment.common.constants.PaymentCommonConstants;
import com.github.jyoghurt.common.payment.common.enums.PaymentRefundEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentStateEnum;
import com.github.jyoghurt.common.payment.common.module.RefundRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class TencentPaymentSupport extends BasePaymentSupport {
    @Autowired
    private PaymentRecordsService paymentRecordsService;
    @Autowired
    private TencentCommonSupport tencentCommonSupport;
    @Autowired
    private QrCodeUtil qrCodeUtil;

    @Override
    public Object createPreviousOrder(PaymentRecordsT paymentRecordsT) throws PaymentPreviousErrorException {
        PaymentResult<String> paymentResult = new PaymentResult<>();
        paymentRecordsT.setPaymentMethod(PaymentGatewayEnum.TENCENT_PAY);
        Map<String, Object> responseMap = tencentCommonSupport.tencentPayment(TencentTypeEnum.NATIVE, paymentRecordsT);
        //支付记录保存预支付Id及签名
        paymentResult.setErrorCode(PaymentCommonConstants.PRE_PAYMENT_SUCCESS_CODE);
        paymentRecordsService.previousPayment(responseMap.get(TencentPaymentConstants.PREPAY_ID).toString()
                , PaymentGatewayEnum.TENCENT_PAY, paymentRecordsT.getPaymentId());
        return qrCodeUtil.createQrCode(responseMap.get(TencentPaymentConstants.CODE_URL).toString(), paymentRecordsT.getPaymentId());
    }

    /**
     * 查询支付结果
     *
     * @param paymentRecordsT 查询参数对象
     * @return PaymentResult<String>  支付结果
     */
    @Override
    public PaymentStateEnum queryPaymentResult(PaymentRecordsT paymentRecordsT) {
        return tencentCommonSupport.queryPaymentResult(paymentRecordsT);
    }

    @Override
    public PaymentCloseEnum closePayment(PaymentRecordsT paymentRecordsT) {
        return tencentCommonSupport.closePayment(paymentRecordsT);
    }

    @Override
    public PaymentRefundEnum refundPayment(RefundRequest refundRequest, PaymentRecordsT paymentRecordsT) {
        return tencentCommonSupport.refundPayment(refundRequest, paymentRecordsT);
    }
}
