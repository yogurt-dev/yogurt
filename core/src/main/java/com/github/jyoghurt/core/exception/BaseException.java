package com.github.jyoghurt.core.exception;


/**
 * Created with IntelliJ IDEA. User: jtwu Date: 13-2-26 Time: 下午4:10 基础异常类，其他异常需继承此类
 */
public abstract class BaseException extends RuntimeException {
    private static final long serialVersionUID = 8686960428281101225L;
    private boolean logFlag = false;
    /**
     * 异常码
     */
    private String errorCode;

    /**
     * 自定义异常体
     */
    private ExceptionBody exceptionBody;

    public BaseException(String refBizId, String logContent, Exception e) {
        super();
    }

    public BaseException(ExceptionBody exceptionBody) {
        super(exceptionBody.getMessage());
        this.exceptionBody = exceptionBody;
        this.errorCode = exceptionBody.getCode();
    }

    public BaseException(ExceptionBody exceptionBody, Throwable cause) {
        super(exceptionBody.getMessage(), cause);
        this.exceptionBody = exceptionBody;
        this.errorCode = exceptionBody.getCode();
        if (cause instanceof BaseException == false||(((BaseException)cause).logFlag = true)) {
            logFlag=true;
        }
    }

    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause.getMessage(), cause);
        if (cause instanceof BaseException == false) {
            logFlag=true;
        }
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        if (cause instanceof BaseException == false) {
            logFlag=true;
        }
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ExceptionBody getExceptionBody() {
        return exceptionBody;
    }

    public boolean getLogFlag() {
        return logFlag;
    }

}
