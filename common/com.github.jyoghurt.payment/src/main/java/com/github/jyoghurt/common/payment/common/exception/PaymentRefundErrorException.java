package com.github.jyoghurt.common.payment.common.exception;

import com.github.jyoghurt.common.payment.common.exception.enums.PaymentExceptionEnum;
import com.github.jyoghurt.core.exception.BaseAccidentException;
import com.github.jyoghurt.core.exception.ExceptionBody;


/**
 * user:zjl
 * date: 2016/12/26.
 */
public class PaymentRefundErrorException extends BaseAccidentException{

    private static final ExceptionBody ERROR_6006 = new ExceptionBody(PaymentExceptionEnum.ERROR_6006);

    public PaymentRefundErrorException() {
        super(ERROR_6006);
    }
}
