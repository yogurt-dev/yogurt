package com.github.jyoghurt.drools.service.knowledge.exception.enums;

/**
 * user:zjl
 * date: 2016/12/29.
 */
public enum DroolsExcepitonEnum {
    ERROR_91500("规则输入错误");

    DroolsExcepitonEnum(String message) {
        this.message = message;
    }

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
