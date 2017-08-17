package com.github.jyoghurt.security.securityUserT.domain;

/**
 * Created by Administrator on 2015/9/1.
 */
public class SecurityUnitV {
    //单位ID
    private String unitId;
    //单位名称
    private String unitName;
    //单位类型
    private String unitType;
    //对应的用户
    private String userId;
    //对应的角色
    private String roleId;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }


}
