package com.github.yogurt.core.exception;

/**
 * redis服务器异常
 */
public class RedisException extends BaseErrorException {

    public RedisException() {
        super(CoreExceptionEnum.ERROR_2001.getMessage());
    }

    public RedisException(Exception e) {
        super(CoreExceptionEnum.ERROR_2001.getMessage(), e);
    }
}
