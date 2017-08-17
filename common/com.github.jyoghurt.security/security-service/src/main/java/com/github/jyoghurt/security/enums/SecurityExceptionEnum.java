package com.github.jyoghurt.security.enums;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.security.enums
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-12-08 10:25
 */
public enum SecurityExceptionEnum {
    ERROR_90801("账号或密码错误，无法登录"),
    ERROR_90802("用户未配置邮箱，请配置邮箱"),
    ERROR_90803("该微信用户未与系统账号绑定，或已绑定账号被其他微信用户绑定。请重新绑定"),
    ERROR_90804("会话已超时，请重新登录后再进行微信绑定"),
    ERROR_90805("非该机构下用户，无法进行创建操作"),
    ERROR_90806("账号或密码错误"),
    ERROR_90807("此功能正式环境可用");
    private String message;

    SecurityExceptionEnum(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public SecurityExceptionEnum setMessage(String message) {
        this.message = message;
        return this;
    }

}
