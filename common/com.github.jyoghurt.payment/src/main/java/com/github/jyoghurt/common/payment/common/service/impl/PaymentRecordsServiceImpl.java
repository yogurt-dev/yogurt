package com.github.jyoghurt.common.payment.common.service.impl;


import com.github.jyoghurt.common.payment.common.constants.PaymentCommonConstants;
import com.github.jyoghurt.common.payment.common.dao.PaymentRecordsMapper;
import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsBusinessR;
import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.enums.PaymentBusinessTypeEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.common.payment.common.enums.PaymentResultTypeEnum;
import com.github.jyoghurt.common.payment.common.exception.*;
import com.github.jyoghurt.common.payment.common.module.BaseCallBackParam;
import com.github.jyoghurt.common.payment.common.module.PaymentRecordResult;
import com.github.jyoghurt.common.payment.common.module.PaymentRequest;
import com.github.jyoghurt.common.payment.common.service.*;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.handle.OperatorHandle;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("paymentRecordsService")
public class PaymentRecordsServiceImpl extends ServiceSupport<PaymentRecordsT, PaymentRecordsMapper> implements PaymentRecordsService {
    private static Logger logger = LoggerFactory.getLogger(PaymentRecordsServiceImpl.class);
    @Autowired
    private PaymentRecordsMapper paymentRecordsMapper;
    @Autowired
    private PaymentCallBackService paymentCallBackService;
    @Autowired
    private PaymentRecordsBusinessService paymentRecordsBusinessService;
    @Autowired
    private PaymentValidateService paymentValidateService;
    @Autowired
    private PaymentManagerService paymentManagerService;

    @Override
    public PaymentRecordsMapper getMapper() {
        return paymentRecordsMapper;
    }

    @Override
    public void logicDelete(Serializable id) {
        getMapper().logicDelete(PaymentRecordsT.class, id);
    }

    @Override
    public PaymentRecordsT find(Serializable id) {
        return getMapper().selectById(PaymentRecordsT.class, id);
    }


    /**
     * 生成支付记录
     *
     * @param paymentRequest 支付请求
     * @return 支付记录展示数据
     */
    @Override
    public PaymentRecordResult prePaymentRecords(PaymentRequest paymentRequest) {
        try {
            paymentValidateService.validateBusinessPaymentState(paymentRequest.getPaymentBusinessType(), paymentRequest.getBusinessIds());
        } catch (PaymentRepeatException e) {
            throw new BaseErrorException("生成支付记录失败，存在已支付业务Id，paymentDetail：{0}", paymentRequest.getPaymentDetail());
        } catch (PaymentRefundedException e) {
            throw new BaseErrorException("生成支付记录失败，存在已退款业务Id，paymentDetail：{0}", paymentRequest.getPaymentDetail());
        } catch (PaymentClosedException e) {
            //根据业务查询到 支付记录合 支付记录集合中存在已经取消的支付记录为正常 因为该支付记录已经取消掉 不会再进行任何操作 所以不会对当前支付造成任何影响
            logger.debug("根据业务查询到 支付记录合 支付记录集合中存在已经取消的支付记录为正常 因为该支付记录已经取消掉 不会再进行任何操作 所以不会对当前支付造成任何影响");
        }
        PaymentRecordsT paymentRecordsT = new PaymentRecordsT();
        if (null == paymentRequest.getTotalFee()) {
            throw new BaseErrorException("生成支付记录失败，缺少支付金额，paymentId：{0}", paymentRecordsT.getPaymentId());
        }
        if (null == paymentRequest.getModuleName()) {
            throw new BaseErrorException("生成支付记录失败，缺少模块名称，paymentId：{0}", paymentRecordsT.getPaymentId());
        }
        if (null == paymentRequest.getPaymentDetail()) {
            throw new BaseErrorException("生成支付记录失败，支付详细信息{0}，paymentId：{1}", paymentRecordsT.getPaymentId());
        }
        paymentRecordsT.setTotleFee(paymentRequest.getTotalFee());
        paymentRecordsT.setPaymentBusinessType(paymentRequest.getModuleName());
        paymentRecordsT.setPaymentDetil(paymentRequest.getPaymentDetail());
        paymentRecordsT.setBussinessNum(paymentRequest.getOrderNum());
        //设置相关数据区
        if (null != paymentRequest.getDataArea()) {
            paymentRecordsT.setDataAreaMap(paymentRequest.getDataArea());
        }
        return producePaymentRecord(paymentRecordsT, paymentRequest.getBusinessIds(), paymentRequest.getPaymentBusinessType());
    }

