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
	private static final String MESSAGE = "errorMessage";
	/**
	 * 异常编码
	 */
	private String errorCode;
	/**
	 * 异常描述信息
	 */
	private String errorMessage;
	/**
	 * 异常内容体，只有在返回具体异常信息时使用
	 */
	private Serializable errorBody;

	public ExceptionBody(String code, String errorMessage) {
		this.errorCode = code;
		this.errorMessage = errorMessage;
	}
	public ExceptionBody(String code, String errorMessage,String errorBody) {
		this.errorCode = code;
		this.errorMessage = errorMessage;
		this.errorBody = errorBody;
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

	public Serializable getErrorBody() {
		return errorBody;
	}

	public void setErrorBody(Serializable errorBody) {
		this.errorBody = errorBody;
	}

	@Override
	public String toString() {
		return "errorCode:" + errorCode + " errorMessage:" + errorMessage;
	}
}
