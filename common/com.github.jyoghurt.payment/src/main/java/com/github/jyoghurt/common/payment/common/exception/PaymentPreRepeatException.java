package com.github.jyoghurt.common.payment.common.exception;

import com.github.jyoghurt.common.payment.common.exception.enums.PaymentExceptionEnum;
import com.github.jyoghurt.core.exception.ExceptionBody;

/**
 * user:zjl
 * date: 2016/10/31.
 */
public class PaymentPreRepeatException extends PaymentException {
    private static final ExceptionBody ERROR_6004 = new ExceptionBody(PaymentExceptionEnum.ERROR_6004);

    public PaymentPreRepeatException() {
        super(ERROR_6004);
    }
}
