package com.github.jyoghurt.security.securitySyslogT.enums;

/**
 * Created by jtwu on 2015/9/16.
 * 客户端系统类型
 */
public enum SystemType {
    WIN10("Windows NT 6.4,Windows NT 10.0"),
    WIN8_1("Windows NT 6.3"),
    WIN8("Windows NT 6.2"),
    WIN7("Windows NT 6.1"),
    WIN_VISTA("Windows NT 6.0"),
    WIN2003("Windows NT 5.2"),
    WIN_XP("Windows NT 5.1"),
    WIN2000("Windows NT 5.0"),
    ANDROID("android"),
    IOS("ios,iphone"),
    OTHER("other");

    private String value;

    SystemType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
