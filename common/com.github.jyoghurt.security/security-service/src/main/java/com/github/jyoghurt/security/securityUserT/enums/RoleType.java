package com.github.jyoghurt.security.securityUserT.enums;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.security.securityUserT.enums
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-07-19 09:58
 */
public enum RoleType {
    DZ("e6ba28ee4f284455897802d2668246d4","店长"),
    DY("d12a54e629fa4358be6dae6ab016ca0f","店员");

    RoleType(String stateCode, String stateName) {
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
