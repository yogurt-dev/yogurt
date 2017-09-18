package com.github.jyoghurt.serial.enums;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.community.base.serial.domain
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-03-24 20:58
 */
public enum SerialType {

    NORMAL("默认","NORMAL"),//所有模块公用流水
    ADVANCED("高级","ADVANCED"),//分模块生成流水，生成的格式 模块名+年月日+流水号，隔天从0开始
    NORMAL_MODULE("分模块生成数字型","NORMAL_MODULE");//分模块生成流水，纯数字类型序列号，需要到SerialInfoT表中，配置基础数据  module = 模块  type =
    // NORMAL_MODULE
    // serialNo 配置一个初始值，如 120000000


    private String name;
    private String code;

    SerialType(String name, String code) {
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
