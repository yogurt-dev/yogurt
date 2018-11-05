package com.github.yogurt.core.exception;

/**
 * Created by IntelliJ IDEA. Date: 12-12-5 Time: 上午10:46 工具类异常
 * @author jtwu
 */
public class UtilException extends BaseErrorException{
	private static final long serialVersionUID = 2244979209286032238L;

	public UtilException() {
        super();
    }

    public UtilException(String message) {
        super(message);
    }

    public UtilException(Throwable cause) {
        super(cause.getMessage(),cause);
    }

    public UtilException(String message, Throwable cause) {
        super(message, cause);
    }

}
