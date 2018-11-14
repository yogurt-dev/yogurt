package com.github.yogurt.core.exception;

/**
 * Created by IntelliJ IDEA.  Date: 12-12-5 Time: 上午10:46 Service层异常
 * @author jtwu
 */
public class ServiceException extends BaseErrorException {
    private static final long serialVersionUID = 7799029148591208607L;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }


}
