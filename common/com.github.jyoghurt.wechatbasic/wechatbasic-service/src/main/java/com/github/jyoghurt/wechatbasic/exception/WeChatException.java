package com.github.jyoghurt.wechatbasic.exception;

/**
 * Created by zhangjl on 2015/11/11.
 */

import com.github.jyoghurt.core.exception.BaseErrorException;

public class WeChatException extends BaseErrorException {
    private static final long serialVersionUID = 2657618496024743577L;
    private String errorCode;
    public WeChatException() {
    }

    public WeChatException(String message) {
        super(message);
    }
    public WeChatException(String errorCode,String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public WeChatException(Throwable cause) {
        super("", cause);
    }

    public WeChatException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }
}


