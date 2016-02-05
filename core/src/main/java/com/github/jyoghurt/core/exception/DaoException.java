package com.github.jyoghurt.core.exception;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA. User: jtwu Date: 12-12-5 Time: 上午10:46 Dao层异常
 */
public class DaoException extends BaseException {
	private static final long serialVersionUID = 7799029148591208607L;
    public Logger logger = LoggerFactory.getLogger(this.getClass());

	public DaoException() {
        super();
    }

    public DaoException(String message) {
        super(message);
        logger.error(message);
    }

    public DaoException(Throwable cause) {
        super(StringUtils.EMPTY,cause);
        logger.error(null,cause);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
        logger.error(message,cause);
    }
}
