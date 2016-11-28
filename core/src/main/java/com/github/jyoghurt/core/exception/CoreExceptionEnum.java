package com.github.jyoghurt.core.exception;

public enum CoreExceptionEnum {

    ERROR_2001("缓存服务器异常！");

    private String message;

    CoreExceptionEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
