package com.github.jyoghurt.security.securityUserT.enums;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.security.securityUserT.enums
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-07-19 09:54
 */
public enum ResourceType {
    STORE("store","门店"),
    STOCK("stock","仓库"),
    CITY("City","城市资源"),
    UNIT("unit","机构"),
    ALL("all","所有资源");

    ResourceType(String stateCode, String stateName) {
        this.stateCode = stateCode;
        this.stateName = stateName;
    }

    private String stateCode;
    private String stateName;

    public String getStateName() {
        return stateName;
    }

    public String getStateCode() {
        return stateCode;
    }
}
