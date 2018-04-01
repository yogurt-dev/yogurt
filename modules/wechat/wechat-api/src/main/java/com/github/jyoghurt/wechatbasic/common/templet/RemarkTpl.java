package com.github.jyoghurt.wechatbasic.common.templet;

/**
 * user:dell
 * data:2016/5/10.
 */
public class RemarkTpl {
    private String value = "如有问题请致电400-015-5567或直接在微信留言，驴鱼将第一时间为您服务！";
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
        return "RemarkTpl{" +
                "value='" + value + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