    /**
     * 生成支付记录并保存相关数据信息
     *
     * @param paymentRecordsT         支付记录
     * @param businessIds             业务Id集合
     * @param paymentBusinessTypeEnum 业务类型
     * @return 支付结果
     */
    private PaymentRecordResult producePaymentRecord(PaymentRecordsT paymentRecordsT, List<String> businessIds, PaymentBusinessTypeEnum paymentBusinessTypeEnum) {
        //是否需要支付 true为是  false为否
        boolean paymentFlag = true;
        if (0 == BigDecimal.ZERO.compareTo(paymentRecordsT.getTotleFee())) {
            //无需支付
            paymentFlag = false;
        }
        //若支付金额为0元  那默认支付为成功  并回调业务
        paymentRecordsT.setPaymentMethod(paymentFlag ? null : PaymentGatewayEnum.ONLY_LUCK_OR_BALANCE_PAY);
        paymentRecordsT.setPaymentState(false);
        paymentRecordsT.setResponseState(false);
        savePayment(paymentRecordsT, businessIds, paymentBusinessTypeEnum);
        //如果是不需要支付的订单则直接完成
        //若支付金额为0元  那默认支付为成功  并回调业务
        if (!paymentFlag) {
            try {
                this.finishPaymentRecord(paymentRecordsT, businessIds, paymentBusinessTypeEnum);
            } catch (PaymentRepeatException e) {
                logger.info("已支付，支付Id:{}", paymentRecordsT.getPaymentId());
            }
        }
        //若需支付的金额为0元 完成支付
        PaymentRecordResult paymentRecordResult = new PaymentRecordResult();
        paymentRecordResult.setPaymentId(paymentRecordsT.getPaymentId());
        paymentRecordResult.setPaymentPlatForm(null == paymentRecordsT.getPaymentMethod() ?
                PaymentGatewayEnum.CASH_PAY.getPaymentPlatForm() :
                paymentRecordsT.getPaymentMethod().getPaymentPlatForm());
        paymentRecordResult.setPaymentDetil(paymentRecordsT.getPaymentDetil());
        paymentRecordResult.setTotleFee(paymentRecordsT.getTotleFee());
        paymentRecordResult.setOrderNum(paymentRecordsT.getBussinessNum());
        return paymentRecordResult;
    }

    private void savePayment(PaymentRecordsT paymentRecordsT, List<String> businessIds, PaymentBusinessTypeEnum paymentBusinessTypeEnum) {
        this.save(paymentRecordsT);
        //保存业务及支付记录中间表
        this.savePaymentRecordsBusiness(businessIds, paymentBusinessTypeEnum, paymentRecordsT.getPaymentId());

    }

    /**
     * @param paymentId 支付记录Id
     * @return PaymentRecordResult  待支付信息
     */
    @Override
    public PaymentRecordResult reBuildPaymentRecords(String paymentId) throws PaymentClosedException, PaymentRepeatException, PaymentRefundedException {
        PaymentRecordsT paymentRecordsT = this.find(paymentId);
        if (null == paymentRecordsT) {
            throw new BaseErrorException("完成支付异常,支付记录为空");
        }
        paymentValidateService.validatePayment(paymentRecordsT);
        List<PaymentRecordsBusinessR> paymentRecordsBusinessRs = paymentRecordsBusinessService.findAll(new
                PaymentRecordsBusinessR().setPaymentId(paymentId));
        List<String> businessIds = new ArrayList<>();
        for (PaymentRecordsBusinessR paymentRecordsBusinessR : paymentRecordsBusinessRs) {
            businessIds.add(paymentRecordsBusinessR.getBusinessId());
        }
        paymentRecordsT.setPaymentId(null);
        paymentRecordsT.setPaymentMethod(null);
        paymentRecordsT.setDeleteFlag(false);
        return producePaymentRecord(paymentRecordsT, businessIds, paymentRecordsT.getPaymentBusinessType());
    }

