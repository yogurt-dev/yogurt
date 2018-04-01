package com.github.jyoghurt.common.payment.common.service;

import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.enums.PaymentPlatFormEnum;
import com.github.jyoghurt.common.payment.common.exception.PaymentPreRepeatException;
import com.github.jyoghurt.common.payment.common.exception.PaymentRefundedException;
import com.github.jyoghurt.common.payment.common.exception.PaymentRepeatException;
import com.github.jyoghurt.common.payment.common.enums.PaymentBusinessTypeEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentStateEnum;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.common.payment.common.exception.PaymentClosedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * user:dell
 * date: 2016/5/18.
 */
@Service
public class PaymentValidateService {
    /**
     * 支付服务接口
     */
    @Autowired
    private PaymentService paymentService;
    /**
     * 支付记录服务接口
     */
    @Autowired
    private PaymentRecordsService paymentRecordsService;

    /**
     * 验证是否可以进行预支付
     *
     * @param paymentRecordsT 支付记录
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     * @throws PaymentPreRepeatException
     */
    public PaymentStateEnum verifyAdvancePayment(PaymentRecordsT paymentRecordsT) throws PaymentClosedException, PaymentPreRepeatException, PaymentRepeatException, PaymentRefundedException {
        //若支付记录已被取消则抛出异常
        if (paymentRecordsT.getDeleteFlag()) {
            throw new PaymentClosedException();
        }
        //若为支付宝或微信已近预支付过的支付记录 不可重复预支付 避免页面刷新
        if (null != paymentRecordsT.getPaymentMethod()
                && (PaymentPlatFormEnum.TENCENT_PAY == paymentRecordsT.getPaymentMethod().getPaymentPlatForm()
                || PaymentPlatFormEnum.ALI_PAY == paymentRecordsT.getPaymentMethod().getPaymentPlatForm())) {
            throw new PaymentPreRepeatException();
        }
        //调用支付模块基础验证
        return this.validatePayment(paymentRecordsT);
    }

    /**
     * 验证是否可以进行支付
     *
     * @param paymentId 支付记录Id
     * @return 验证信息 true可以支付  false不可以支付
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     */
    public boolean verifyPaymentState(String paymentId) throws PaymentClosedException, PaymentRepeatException, PaymentRefundedException {
        PaymentRecordsT paymentRecordsT = paymentRecordsService.find(paymentId);
        return verifyPaymentState(paymentRecordsT);
    }

    /**
     * 验证是否可以进行支付
     *
     * @param paymentRecordsT 支付记录
     * @return 验证信息 true可以支付  false不可以支付
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     */
    public boolean verifyPaymentState(PaymentRecordsT paymentRecordsT) throws PaymentClosedException, PaymentRepeatException, PaymentRefundedException {
        //调用支付模块基础验证
        PaymentStateEnum paymentState = this.validatePayment(paymentRecordsT);
        if (paymentRecordsT.getDeleteFlag()) {
            throw new PaymentClosedException();
        }
        //若业务验证失败则不允许重复支付
        switch (paymentState) {
            case SUCCESS:
                throw new PaymentRepeatException();
            case CLOSED:
                throw new PaymentClosedException();
            case NOTPAY:
                return true;
            case REFUND:
                throw new PaymentRefundedException();
            default:
                throw new BaseErrorException("validateRealPaymentAndSync,检查支付状态异常，枚举无法找到,枚举类型:{0},paymentId:{1}",
                        paymentState.name(), paymentRecordsT.getPaymentId());
        }
    }

    /**
     * 验证支付状态
     *
     * @param paymentRecord 支付记录
     * @return PaymentStateEnum 返回支付状态
     */
    public PaymentStateEnum validatePayment(PaymentRecordsT paymentRecord) throws PaymentClosedException, PaymentRepeatException, PaymentRefundedException {
        //验证与其相关联的所有支付记录  包含其本身
        validateRelevancePayment(paymentRecord);
        //若验证关联记录中无异常
        //根据支付记录无法准确校验出当前支付的具体状态 通过查询对应接口获取实际的支付状态 并返回
        return validateRealPaymentAndSync(paymentRecord);
    }

    /**
     * 根据业务校验支付状态
     *
     * @param paymentBusinessTypeEnum 支付业务类型
     * @param businessIds             业务Id集合
     * @return true验证通过 此次筛选的业务均可进行支付
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     * @throws PaymentClosedException
     */
    public boolean validateBusinessPaymentState(PaymentBusinessTypeEnum paymentBusinessTypeEnum, List<String> businessIds) throws PaymentRepeatException, PaymentRefundedException, PaymentClosedException {
        if (businessIds.size() == 0) {
            return false;
        }
        List<PaymentRecordsT> paymentRecordsTList = paymentRecordsService
                .findPaymentRecordsByBusIds(businessIds, paymentBusinessTypeEnum);
        for (PaymentRecordsT paymentRecordsT : paymentRecordsTList) {
            relevancePaymentValidateSign(validateSinglePaymentAndSync(paymentRecordsT));
        }
        return true;
    }


