package com.github.jyoghurt.core.exception;

/**
 * Created by jtwu on 2016/4/8.
 * 自定义异常体
 */
public class ExceptionBody {
    private String code;
    private String message;

    public ExceptionBody(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "errorCode:"+code+" errorMessage:"+message;
    }
}
