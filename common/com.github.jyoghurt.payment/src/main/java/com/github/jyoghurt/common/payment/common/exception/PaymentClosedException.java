package com.github.jyoghurt.common.payment.common.exception;

import com.github.jyoghurt.common.payment.common.exception.enums.PaymentExceptionEnum;
import com.github.jyoghurt.core.exception.ExceptionBody;

/**
 * user:zjl
 * date: 2016/10/31.
 */
public class PaymentClosedException extends PaymentException {
    private static final ExceptionBody ERROR_6002 = new ExceptionBody(PaymentExceptionEnum.ERROR_6002);

    public PaymentClosedException() {
        super(ERROR_6002);
    }
}
