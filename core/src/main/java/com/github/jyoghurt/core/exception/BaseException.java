package com.github.jyoghurt.core.exception;


/**
 * Created with IntelliJ IDEA. User: jtwu Date: 13-2-26 Time: 下午4:10 基础异常类，其他异常需继承此类
 */
public class BaseException extends Exception {
	private static final long serialVersionUID = 8686960428281101225L;

    public BaseException(String refBizId, String logContent, Exception e) {
        super();
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


}
