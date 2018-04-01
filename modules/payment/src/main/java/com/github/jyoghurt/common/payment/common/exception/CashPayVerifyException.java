package com.github.jyoghurt.common.payment.common.exception;

import com.github.jyoghurt.common.payment.common.exception.enums.PaymentExceptionEnum;
import com.github.jyoghurt.core.exception.ExceptionBody;

/**
 * user:zjl
 * date: 2016/10/31.
 */
public class CashPayVerifyException extends PaymentException {
    private static final ExceptionBody ERROR_6401 = new ExceptionBody(PaymentExceptionEnum.ERROR_6401);

    public CashPayVerifyException(Object... obj) {
        super(ERROR_6401, obj);
    }

}
