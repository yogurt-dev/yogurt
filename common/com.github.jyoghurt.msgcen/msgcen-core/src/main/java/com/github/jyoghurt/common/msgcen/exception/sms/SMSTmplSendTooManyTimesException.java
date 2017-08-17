package com.github.jyoghurt.common.msgcen.exception.sms;

import com.github.jyoghurt.common.msgcen.exception.MsgException;

/**
 * user:zjl
 * date: 2016/12/14.
 */
public class SMSTmplSendTooManyTimesException extends MsgException {
    public SMSTmplSendTooManyTimesException() {
        super();
    }

    public SMSTmplSendTooManyTimesException(Throwable e) {
        super(e);
    }
}