    /**
     * @param businessIds         业务id集合
     * @param paymentBusinessType 业务类型
     * @param paymentId           支付记录Id
     */
    @Override
    public void savePaymentRecordsBusiness(List<String> businessIds, PaymentBusinessTypeEnum paymentBusinessType, String paymentId) {
        List<PaymentRecordsBusinessR> paymentRecordsBusinessRs = new ArrayList<>();
        for (String businessId : businessIds) {
            PaymentRecordsBusinessR paymentRecordsBusinessR = new PaymentRecordsBusinessR();
            if (null == paymentBusinessType || StringUtils.isEmpty(paymentId)) {
                logger.error("保存业务及支付记录关联失败，数据回滚，失败原因:{}", "缺少参数");
                throw new BaseErrorException("保存业务及支付记录关联失败");
            }
            paymentRecordsBusinessR.setBusinessId(businessId).setBusinessType(paymentBusinessType).setPaymentId(paymentId);
            paymentRecordsBusinessRs.add(paymentRecordsBusinessR);
        }
        paymentRecordsBusinessService.saveBatch(paymentRecordsBusinessRs);
    }

    /**
     * 根据支付记录查询相关的所有支付记录
     * 首先到中间表查询相关所有业务Id集合
     * 根据业务Id集合查询所有与业务相关的支付记录并返回
     * <p>
     * 支付记录ID---->业务Ids----->关联的所有支付记录
     *
     * @param paymentRecordsT 支付记录
     * @return 关联支付记录集合
     */
    @Override
    public List<PaymentRecordsT> findPaymentRecords(PaymentRecordsT paymentRecordsT) {
        //获取全部业务集合
        List<PaymentRecordsBusinessR> businesses = findBusinessListByPaymentId(paymentRecordsT.getPaymentId());
        //支付记录id集合
        List<String> businessIds = businesses.stream().map(PaymentRecordsBusinessR::getBusinessId).collect(Collectors.toList());
        if (businessIds.size() == 0) {
            return new ArrayList<>();
        }
        return findPaymentRecordsByBusIds(businessIds, paymentRecordsT.getPaymentBusinessType());
    }

    /**
     * 根据支付记录查询相关的所有支付记录
     * 首先到中间表查询相关所有业务Id集合
     * 根据业务Id集合查询所有与业务相关的支付记录并返回
     * <p>
     * 支付记录ID---->业务Ids----->关联的所有支付记录
     *
     * @param paymentId 支付记录Id
     * @return 关联支付记录集合
     */
    @Override
    public List<PaymentRecordsT> findPaymentRecords(String paymentId) {
        return findPaymentRecords(this.find(paymentId));
    }

    /**
     * 根据业务Id集合查询所有的支付记录集合
     * 业务Ids----->关联的所有支付记录
     *
     * @param businessId          业务ID
     * @param paymentBusinessType 业务类型
     * @return 关联的所有支付记录
     */
    @Override
    public List<PaymentRecordsT> findPaymentRecordsByBusId(String businessId, PaymentBusinessTypeEnum paymentBusinessType) {
        if (org.springframework.util.StringUtils.isEmpty(businessId) || null == paymentBusinessType) {
            logger.error("查询最新支付记录失败，失败原因:{}", "缺少查询参数");
            throw new BaseErrorException("查询最新支付记录失败");
        }
        //查询最新中间表
        List<PaymentRecordsBusinessR> paymentRecordsBusinessRs = paymentRecordsBusinessService
                .findAll(new PaymentRecordsBusinessR().setBusinessId(businessId).setBusinessType(paymentBusinessType)
                        , new QueryHandle().addOrderBy("createDateTime", "desc"));
        //若查询关联表为空则返回空集合
        if (null == paymentRecordsBusinessRs || paymentRecordsBusinessRs.size() == 0) {
            return new ArrayList<>();
        }
        //获取最新一条的关联记录
        List<String> paymentIds = new ArrayList<>();
        //遍历获取支付记录集合
        paymentIds.addAll(paymentRecordsBusinessRs.stream().map(PaymentRecordsBusinessR::getPaymentId).collect(Collectors.toList()));
        if (paymentIds.size() == 0) {
            return new ArrayList<>();
        }
        //返回支付记录集合
        return this.findAll(new PaymentRecordsT().setDeleteFlag(false),
                new QueryHandle().addOperatorHandle("paymentId", OperatorHandle.operatorType.IN, paymentIds.toArray()));
    }

