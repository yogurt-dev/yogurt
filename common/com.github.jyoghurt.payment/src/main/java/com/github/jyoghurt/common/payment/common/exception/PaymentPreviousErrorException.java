package com.github.jyoghurt.common.payment.common.exception;

import com.github.jyoghurt.common.payment.common.exception.enums.PaymentExceptionEnum;
import com.github.jyoghurt.core.exception.BaseAccidentException;
import com.github.jyoghurt.core.exception.ExceptionBody;

/**
 * user:zjl
 * date: 2017/1/3.
 */
public class PaymentPreviousErrorException extends BaseAccidentException {
    private static final ExceptionBody ERROR_6007 = new ExceptionBody(PaymentExceptionEnum.ERROR_6007);

    public PaymentPreviousErrorException() {
        super(ERROR_6007);
    }
}
