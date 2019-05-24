package com.github.yogurt.core.exception;

import org.apache.commons.beanutils.PropertyUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * @author jtwu
 * @date 2016/4/8
 * 自定义异常体
 */
public class ExceptionBody implements Serializable {
	public static final String MESSAGE = "errorMessage";
	private String errorCode;
	private String errorMessage;

	public ExceptionBody(String code, String errorMessage) {
		this.errorCode = code;
		this.errorMessage = errorMessage;
	}

	public ExceptionBody(Enum errorEnum) {
		this.errorCode = errorEnum.name();
		try {
			this.errorMessage = PropertyUtils.getProperty(errorEnum, MESSAGE).toString();
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new BaseErrorException(errorMessage, e);
		}
	}

	public String getErrorCode() {
		return errorCode;
	}

	public ExceptionBody setErrorCode(String errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public ExceptionBody setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}


	@Override
	public String toString() {
		return "errorCode:" + errorCode + " errorMessage:" + errorMessage;
	}
}
