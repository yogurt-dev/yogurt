package com.github.jyoghurt.common.payment.common.service;

import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.enums.PaymentBusinessTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * user:dell
 * date: 2016/8/12.
 */
@Service
public class PaymentSearchService {
    @Autowired
    private PaymentRecordsService paymentRecordsService;


    public PaymentRecordsT getPaymentRecordById(String paymentId) {
        return paymentRecordsService.find(paymentId);
    }

    /**
     * 根据业务信息查找相关支付记录
     *
     * @param businessId          业务ID
     * @param paymentBusinessType 业务类型
     * @return 支付记录集合
     */
    public List<PaymentRecordsT> findPaymentRecordsByBusId(String businessId, PaymentBusinessTypeEnum paymentBusinessType) {
        return paymentRecordsService.findPaymentRecordsByBusId(businessId, paymentBusinessType);
    }

    /**
     * 根据业务信息查找支付成功的支付记录
     *
     * @param businessId          业务ID
     * @param paymentBusinessType 业务类型
     * @return 支付记录集合
     */
    public PaymentRecordsT findSuccessPaymentRecordsByBusId(String businessId, PaymentBusinessTypeEnum paymentBusinessType) {
        return paymentRecordsService.findSuccessPaymentRecordsByBusId(businessId, paymentBusinessType).get(0);
    }

    /**
     * 根据业务Id集合查找相关支付记录集合
     *
     * @param businessIds         业务ID集合
     * @param paymentBusinessType 业务类型
     * @return 支付记录集合
     */
    public List<PaymentRecordsT> findPaymentRecordsByBusIds(List<String> businessIds, PaymentBusinessTypeEnum
            paymentBusinessType) {
        return paymentRecordsService.findPaymentRecordsByBusIds(businessIds, paymentBusinessType);
    }

    /**
     * 根据支付记录 查找支付关联Id集合
     *
     * @param paymentId 支付Id
     * @return 支付关联Id集合
     */
    public List<String> findBusinessIdsByPaymentId(String paymentId) {
        return paymentRecordsService.findBusinessIdsByPaymentId(paymentId);
    }
}
