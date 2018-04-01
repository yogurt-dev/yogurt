package com.github.jyoghurt.common.payment.common.dao;


import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsBusinessR;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * PaymentRecordsBusiness Mapper
 *
 */
public interface PaymentRecordsBusinessMapper extends BaseMapper<PaymentRecordsBusinessR> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    PaymentRecordsBusinessR selectById(@Param(ENTITY_CLASS) Class<PaymentRecordsBusinessR> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<PaymentRecordsBusinessR> pageData(@Param(ENTITY_CLASS) Class<PaymentRecordsBusinessR> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<PaymentRecordsBusinessR> findAll(@Param(ENTITY_CLASS) Class<PaymentRecordsBusinessR> entityClass, @Param(DATA) Map<String, Object> data);
}