    @Override
    public List<PaymentRecordsT> findSuccessPaymentRecordsByBusId(String businessId, PaymentBusinessTypeEnum paymentBusinessType) {
        if (org.springframework.util.StringUtils.isEmpty(businessId) || null == paymentBusinessType) {
            logger.error("查询最新支付记录失败，失败原因:{}", "缺少查询参数");
            throw new BaseErrorException("查询最新支付记录失败");
        }
        //查询最新中间表
        List<PaymentRecordsBusinessR> paymentRecordsBusinessRs = paymentRecordsBusinessService
                .findAll(new PaymentRecordsBusinessR().setBusinessId(businessId).setBusinessType(paymentBusinessType)
                        , new QueryHandle().addOrderBy("createDateTime", "desc"));
        //若查询关联表为空则返回空集合
        if (null == paymentRecordsBusinessRs || paymentRecordsBusinessRs.size() == 0) {
            return new ArrayList<>();
        }
        //获取最新一条的关联记录
        List<String> paymentIds = new ArrayList<>();
        //遍历获取支付记录集合
        paymentIds.addAll(paymentRecordsBusinessRs.stream().map(PaymentRecordsBusinessR::getPaymentId).collect(Collectors.toList()));
        if (paymentIds.size() == 0) {
            return new ArrayList<>();
        }
        return this.findAll(new PaymentRecordsT().setDeleteFlag(false).setPaymentState(true),
                new QueryHandle().addOperatorHandle("paymentId", OperatorHandle.operatorType.IN, paymentIds.toArray()));
    }

    @Override
    public PaymentRecordsT findSuccessPaymentRecordsByPaymentId(String paymentId) {
        PaymentRecordsT paymentRecordsT = this.find(paymentId);
        if (paymentRecordsT.getPaymentState() && !paymentRecordsT.getDeleteFlag()) {
            return paymentRecordsT;
        }
        //查询最新中间表
        List<PaymentRecordsBusinessR> paymentRecordsBusinessRs = paymentRecordsBusinessService
                .findAll(new PaymentRecordsBusinessR().setPaymentId(paymentRecordsT.getPaymentId())
                        , new QueryHandle().addOrderBy("createDateTime", "desc"));
        //若查询关联表为空则返回空集合
        if (null == paymentRecordsBusinessRs || paymentRecordsBusinessRs.size() == 0) {
            return null;
        }
        //获取最新一条的关联记录
        List<String> businessIds = new ArrayList<>();
        //遍历获取支付记录集合
        businessIds.addAll(paymentRecordsBusinessRs.stream().map(PaymentRecordsBusinessR::getBusinessId).collect(Collectors.toList()));
        if (businessIds.size() == 0) {
            return null;
        }
        //根据业务id反向查关联
        //查询最新中间表
        List<PaymentRecordsBusinessR> recordsBusinessRs = paymentRecordsBusinessService
                .findAll(new PaymentRecordsBusinessR(),new QueryHandle().addOperatorHandle("businessId", OperatorHandle.operatorType.IN, businessIds.toArray()));
        //若查询关联表为空则返回空集合
        if (null == recordsBusinessRs || recordsBusinessRs.size() == 0) {
            return null;
        }
        //获取最新一条的关联记录
        List<String> paymentIds = new ArrayList<>();
        //遍历获取支付记录集合
        paymentIds.addAll(recordsBusinessRs.stream().map(PaymentRecordsBusinessR::getPaymentId).collect(Collectors.toList()));
        if (businessIds.size() == 0) {
            return null;
        }
        List<PaymentRecordsT> successPayList = this.findAll(new PaymentRecordsT().setDeleteFlag(false).setPaymentState(true),
                new QueryHandle().addOperatorHandle("paymentId", OperatorHandle.operatorType.IN, paymentIds.toArray()));
        if (successPayList.size() == 0) {
            return null;
        }
        if (successPayList.size() != 1) {
            throw new BaseErrorException("一个业务包含多条支付成功记录", paymentId);
        }
        return successPayList.get(0);
    }

