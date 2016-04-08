package com.github.jyoghurt.core.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: jtwu Date: 13-2-26 Time: 下午4:10 基础异常类，其他异常需继承此类
 */
public abstract class BaseException extends Exception {
    private static final long serialVersionUID = 8686960428281101225L;
    /**
     * 异常码
     */
    private String errorCode;

    public BaseException(String refBizId, String logContent, Exception e) {
        super();
    }

    public BaseException(ExceptionBody exceptionBody) {
        super(exceptionBody.getMessage());
        this.errorCode = exceptionBody.getCode();
    }

    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    abstract ExceptionBody[] getErrors();
}
