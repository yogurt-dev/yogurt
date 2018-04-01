package com.github.jyoghurt.common.payment.business.tencent.support;


import com.github.jyoghurt.common.payment.business.tencent.common.config.TencentConfigure;
import com.github.jyoghurt.common.payment.business.tencent.common.constants.TencentPaymentConstants;
import com.github.jyoghurt.common.payment.business.tencent.common.support.TencentCommonSupport;
import com.github.jyoghurt.common.payment.business.tencent.common.utils.TencentPayCommonUtil;
import com.github.jyoghurt.common.payment.business.tencent.common.utils.TencentSignatureUtil;
import com.github.jyoghurt.common.payment.business.tencent.enums.TencentTypeEnum;
import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.enums.PaymentCloseEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentRefundEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentStateEnum;
import com.github.jyoghurt.common.payment.common.exception.PaymentPreviousErrorException;
import com.github.jyoghurt.common.payment.common.module.RefundRequest;
import com.github.jyoghurt.common.payment.common.service.PaymentRecordsService;
import com.github.jyoghurt.common.payment.common.support.BasePaymentSupport;
import com.github.jyoghurt.common.payment.common.utils.CommonUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;


@Component
public class TencentJsSupport extends BasePaymentSupport {
    @Autowired
    private PaymentRecordsService paymentRecordsService;
    @Autowired
    private TencentCommonSupport tencentCommonSupport;

    /**
     * @param paymentRecordsT 预支付需要的参数对象
     * @return PaymentResult<JSONObject>
     */
    @Override
    public Object createPreviousOrder(PaymentRecordsT paymentRecordsT) throws PaymentPreviousErrorException {
        Map<String, Object> vars = paymentRecordsT.getDataAreaMap();
        paymentRecordsT.setPaymentMethod(PaymentGatewayEnum.TENCENT_JSAPI);
        TencentConfigure tencentConfigure = TencentPayCommonUtil.initTencentPayConfigs(paymentRecordsT);
        Map<String, Object> responseMap = tencentCommonSupport.tencentPayment(TencentTypeEnum.JSAPI, paymentRecordsT);
        JSONObject jsApi = new JSONObject();
        jsApi.put("appId", vars.get("tencentAppId"));
        String timeStamp = String.valueOf(new Date().getTime());
        jsApi.put("timeStamp", timeStamp.substring(0, timeStamp.length() - 3));
        jsApi.put("nonceStr", CommonUtil.getRandomStr());
        jsApi.put("package", "prepay_id=" + responseMap.get(TencentPaymentConstants.PREPAY_ID).toString());
        jsApi.put("signType", "MD5");
        jsApi.put("paySign", TencentSignatureUtil.getSign(jsApi, tencentConfigure));
        //支付记录保存预支付Id及签名
        paymentRecordsService.previousPayment(responseMap.get(TencentPaymentConstants.PREPAY_ID).toString(),
                PaymentGatewayEnum.TENCENT_JSAPI, paymentRecordsT.getPaymentId());
        return jsApi;
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

    /**
     * @param paymentRecordsT 取消支付对象
     * @return PaymentResult<PaymentCloseEnum>
     */
    @Override
    public PaymentCloseEnum closePayment(PaymentRecordsT paymentRecordsT) {
        return tencentCommonSupport.closePayment(paymentRecordsT);
    }

    /**
     * @param refundRequest   退款请求
     * @param paymentRecordsT 支付记录* @return 退款结果枚举
     * @return 退款状态枚举
     */
    @Override
    public PaymentRefundEnum refundPayment(RefundRequest refundRequest, PaymentRecordsT paymentRecordsT) {
        return tencentCommonSupport.refundPayment(refundRequest, paymentRecordsT);
    }

}