    /**
     * 根据业务id集合查找支付记录集合
     *
     * @param businessIds         业务ID集合
     * @param paymentBusinessType 业务类型
     * @return 支付记录集合
     */
    @Override
    public List<PaymentRecordsT> findPaymentRecordsByBusIds(List<String> businessIds, PaymentBusinessTypeEnum paymentBusinessType) {
        if (businessIds.size() == 0) {
            return new ArrayList<>();
        }
        //查询最新中间表
        List<PaymentRecordsBusinessR> paymentRecordsBusinessRs = paymentRecordsBusinessService
                .findAll(new PaymentRecordsBusinessR().setBusinessType(paymentBusinessType), new QueryHandle()
                        .addOperatorHandle("businessId", OperatorHandle.operatorType.IN, businessIds.toArray())
                        .addOrderBy("createDateTime", "desc"));
        //若查询关联表为空则返回空集合
        if (null == paymentRecordsBusinessRs || paymentRecordsBusinessRs.size() == 0) {
            return new ArrayList<>();
        }
        //获取最新一条的关联记录
        List<String> paymentIds = new ArrayList<>();
        //遍历获取支付记录集合
        paymentIds.addAll(paymentRecordsBusinessRs.stream().map(PaymentRecordsBusinessR::getPaymentId).collect(Collectors.toList()));
        if (paymentIds.size() == 0) {
            return new ArrayList<>();
        }
        //返回支付记录集合
        return this.findAll(new PaymentRecordsT().setDeleteFlag(false),
                new QueryHandle().addOperatorHandle("paymentId", OperatorHandle.operatorType.IN, paymentIds.toArray()));
    }

    /**
     * 根据支付记录查找支付关联表集合
     *
     * @param paymentId 支付Id
     * @return 支付关联表集合
     */
    @Override
    public List<PaymentRecordsBusinessR> findBusinessListByPaymentId(String paymentId) {
        return paymentRecordsBusinessService.findAll(new PaymentRecordsBusinessR().setPaymentId(paymentId));
    }

    /**
     * 根据支付记录 查找支付关联Id集合
     *
     * @param paymentId 支付Id
     * @return 支付关联Id集合
     */
    @Override
    public List<String> findBusinessIdsByPaymentId(String paymentId) {
        List<PaymentRecordsBusinessR> paymentRecordsBusinessRs = paymentRecordsBusinessService
                .findAll(new PaymentRecordsBusinessR().setPaymentId(paymentId));
        return paymentRecordsBusinessRs.stream().map(PaymentRecordsBusinessR::getBusinessId).collect(Collectors.toList());
    }

