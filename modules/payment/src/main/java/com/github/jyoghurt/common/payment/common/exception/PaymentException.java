package com.github.jyoghurt.common.payment.common.exception;

import com.github.jyoghurt.core.exception.BaseAccidentException;
import com.github.jyoghurt.core.exception.ExceptionBody;


public class PaymentException extends BaseAccidentException {
    private static final long serialVersionUID = 2657618496024743577L;

    private static String commonMsg = "您好，系统异常，我们正在拼命修复，给您带来的不便请谅解！";
    /**
     * 未知异常
     */
    public static final ExceptionBody UNKNOW_EXCEPTION = new ExceptionBody("6999", commonMsg);


    public PaymentException(ExceptionBody exceptionBody) {
        super(exceptionBody);
    }

    public PaymentException(ExceptionBody exceptionBody, Throwable cause) {
        super(exceptionBody, cause);
    }

    public PaymentException(ExceptionBody exceptionBody, Object... obj) {
        super(exceptionBody, obj);
    }

    public PaymentException(ExceptionBody exceptionBody, Throwable cause, Object... obj) {
        super(exceptionBody, cause, obj);
    }
}