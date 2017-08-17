package com.github.jyoghurt.common.msgcen.common.enums;

/**
 * user:zjl
 * date: 2016/11/21.
 */
public enum MsgSendStatusEnum {
    NOTSEND("未发送"),
    SUCCESS("发送成功"),
    FAIL("发送失败");
    private String statusValue;

    MsgSendStatusEnum(String statusValue) {
        this.statusValue = statusValue;
    }

    public String getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(String statusValue) {
        this.statusValue = statusValue;
    }
}