    /**
     * 根据业务Id和业务类型 取消支付
     *
     * @param businessId          业务Id
     * @param paymentBusinessType 业务类型
     */
    @Override
    public void deletePaymentRecordsBusinessR(String businessId, PaymentBusinessTypeEnum paymentBusinessType) {
        //查询有效中间表
        List<PaymentRecordsBusinessR> paymentRecordsBusinessRs = paymentRecordsBusinessService.findAll(new PaymentRecordsBusinessR()
                .setBusinessId(businessId)
                .setBusinessType(paymentBusinessType)
                .setDeleteFlag(false), new QueryHandle().addOrderBy("createDateTime", "desc"));
        //若查询关联表为空则返回空
        if (null == paymentRecordsBusinessRs || paymentRecordsBusinessRs.size() == 0) {
            return;
        }
        //获取最新一条的关联记录
        List<String> paymentIds = new ArrayList<>();
        //遍历获取支付记录集合 并更新支付记录关联表状态为已删除
        for (PaymentRecordsBusinessR paymentRecordsBusinessR : paymentRecordsBusinessRs) {
            paymentRecordsBusinessService.updateForSelective(paymentRecordsBusinessR.setDeleteFlag(true));
            paymentIds.add(paymentRecordsBusinessR.getPaymentId());
        }
        //获取支付记录集合
        List<PaymentRecordsT> paymentRecords = this.findAll(new PaymentRecordsT().setDeleteFlag(false),
                new QueryHandle().addOperatorHandle("paymentId", OperatorHandle.operatorType.IN, paymentIds.toArray()));
        for (PaymentRecordsT paymentRecordsT : paymentRecords) {
            if (!paymentRecordsT.getPaymentState()) {
                try {
                    this.closePayment(paymentRecordsT);
                } catch (PaymentClosedException e) {
                    logger.info("支付Id为:{},详情为:{}的支付记录同步取消状态失败，该订单已取消",
                            paymentRecordsT.getPaymentId(),
                            paymentRecordsT.getPaymentDetil());
                } catch (PaymentRepeatException e) {
                    logger.info("支付Id为:{},详情为:{}的支付记录同步取消状态失败，该订单已支付",
                            paymentRecordsT.getPaymentId(),
                            paymentRecordsT.getPaymentDetil());
                }
            }
        }
    }

    /**
     * 根据开始时间 结束时间查找支付记录
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 支付记录集合
     */
    @Override
    public List<PaymentRecordsT> findPaymentByTime(Date startTime, Date endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryHandle queryHandle = new QueryHandle();
        if (null != startTime) {
            queryHandle.addWhereSql("t.createDateTime>=#{data.startTime}").addExpandData("startTime", sdf.format(startTime));
        }
        if (null != endTime) {
            queryHandle.addWhereSql("t.createDateTime<=#{data.endTime}").addExpandData("endTime", sdf.format(endTime));
        }
        return this.findAll(new PaymentRecordsT(), queryHandle);
    }

    /**
     * 完成支付记录
     *
     * @param paymentId          支付记录Id
     * @param paymentGatewayEnum 支付网关
     * @throws PaymentRepeatException
     */
    @Override
    public void finishPaymentRecord(String paymentId, PaymentGatewayEnum paymentGatewayEnum) throws PaymentRepeatException {
        //获取当前支付记录
        PaymentRecordsT paymentRecordsT = this.find(paymentId);
        paymentRecordsT.setPaymentMethod(paymentGatewayEnum);
        finishPaymentRecord(paymentRecordsT);
    }

    /**
     * 完成支付记录
     *
     * @param paymentRecordsT 支付记录
     * @throws PaymentRepeatException
     */
    @Override
    public void finishPaymentRecord(PaymentRecordsT paymentRecordsT) throws PaymentRepeatException {
        finishPaymentRecord(paymentRecordsT, null, null);
    }

