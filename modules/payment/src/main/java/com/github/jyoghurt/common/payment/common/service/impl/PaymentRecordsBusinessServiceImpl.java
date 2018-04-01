package com.github.jyoghurt.common.payment.common.service.impl;


import com.github.jyoghurt.common.payment.common.domain.PaymentRecordsBusinessR;
import com.github.jyoghurt.common.payment.common.service.PaymentCallBackService;
import com.github.jyoghurt.common.payment.common.service.PaymentRecordsBusinessService;
import com.github.jyoghurt.common.payment.common.service.PaymentRecordsService;
import com.github.jyoghurt.common.payment.common.dao.PaymentRecordsBusinessMapper;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("paymentRecordsBusinessService")
public class PaymentRecordsBusinessServiceImpl extends ServiceSupport<PaymentRecordsBusinessR, PaymentRecordsBusinessMapper> implements PaymentRecordsBusinessService {
    private static Logger logger = LoggerFactory.getLogger(PaymentRecordsBusinessServiceImpl.class);
    @Autowired
    private PaymentRecordsService paymentRecordsService;
    @Autowired
    private PaymentRecordsBusinessMapper paymentRecordsBusinessMapper;
    @Autowired
    private PaymentCallBackService paymentCallBackService;

    @Override
    public PaymentRecordsBusinessMapper getMapper() {
        return paymentRecordsBusinessMapper;
    }

    @Override
    public void logicDelete(Serializable id)  {
        getMapper().logicDelete(PaymentRecordsBusinessR.class, id);
    }

    @Override
    public PaymentRecordsBusinessR find(Serializable id)  {
        return getMapper().selectById(PaymentRecordsBusinessR.class, id);
    }

}
