package com.github.jyoghurt.msgcen.exception.sms;

import com.github.jyoghurt.msgcen.exception.MsgException;

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
