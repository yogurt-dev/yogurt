package com.github.jyoghurt.common.msgcen.exception.sms;

import com.github.jyoghurt.common.msgcen.exception.MsgException;
import com.github.jyoghurt.common.msgcen.exception.enums.MsgExceptionEnum;
import com.github.jyoghurt.core.exception.ExceptionBody;

/**
 * user:zjl
 * date: 2016/12/14.
 */
public class SMSSendTooManyPhonesException extends MsgException {
    private static final ExceptionBody ERROR_9003 = new ExceptionBody(MsgExceptionEnum.ERROR_9003);

    public SMSSendTooManyPhonesException(Object... obj) {
        super(ERROR_9003, obj);
    }
}
