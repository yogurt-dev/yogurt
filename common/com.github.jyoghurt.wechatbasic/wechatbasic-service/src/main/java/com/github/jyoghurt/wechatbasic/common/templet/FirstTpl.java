package com.github.jyoghurt.wechatbasic.common.templet;

/**
 * user:dell
 * data:2016/5/10.
 */
public class FirstTpl {
    private String value;
    private String color = "#173177";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "FirstTpl{" +
                "value='" + value + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
