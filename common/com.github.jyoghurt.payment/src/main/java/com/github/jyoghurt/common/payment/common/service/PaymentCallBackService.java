package com.github.jyoghurt.common.payment.common.service;

import com.github.jyoghurt.common.payment.common.module.BaseCallBackParam;


public interface PaymentCallBackService {
    void assignListener(BaseCallBackParam baseCallBackParam);
}
