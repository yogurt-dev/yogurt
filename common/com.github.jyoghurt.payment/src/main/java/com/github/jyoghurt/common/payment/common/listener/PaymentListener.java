package com.github.jyoghurt.common.payment.common.listener;

import com.github.jyoghurt.common.payment.common.module.BaseCallBackParam;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;

import java.util.List;

/**
 * 支付监听
 */
public interface PaymentListener {
    /**
     * 预支付之前事件
     * 业务实现 可包含验证及预支付之前保存业务数据
     *
     * @param paymentGatewayEnum 支付平台枚举
     * @param businessIds        支付记录集合
     */
    void beforePreviousPayment(PaymentGatewayEnum paymentGatewayEnum, List<String> businessIds);

    /**
     * 支付前事件
     * 业务实现 可包含验证及支付之前保存业务数据
     *
     * @param paymentGatewayEnum 支付平台枚举
     * @param businessIds        支付记录集合
     */
    void beforePayment(PaymentGatewayEnum paymentGatewayEnum, List<String> businessIds);

    /**
     * 支付调用成功监听方法
     * 需各模块实现具体业务
     *
     * @param baseCallBackParam 支付返回对象
     * @param businessIds       支付记录集合
     */
    void afterPayment(BaseCallBackParam baseCallBackParam, List<String> businessIds);
}
