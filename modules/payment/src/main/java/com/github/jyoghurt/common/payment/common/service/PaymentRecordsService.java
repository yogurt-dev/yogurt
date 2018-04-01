package com.github.jyoghurt.common.payment.common.service;


import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsBusinessR;
import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.enums.PaymentBusinessTypeEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.common.payment.common.exception.*;
import com.github.jyoghurt.common.payment.common.module.PaymentRecordResult;
import com.github.jyoghurt.common.payment.common.module.PaymentRequest;
import com.github.jyoghurt.core.service.BaseService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 支付管理服务层
 */
public interface PaymentRecordsService extends BaseService<PaymentRecordsT> {
    /**
     * 生成支付记录
     *
     * @param paymentRequest 支付请求
     * @return PaymentRecordResult  待支付信息
     */
    PaymentRecordResult prePaymentRecords(PaymentRequest paymentRequest);

    /**
     * 重新生成支付记录
     *
     * @param paymentId 支付记录Id
     * @return PaymentRecordResult  待支付信息
     */
    PaymentRecordResult reBuildPaymentRecords(String paymentId) throws PaymentClosedException, PaymentRepeatException, PaymentRefundedException;


    /**
     * 根据支付记录查找全部关联支付记录
     *
     * @param paymentRecordsT 支付记录
     * @return 关联支付记录集合
     */
    List<PaymentRecordsT> findPaymentRecords(PaymentRecordsT paymentRecordsT);

    /**
     * 根据支付记录Id查找全部关联支付记录
     *
     * @param paymentId 支付记录Id
     * @return 关联支付记录集合
     */
    List<PaymentRecordsT> findPaymentRecords(String paymentId);


    /**
     * 根据业务信息查找相关支付记录
     *
     * @param businessId          业务ID
     * @param paymentBusinessType 业务类型
     * @return 支付记录集合
     */
    List<PaymentRecordsT> findPaymentRecordsByBusId(String businessId, PaymentBusinessTypeEnum paymentBusinessType);

    /**
     * 根据业务信息查找相关支付成功的支付记录
     *
     * @param businessId          业务ID
     * @param paymentBusinessType 业务类型
     * @return 支付记录集合
     */
    List<PaymentRecordsT> findSuccessPaymentRecordsByBusId(String businessId, PaymentBusinessTypeEnum paymentBusinessType);
    /**
     * 根据业务信息查找相关支付成功的支付记录
     *
     * @param paymentId          支付Id
     * @return 支付记录集合
     */
    PaymentRecordsT findSuccessPaymentRecordsByPaymentId(String paymentId);
    /**
     * 根据业务Id集合查找相关支付记录集合
     *
     * @param businessIds         业务ID集合
     * @param paymentBusinessType 业务类型
     * @return 支付记录集合
     */
    List<PaymentRecordsT> findPaymentRecordsByBusIds(List<String> businessIds, PaymentBusinessTypeEnum paymentBusinessType);

    /**
     * 根据支付Id查找业务集合
     *
     * @param paymentId 支付Id
     * @return 业务订单集合
     */
    List<PaymentRecordsBusinessR> findBusinessListByPaymentId(String paymentId);

    /**
     * 根据支付Id查找业务Id集合
     *
     * @param paymentId 支付Id
     * @return 业务订单Id集合
     */
    List<String> findBusinessIdsByPaymentId(String paymentId);

    /**
     * 业务与订单多对多关系维护
     * 本方法为业务多，支付记录唯、为一
     *
     * @param businessIds         业务id集合
     * @param paymentBusinessType 业务类型
     * @param paymentId           支付记录Id
     */
    void savePaymentRecordsBusiness(List<String> businessIds, PaymentBusinessTypeEnum paymentBusinessType, String paymentId);

    /**
     * 完成当前支付
     *
     * @param paymentId 支付记录Id
     * @throws PaymentRepeatException
     */
    void finishPaymentRecord(String paymentId, PaymentGatewayEnum paymentGatewayEnum) throws PaymentRepeatException;

    /**
     * 完成当前支付
     *
     * @param paymentRecords 支付记录
     * @throws PaymentRepeatException
     */
    void finishPaymentRecord(PaymentRecordsT paymentRecords) throws PaymentRepeatException;

    /**
     * 删除业务
     *
     * @param businessId          业务Id
     * @param paymentBusinessType 业务类型
     */
    void deletePaymentRecordsBusinessR(String businessId, PaymentBusinessTypeEnum paymentBusinessType);

    /**
     * 根据开始时间结束时间查询支付记录集合
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 支付记录记录集合
     */
    List<PaymentRecordsT> findPaymentByTime(Date startTime, Date endTime);

    /**
     * 检查是否需要关闭
     *
     * @param paymentRecordsT 支付记录
     * @return true 应该关闭  false 不应关闭
     */
    boolean checkPaymentClose(PaymentRecordsT paymentRecordsT);

    /**
     * * 关闭支付记录
     * 即将支付记录中的关于预支付信息置为空
     *
     * @param paymentRecordsT 支付记录
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     */
    void closePayment(PaymentRecordsT paymentRecordsT) throws PaymentClosedException, PaymentRepeatException;

    /**
     * @param refundMoney 退款金额
     * @param paymentId   退款记录id
     * @throws PaymentRefundErrorException
     */
    void refundMoney(BigDecimal refundMoney, String paymentId) throws PaymentRefundErrorException;

    /**
     * @param paymentMethod 支付路由
     * @param paymentId     支付记录Id
     * @throws PaymentPreviousErrorException
     */
    void previousPayment(PaymentGatewayEnum paymentMethod, String paymentId) throws PaymentPreviousErrorException;

    /**
     * 预支付统一更新入口
     *
     * @param payperId      预支付Id
     * @param paymentMethod 支付路由
     * @param paymentId     支付记录Id
     * @throws PaymentPreviousErrorException
     */
    void previousPayment(String payperId, PaymentGatewayEnum paymentMethod, String paymentId) throws PaymentPreviousErrorException;
}
