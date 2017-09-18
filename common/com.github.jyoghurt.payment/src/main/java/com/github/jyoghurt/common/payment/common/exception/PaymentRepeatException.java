package com.github.jyoghurt.common.payment.common.exception;

import com.github.jyoghurt.common.payment.common.exception.enums.PaymentExceptionEnum;
import com.github.jyoghurt.core.exception.ExceptionBody;

/**
 * user:zjl
 * date: 2016/10/31.
 */
public class PaymentRepeatException extends PaymentException {
    private static final ExceptionBody ERROR_6001 = new ExceptionBody(PaymentExceptionEnum.ERROR_6001);

    public PaymentRepeatException() {
        super(ERROR_6001);
    }
}
