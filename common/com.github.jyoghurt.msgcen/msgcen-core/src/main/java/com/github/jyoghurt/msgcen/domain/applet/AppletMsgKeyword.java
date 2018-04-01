package com.github.jyoghurt.msgcen.domain.applet;

public class AppletMsgKeyword {
    private String value;
    private String color;

    public AppletMsgKeyword(String value) {
        this.value = value;
        this.color = "#173177";
    }

    public AppletMsgKeyword(String value, String color) {
        this.value = value;
        this.color = color;
    }

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
}
