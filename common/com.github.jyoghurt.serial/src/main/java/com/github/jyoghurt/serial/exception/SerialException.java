package com.github.jyoghurt.serial.exception;

import com.github.jyoghurt.core.exception.BaseErrorException;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.community.base.serial.exception
 * @Description: 异常定义
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-03-15 14:30
 */
public class SerialException extends BaseErrorException {


    private static final long serialVersionUID = -1977500023878783626L;
    private String errorCode;

    public SerialException(String refBizId, String logContent, Exception e) {
        super(refBizId, logContent, e);
    }

    public SerialException() {
        super();
    }

    public SerialException(String errorCode, String errorMsg) {
        super("业务主键获取异常，错误代码：" + errorCode + " 错误信息：" + errorMsg);
        this.errorCode = errorCode;
    }

    public SerialException(String message) {
        super(message);
    }

//    public SerialException(Throwable cause) {
//        super(cause);
//    }

    public SerialException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
