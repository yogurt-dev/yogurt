package com.github.jyoghurt.annex.exception;

import com.github.jyoghurt.core.exception.BaseAccidentException;

/**
 * Created by dell on 2016/2/1.
 */
public class AnnexException extends BaseAccidentException{
    private static final long serialVersionUID = 2657618496024743577L;
    private String errorCode;
    public AnnexException() {
    }

    public AnnexException(String message) {
        super(message);
    }
    public AnnexException(String errorCode,String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AnnexException(Throwable cause) {
        super("", cause);
    }

    public AnnexException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }
}
