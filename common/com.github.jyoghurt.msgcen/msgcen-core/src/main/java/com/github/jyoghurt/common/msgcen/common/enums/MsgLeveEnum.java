package com.github.jyoghurt.common.msgcen.common.enums;

/**
 * user:zjl
 * date: 2016/11/21.
 */
public enum MsgLeveEnum {
    COMMON("普通"),
    URGENCY("紧急");
    private String levelValue;

    MsgLeveEnum(String levelValue) {
        this.levelValue = levelValue;
    }

    public String getLevelValue() {
        return levelValue;
    }

    public void setLevelValue(String levelValue) {
        this.levelValue = levelValue;
    }
}
