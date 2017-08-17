package com.github.jyoghurt.common.msgcen.exception.enums;

/**
 * user:zjl
 * date: 2016/11/21.
 */
public enum MsgExceptionEnum {
    ERROR_9001("用户今日接收本短信次数已超过最大次数限制"),
    ERROR_9002("短信验证码发送过于频繁"),
    ERROR_9003("批量发送短信最多不超过100条"),
    ERROR_9999("消息发送异常");
    private String message;

    MsgExceptionEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
