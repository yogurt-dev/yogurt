package com.github.jyoghurt.core.exception;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: jtwu Date: 12-12-5 Time: 上午10:46 Dao层异常
 */
public class DaoException extends BaseException {
    private static final long serialVersionUID = 7799029148591208607L;

    @Override
    ExceptionBody[] getErrors() {
        return new ExceptionBody[0];
    }

    public DaoException(ExceptionBody exceptionBody) {
        super(exceptionBody);
    }

    public DaoException() {
        super();
    }

    public DaoException(String message) {
        super(message);
    }

    public DaoException(Throwable cause) {
        super(StringUtils.EMPTY, cause);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }


}