    /**
     * 所有关联支付校验
     * <p>
     * 通过当前payment记录无法进行准确的判断
     * 再通过查询与其关联的所有支付记录进行判断
     *
     * @param paymentRecord 支付记录
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     */
    private boolean validateRelevancePayment(PaymentRecordsT paymentRecord) throws PaymentClosedException, PaymentRepeatException, PaymentRefundedException {
        //查询订单关联的所有生效支付记录
        List<PaymentRecordsT> paymentRecords = paymentRecordsService.findPaymentRecords(paymentRecord);
        for (PaymentRecordsT paymentRecordsT : paymentRecords) {
            //若支付记录为其本身则跳过
            relevancePaymentValidateSign(validateSinglePaymentAndSync(paymentRecordsT));
        }
        return true;
    }

    /**
     * 关联支付记录验证标准
     *
     * @param paymentStateEnum 支付状态
     * @throws PaymentRepeatException
     * @throws PaymentRefundedException
     */
    private void relevancePaymentValidateSign(PaymentStateEnum paymentStateEnum) throws PaymentRepeatException, PaymentRefundedException, PaymentClosedException {
        switch (paymentStateEnum) {
            case SUCCESS://任意已支付的支付记录  抛出重复支付异常
                throw new PaymentRepeatException();
            case REFUND://任意退款的支付记录均需要跳出验证 并返回已退款
                throw new PaymentRefundedException();
            case CLOSED:
                throw new PaymentClosedException();
        }
    }

    /**
     * 验证单独一个支付记录的实际支付状态
     *
     * @param paymentRecord 支付记录
     * @return 支付状态
     */
    public PaymentStateEnum validateSinglePaymentAndSync(PaymentRecordsT paymentRecord) {
        //普通校验结果  (首先根据支付记录进行校验)
        PaymentStateEnum commonState = validateCommonPayment(paymentRecord);
        if (null != commonState) {
            //若普通校验已经验证出结果则直接返回无需准确校验
            return commonState;
        }
        return validateRealPaymentAndSync(paymentRecord);
    }

    /**
     * 私有方法 验证支付记录的支付状态
     * <p>
     * 此方法只验证支付记录本身属性 不能作为严格的支付验证使用
     *
     * @param paymentRecord 支付记录
     * @return 若验证出结果了返回支付状态  若验证后仍不知结果 返回null
     */
    private PaymentStateEnum validateCommonPayment(PaymentRecordsT paymentRecord) {
        if (null == paymentRecord) {
            throw new BaseErrorException("完成支付异常,支付记录为空");
        }
        if (null == paymentRecord.getPaymentMethod()) {
            return PaymentStateEnum.NOTPAY;
        }
        //若支付记录已被取消 则未已取消支付记录
        if (paymentRecord.getDeleteFlag()) {
            return PaymentStateEnum.CLOSED;
        }
        //若支付记录中无支付状态 属于异常订单不予支付
        if (null == paymentRecord.getPaymentState()) {
            throw new BaseErrorException("支付记录中无支付状态 ，于异常支付记录不予支付，paymentId:{0}", paymentRecord.getPaymentId());
        }
        //支付状态为已支付
        if (paymentRecord.getPaymentState()) {
            try {
                paymentRecordsService.finishPaymentRecord(paymentRecord);
            } catch (PaymentRepeatException e) {
                return PaymentStateEnum.SUCCESS;
            }
        }
        return null;
    }


    /**
     * 校验实际支付状态
     * <p>
     * 根据支付记录无法准确校验出当前支付的具体状态
     * 需查询支付记录对应的支付平台
     * 判断当前支付记录真正的支付状态
     *
     * @param paymentRecord 支付记录
     * @return 支付状态
     */
    private PaymentStateEnum validateRealPaymentAndSync(PaymentRecordsT paymentRecord) {
        if (null == paymentRecord.getPaymentMethod() && !paymentRecord.getPaymentState() && !paymentRecord.getDeleteFlag()) {
            return PaymentStateEnum.NOTPAY;
        }
        //其他情况根据工单编号查询支付接口，查看是否支付成功
        PaymentStateEnum paymentStateEnum = paymentService.queryPaymentResult(paymentRecord.getPaymentMethod(), paymentRecord);
        //根据订单的支付状态校验订单
        switch (paymentStateEnum) {
            case SUCCESS:
                //订单状态为已支付
                //那么更新支付记录为完成状态
                try {
                    paymentRecordsService.finishPaymentRecord(paymentRecord);
                } catch (PaymentRepeatException e) {
                    return PaymentStateEnum.SUCCESS;
                }
                return PaymentStateEnum.SUCCESS;
            case CLOSED:
                paymentService.closePayment(paymentRecord);
                return PaymentStateEnum.CLOSED;
            case NOTPAY:
                return PaymentStateEnum.NOTPAY;
            case REFUND:
                return PaymentStateEnum.REFUND;
            default: {
                throw new BaseErrorException("validateRealPaymentAndSync,检查支付状态异常，枚举无法找到,枚举类型:{0},paymentId:{1}",
                        paymentStateEnum.name(), paymentRecord.getPaymentId());
            }
        }
    }

    /**
     * 验证现金支付 实际支付金额是否大于应支付金额
     *
     * @param paymentRecordsT 支付记录
     * @param paymentAmount   支付金额
     * @return true验证通过  false验证不通过
     */
    public Boolean checkCashPay(PaymentRecordsT paymentRecordsT, BigDecimal paymentAmount) {
        return null != paymentRecordsT && null != paymentRecordsT.getTotleFee() && -1 != paymentAmount.compareTo(paymentRecordsT.getTotleFee());
    }

}
