package com.github.jyoghurt.sw.exception.enums;

public enum SwitchExceptionEnum {

    ERROR_631("网络繁忙！");

    private String message;

    SwitchExceptionEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
