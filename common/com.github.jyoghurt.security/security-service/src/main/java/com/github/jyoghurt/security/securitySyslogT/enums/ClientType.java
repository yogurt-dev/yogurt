package com.github.jyoghurt.security.securitySyslogT.enums;

/**
 * Created by jtwu on 2015/9/16.
 * 客户端类型
 */
public enum ClientType {
    IE11("MSIE 11.0"),
    IE10("MSIE 10.0"),
    IE9("MSIE 9.0"),
    IE8("MSIE 8.0"),
    IE7("MSIE 7.0"),
    IE6("MSIE 6.0"),
    MAXTHON("Maxthon"),
    QQ("QQBrowser"),
    GREEN("GreenBrowser"),
    SE360("360SE"),
    FIREFOX("Firefox"),
    OPERA("Opera"),
    CHROME("Chrome"),
    SAFARI("Safari"),
    OTHER("其它");

    private String value;

    ClientType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
