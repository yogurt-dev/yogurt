package com.github.jyoghurt.sms;

/**
 * @date: 2016-11-02 11:52
 */
public enum SmsExceptionEnums {
    ERROR_91100("发送短信失败，错误码： {0} 错误信息：{1}");

    SmsExceptionEnums(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public SmsExceptionEnums setMessage(String message) {
        this.message = message;
        return this;
    }

    private String message;

}
