package com.github.jyoghurt.msgcen.exception;

import com.github.jyoghurt.core.exception.BaseAccidentException;
import com.github.jyoghurt.core.exception.ExceptionBody;

/**
 * user:zjl
 * date: 2016/11/21.
 */
public class MsgException extends BaseAccidentException {
    public MsgException() {
        super();
    }

    public MsgException(Throwable e) {
        super(e);
    }

    public MsgException(ExceptionBody exceptionBody, Object... objects) {
        super(exceptionBody, objects);
    }
}

