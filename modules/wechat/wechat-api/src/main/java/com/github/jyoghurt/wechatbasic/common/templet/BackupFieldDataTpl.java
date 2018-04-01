package com.github.jyoghurt.wechatbasic.common.templet;

/**
 * user:dell
 * data:2016/5/10.
 */
public class BackupFieldDataTpl {
    private String value;
    private String color = "#173177";

    public String getColor() {
        return color;
    }

    public BackupFieldDataTpl setColor(String color) {
        this.color = color;
        return this;
    }

    public String getValue() {
        return value;
    }

    public BackupFieldDataTpl setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "BackupFieldDataTpl{" +
                "value='" + value + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
