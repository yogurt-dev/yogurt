package com.github.jyoghurt.dataDict.enums;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.motorInsurance.pub.utils
 * @Description: 枚举类，描述静态常量
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2015-09-02 15:00
 */
public enum SysVarEnum {
    YES_STATICVAR("1", "是"),
    NO_STATICVAR("0", "否");


    private String code;
    private String message;

    SysVarEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
