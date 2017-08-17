package com.github.jyoghurt.activiti.business.exception;

import com.github.jyoghurt.activiti.business.exception.enums.WorkFlowExceptionEnum;
import com.github.jyoghurt.core.exception.BaseAccidentException;
import com.github.jyoghurt.core.exception.ExceptionBody;

/**
 * user:zjl
 * date: 2017/1/9.
 */
public class RepetitionSubmitException extends BaseAccidentException{

    private static final ExceptionBody ERROR_90100 = new ExceptionBody(WorkFlowExceptionEnum.ERROR_90100);

    public RepetitionSubmitException(Object... obj) {
        super(ERROR_90100, obj);
    }
}
