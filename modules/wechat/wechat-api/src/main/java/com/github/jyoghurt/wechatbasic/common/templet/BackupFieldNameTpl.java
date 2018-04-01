package com.github.jyoghurt.wechatbasic.common.templet;

/**
 * user:dell
 * data:2016/5/10.
 */
public class BackupFieldNameTpl {
    private String value;
    private String color = "#173177";

    public String getValue() {

        return value;
    }

    public BackupFieldNameTpl setValue(String value) {
        this.value = value;
        return this;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "BackupFieldNameTpl{" +
                "value='" + value + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
