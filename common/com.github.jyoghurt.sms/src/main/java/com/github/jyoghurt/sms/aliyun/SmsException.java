package com.github.jyoghurt.sms.aliyun;

import com.github.jyoghurt.sms.SmsExceptionEnums;
import com.github.jyoghurt.core.exception.BaseAccidentException;
import com.github.jyoghurt.core.exception.ExceptionBody;

/**
 *
 */
public class SmsException extends BaseAccidentException {
    public SmsException(Object... param) {
        super(new ExceptionBody(SmsExceptionEnums.ERROR_91100), param);
    }

    public SmsException(Throwable e, Object... param) {
        super(new ExceptionBody(SmsExceptionEnums.ERROR_91100), e, param);
    }
}