package com.github.yogurt.core.exception;

import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author jtwu
 * @date 2016/4/8
 * 自定义异常体
 */
public class ExceptionBody {
    public static String MESSAGE = "message";
    private String code;
    private String message;

    public ExceptionBody(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ExceptionBody(Enum errorEnum) {
        this.code = errorEnum.name();
        try {
            this.message = PropertyUtils.getProperty(errorEnum, MESSAGE).toString();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new BaseErrorException(message, e);
        }
    }

    public String getCode() {
        return code;
    }

    public ExceptionBody setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ExceptionBody setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "errorCode:" + code + " errorMessage:" + message;
    }
}
