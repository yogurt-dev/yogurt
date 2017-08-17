package com.github.jyoghurt.security.enums;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.community.base.serial
 * @Description: 列举了使用业务主键的业务模块列表
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-03-15 15:07
 */
public enum Module {

    WASH("洗衣模块", "WASH"),
    MEMBER("订单模块", "MEMBER"),
    SHOP("商店模块","SHOP");

    private String name;
    private String code;

    Module(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
