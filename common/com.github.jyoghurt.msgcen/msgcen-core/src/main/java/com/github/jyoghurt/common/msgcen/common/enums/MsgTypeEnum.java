package com.github.jyoghurt.common.msgcen.common.enums;

/**
 * user:zjl
 * date: 2016/11/16.
 */
public enum MsgTypeEnum {
    WECHAT_TMPL("微信模板消息"),//微信模板消息
    EMAIL("EMAIL"),//
    SMS("短信消息");//
    private String typeValue;

    MsgTypeEnum(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }
}
