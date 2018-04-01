package com.github.jyoghurt.drools.service.knowledge.exception;

import com.github.jyoghurt.core.exception.BaseAccidentException;
import com.github.jyoghurt.core.exception.ExceptionBody;
import com.github.jyoghurt.drools.service.knowledge.exception.enums.DroolsExcepitonEnum;

/**
 * user:zjl
 * date: 2016/12/29.
 */
public class DroolsRuleErrorException extends BaseAccidentException {

    private static final ExceptionBody ERROR_91500 = new ExceptionBody(DroolsExcepitonEnum.ERROR_91500);

    public DroolsRuleErrorException() {
        super(ERROR_91500);
    }

    public DroolsRuleErrorException(Throwable cause) {
        super(ERROR_91500, cause);
    }

}
