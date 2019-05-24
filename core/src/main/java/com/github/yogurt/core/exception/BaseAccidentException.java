package com.github.yogurt.core.exception;


import java.text.MessageFormat;

/**
 * Created with IntelliJ IDEA.  Date: 13-2-26 Time: 下午4:10 基础意外事件异常类，所有意外事件的基类
 * 以下我们对是“意外事件”和“错误”的理解
 * <p>
 * <p>
 * 异常条件	                        意外事件	                    错误
 * <p>
 * 认为是（Is considered to be）	    设计的一部分	                难以应付的意外
 * 预期发生（Is expected to happen）	有规律但很少发生	            从不
 * 谁来处理（Who cares about it）	    调用方法的上游代码	            需要修复此问题的人员
 * 实例（Examples）	                另一种返回模式	                编程缺陷，硬件故障，配置错误，文件丢失，服务器无法使用
 * 最佳映射（Best Mapping）	        已检查异常	                未检查异常
 *
 * @author jtwu
 */
public class BaseAccidentException extends Exception {
	private static final long serialVersionUID = 8686960428281101225L;
	private boolean logFlag = false;

	/**
	 * 自定义异常体
	 */
	private ExceptionBody exceptionBody;

	public BaseAccidentException(ExceptionBody exceptionBody, Object... objects) {
		super(MessageFormat.format(exceptionBody.getErrorMessage(), objects));
		this.exceptionBody = exceptionBody;
	}

	public BaseAccidentException(ExceptionBody exceptionBody, Throwable cause, Object... objects) {
		super(MessageFormat.format(exceptionBody.getErrorMessage(), objects), cause);
		this.exceptionBody = exceptionBody;
		if (!(cause instanceof BaseAccidentException) || ((BaseAccidentException) cause).logFlag) {
			logFlag = true;
		}
	}

	public BaseAccidentException(BaseAccidentException exception) {
		this.exceptionBody = exception.getExceptionBody();
	}


	public ExceptionBody getExceptionBody() {
		return exceptionBody;
	}

	public boolean getLogFlag() {
		return logFlag;
	}

}
