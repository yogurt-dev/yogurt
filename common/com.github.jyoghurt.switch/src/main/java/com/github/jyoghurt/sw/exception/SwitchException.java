package com.github.jyoghurt.sw.exception;

import com.github.jyoghurt.sw.exception.enums.SwitchExceptionEnum;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.exception.ExceptionBody;

/**
 * 异常操作
 */
public class SwitchException extends BaseErrorException {

    public SwitchException() {
        super(new ExceptionBody(SwitchExceptionEnum.ERROR_631.name(), SwitchExceptionEnum.ERROR_631.getMessage()));
    }

    public SwitchException(Exception e) {
        super(new ExceptionBody(SwitchExceptionEnum.ERROR_631.name(), SwitchExceptionEnum.ERROR_631.getMessage()), e);
    }

    public SwitchException(String message) {
        super(new ExceptionBody(SwitchExceptionEnum.ERROR_631.name(), message));
    }

    public SwitchException(String message, Exception e) {
        super(new ExceptionBody(SwitchExceptionEnum.ERROR_631.name(), message), e);
    }
}
