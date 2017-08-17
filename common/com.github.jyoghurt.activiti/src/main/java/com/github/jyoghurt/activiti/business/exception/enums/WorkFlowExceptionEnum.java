package com.github.jyoghurt.activiti.business.exception.enums;

/**
 * user:zjl
 * date: 2016/11/4.
 */
public enum WorkFlowExceptionEnum {

    ERROR_90100("该待办已被提交")


    ;
    private String message;

    WorkFlowExceptionEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