    /**
     * 完成支付记录
     *
     * @param paymentRecordsT 支付记录
     * @throws PaymentRepeatException
     */
    private void finishPaymentRecord(PaymentRecordsT paymentRecordsT, List<String> businessIds, PaymentBusinessTypeEnum paymentBusinessTypeEnum) throws PaymentRepeatException {
        //若已经支付成功 即被同步则不继续执行
        if (null == paymentRecordsT) {
            throw new BaseErrorException("完成支付异常,支付记录为空");
        }
        if (null == paymentRecordsT.getPaymentState()) {
            paymentRecordsT.setPaymentState(false);
        }
        if (paymentRecordsT.getPaymentState()) {
            throw new PaymentRepeatException();
        }
        logger.info("===========================================================");
        logger.info("=======start=======支付Id为:{},详情为:{}的支付记录开始同步完成状态", paymentRecordsT.getPaymentId(),
                paymentRecordsT.getPaymentDetil());
        logger.info("===========================================================");
        int macthedNum = this.getMapper().updateFinishPayment(paymentRecordsT.getPaymentId(), paymentRecordsT
                .getPaymentMethod(), paymentRecordsT.getDataArea());
        logger.info("===========================================================");
        logger.info("支付Id为:{},详情为:{}的支付记录同步完成状态匹配条数为:{}", paymentRecordsT.getPaymentId(), paymentRecordsT.getPaymentDetil(), macthedNum);
        logger.info("===========================================================");
        //查询更新支付完成的匹配数量  若大于0证明匹配确实执行支付成功的回调方法 避免并发情况重复回调
        if (macthedNum == 0) {
            logger.info("完成支付异常,并发处理筛选已经同步过的支付记录，并不继续进行处理。支付记录Id：{}", paymentRecordsT.getPaymentId());
            throw new PaymentRepeatException();
        }
        logger.info("===========================================================");
        logger.info("支付Id为:{},详情为:{}的支付记录同步完成状态后进行回调业务",
                paymentRecordsT.getPaymentId(), paymentRecordsT.getPaymentDetil());
        //取消所有跟当前预支付相关的支付工单
        paymentManagerService.closePaymentsByPaymentId(paymentRecordsT.getPaymentId());
        try {
            paymentCallBackService.assignListener(createCallBackParam(paymentRecordsT, businessIds, paymentBusinessTypeEnum));
        } catch (Exception e) {
            logger.info("支付成功后回调业务失败,支付信息:{}", paymentRecordsT.toString());
        }
        logger.info("========end========支付Id为:{},详情为:{}的支付记录同步完成状态后进行回调业务",
                paymentRecordsT.getPaymentId(), paymentRecordsT.getPaymentDetil());
        logger.info("===========================================================");
    }

    private BaseCallBackParam createCallBackParam(PaymentRecordsT paymentRecordsT) {
        return createCallBackParam(paymentRecordsT, null, null);
    }

    private BaseCallBackParam createCallBackParam(PaymentRecordsT paymentRecordsT, List<String> businessIds, PaymentBusinessTypeEnum paymentBusinessTypeEnum) {
        BaseCallBackParam baseCallBckParam = new BaseCallBackParam();
        baseCallBckParam.setPaymentId(paymentRecordsT.getPaymentId());
        baseCallBckParam.setPaymentGatewayEnum(paymentRecordsT.getPaymentMethod());
        if (null != paymentRecordsT.getPaymentMethod()) {
            baseCallBckParam.setPaymentGatewayEnum(paymentRecordsT.getPaymentMethod());
        }
        if (null != businessIds) {
            baseCallBckParam.setBuinessIds(businessIds);
        }
        if (null != paymentBusinessTypeEnum) {
            baseCallBckParam.setPaymentBusinessTypeEnum(paymentBusinessTypeEnum);
        }
        baseCallBckParam.setCallBackService(paymentRecordsT.getPaymentBusinessType().getServiceName());
        baseCallBckParam.setPaymentResultTypeEnum(PaymentResultTypeEnum.SUCCESS);
        baseCallBckParam.setTotalFee(paymentRecordsT.getTotleFee());
        baseCallBckParam.setDataArea(paymentRecordsT.getDataAreaMap());
        return baseCallBckParam;
    }

