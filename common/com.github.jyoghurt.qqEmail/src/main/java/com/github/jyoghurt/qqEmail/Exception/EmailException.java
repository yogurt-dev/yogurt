package com.github.jyoghurt.qqEmail.Exception;

/**
 * Created by zhangjl on 2015/11/11.
 */

import com.github.jyoghurt.core.exception.BaseErrorException;

public class EmailException extends BaseErrorException {
    private static final long serialVersionUID = 2657618496024743577L;
    private String errorCode;
    public EmailException() {
    }

    public EmailException(String message) {
        super(message);
    }
    public EmailException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public EmailException(Throwable cause) {
        super("", cause);
    }

    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }
}


