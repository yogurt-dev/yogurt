package com.github.jyoghurt.core.exception;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by IntelliJ IDEA. User: jtwu Date: 12-12-5 Time: 上午10:46 Service层异常
 */
public class ServiceException extends BaseException {
	private static final long serialVersionUID = 2657618496024743577L;

	public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(StringUtils.EMPTY,cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
