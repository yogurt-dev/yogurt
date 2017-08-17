package com.github.jyoghurt.common.msgcen.common.enums;

/**
 * user:zjl
 * date: 2016/11/21.
 */
public enum MsgFailReasonEnum {
    CONNECTTIMEOUT("连接超时"),
    ORDER("其它");
    private String reasonValue;

    MsgFailReasonEnum(String reasonValue) {
        this.reasonValue = reasonValue;
    }

    public String getReasonValue() {
        return reasonValue;
    }

    public void setReasonValue(String reasonValue) {
        this.reasonValue = reasonValue;
    }
}