    /**
     * 检查是否需要关闭
     *
     * @param paymentRecordsT 支付记录
     * @return true 应该关闭  false 不应关闭
     */
    @Override
    public boolean checkPaymentClose(PaymentRecordsT paymentRecordsT) {
        if (paymentRecordsT.getPaymentState()) {
            return false;
        }
        if (paymentRecordsT.getDeleteFlag()) {
            return false;
        }
        //根据超时时限计算超时的时间范围 且在超时边界外多括一分钟的范围 保证支付安全
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, -(Integer.valueOf(PaymentCommonConstants.PAYMENT_TIME_LIMIT)));
        long end_time = cal.getTime().getTime();
        return paymentRecordsT.getModifyDateTime().getTime() < end_time;
    }

    /**
     * @param paymentRecordsT 支付记录
     * @throws PaymentClosedException
     * @throws PaymentRepeatException
     */
    @Override
    public void closePayment(PaymentRecordsT paymentRecordsT) throws PaymentClosedException, PaymentRepeatException {
        if (paymentRecordsT.getPaymentState()) {
            throw new PaymentRepeatException();
        }
        logger.info("===========================================================");
        logger.info("======start======支付Id为:{},详情为:{}的支付记录同步取消状态", paymentRecordsT.getPaymentId(), paymentRecordsT
                .getPaymentDetil());
        logger.info("===========================================================");
        int macthedNum = this.getMapper().updateClosePayment(paymentRecordsT.getPaymentId());
        logger.info("===========================================================");
        logger.info("支付Id为:{},详情为:{}的支付记录同步取消状态匹配数量:{}", paymentRecordsT.getPaymentId(), paymentRecordsT
                .getPaymentDetil(), macthedNum);
        logger.info("===========================================================");
        if (macthedNum == 0) {
            throw new PaymentClosedException();
        }
        logger.info("======end======支付Id为:{},详情为:{}的支付记录同步取消状态", paymentRecordsT.getPaymentId(), paymentRecordsT.getPaymentDetil());
    }

    /**
     * @param refundMoney 退款金额
     * @param paymentId   退款记录id
     * @throws PaymentRefundErrorException
     */
    @Override
    public void refundMoney(BigDecimal refundMoney, String paymentId) throws PaymentRefundErrorException {
        logger.info("===========================================================");
        logger.info("======start======支付Id为:{}的支付记录申请退款,退款金额:{}", paymentId, refundMoney);
        logger.info("===========================================================");
        int macthedNum = this.getMapper().updateRefundedMoney(refundMoney, paymentId);
        if (macthedNum == 0) {
            logger.info("===========================================================");
            logger.info("======end======支付Id为:{}的支付记录退款失败,退款金额:{}", paymentId, refundMoney);
            logger.info("===========================================================");
            throw new PaymentRefundErrorException();
        }
        logger.info("===========================================================");
        logger.info("======end======支付Id为:{}的支付记录退款成功,退款金额:{}", paymentId, refundMoney);
        logger.info("===========================================================");
    }

    /**
     * @param paymentMethod 支付路由
     * @param paymentId     支付记录Id
     * @throws PaymentPreviousErrorException
     */
    @Override
    public void previousPayment(PaymentGatewayEnum paymentMethod, String paymentId) throws PaymentPreviousErrorException {
        previousPayment("", paymentMethod, paymentId);
    }

    /**
     * @param payperId      预支付Id
     * @param paymentMethod 支付路由
     * @param paymentId     支付记录Id
     * @throws PaymentPreviousErrorException
     */
    @Override
    public void previousPayment(String payperId, PaymentGatewayEnum paymentMethod, String paymentId) throws PaymentPreviousErrorException {
        logger.info("===========================================================");
        logger.info("======start======支付Id为:{}的支付记录申请预支付,支付方式:{}，预支付Id:{}", paymentId, paymentMethod.name(), payperId);
        logger.info("===========================================================");
        int macthedNum = this.getMapper().updatePreviousPayment(payperId, paymentMethod, paymentId);
        if (macthedNum == 0) {
            logger.info("===========================================================");
            logger.info("======end======支付Id为:{}的支付记录申请预支付失败,支付方式:{}，预支付Id:{}", paymentId, paymentMethod.name(), payperId);
            logger.info("===========================================================");
            throw new PaymentPreviousErrorException();
        }
        logger.info("===========================================================");
        logger.info("======end======支付Id为:{}的支付记录申请预支付成功,支付方式:{}，预支付Id:{}", paymentId, paymentMethod.name(), payperId);
        logger.info("===========================================================");
    }
}
