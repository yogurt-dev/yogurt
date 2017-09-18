package com.github.jyoghurt.common.payment.common.dao;


import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsT;
import com.github.jyoghurt.common.payment.common.enums.PaymentGatewayEnum;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * PaymentRecords Mapper
 */
public interface PaymentRecordsMapper extends BaseMapper<PaymentRecordsT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    PaymentRecordsT selectById(@Param(ENTITY_CLASS) Class<PaymentRecordsT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<PaymentRecordsT> pageData(@Param(ENTITY_CLASS) Class<PaymentRecordsT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<PaymentRecordsT> findAll(@Param(ENTITY_CLASS) Class<PaymentRecordsT> entityClass, @Param(DATA) Map<String, Object> data);


    @Update("update PaymentRecordsT set deleteFlag=1 where paymentId=#{paymentId} and paymentState=0 and deleteFlag=0")
    int updateClosePayment(@Param("paymentId") String paymentId);

    //支付完成 统一操作数据库sql  保证并发场景数据一致性
    @Update("update PaymentRecordsT set paymentState=1,responseState=1,paymentMethod=#{paymentMethod},dataArea=#{dataArea} " +
            "where paymentId=#{paymentId} and paymentState=0 and deleteFlag=0")
    int updateFinishPayment(@Param("paymentId") String paymentId, @Param("paymentMethod") PaymentGatewayEnum
            paymentMethod, @Param("dataArea") String dataArea);

    //退款统一更新 支付记录中的已退款金额
    @Update("update PaymentRecordsT set refundedMoney=refundedMoney+#{refundMoney} where paymentId=#{paymentId} and" +
            " paymentState=1 and deleteFlag=0 and totleFee-refundedMoney>=#{refundMoney}")
    int updateRefundedMoney(@Param("refundMoney") BigDecimal refundMoney, @Param("paymentId") String paymentId);

    //预支付统一更新
    @Update("update PaymentRecordsT set payperId=#{payperId},paymentMethod=#{paymentMethod} where " +
            "paymentId=#{paymentId} and" +
            " paymentState=0 and deleteFlag=0")
    int updatePreviousPayment(@Param("payperId") String payperId, @Param("paymentMethod") PaymentGatewayEnum
            paymentMethod, @Param("paymentId") String paymentId);
}
