package com.github.jyoghurt.activiti.business.exception;

import com.github.jyoghurt.core.exception.BaseAccidentException;
import com.github.jyoghurt.core.exception.ExceptionBody;

/**
 * Created by dell on 2016/1/8.
 */

public class WorkFlowException extends BaseAccidentException {
    private static final long serialVersionUID = 2657618496024743577L;
    private String errorCode;
    public WorkFlowException() {
    }

    public WorkFlowException(String message) {
        super(message);
    }
    public WorkFlowException(String errorCode,String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public WorkFlowException(Throwable cause) {
        super("", cause);
    }

    public WorkFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    protected ExceptionBody[] getErrors() {
        return new ExceptionBody[0];
    }
}