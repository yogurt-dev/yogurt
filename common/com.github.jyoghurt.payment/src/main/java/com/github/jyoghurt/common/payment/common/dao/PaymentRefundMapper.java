package com.github.jyoghurt.common.payment.common.dao;


import com.github.jyoghurt.common.payment.common.domain.PaymentRefundT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * PaymentRefund Mapper
 *
 */
public interface PaymentRefundMapper extends BaseMapper<PaymentRefundT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    PaymentRefundT selectById(@Param(ENTITY_CLASS) Class<PaymentRefundT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<PaymentRefundT> pageData(@Param(ENTITY_CLASS) Class<PaymentRefundT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<PaymentRefundT> findAll(@Param(ENTITY_CLASS) Class<PaymentRefundT> entityClass, @Param(DATA) Map<String, Object> data);
}
