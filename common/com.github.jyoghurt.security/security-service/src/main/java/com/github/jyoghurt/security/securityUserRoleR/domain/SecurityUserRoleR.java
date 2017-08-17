package com.github.jyoghurt.security.securityUserRoleR.domain;

import com.github.jyoghurt.core.domain.BaseEntity;



@javax.persistence.Table(name = "SecurityUserRoleR")
public class SecurityUserRoleR extends BaseEntity {

    /**
     * 关系ID
     */
    @javax.persistence.Id
    private String relId;

    public String getRelId() {
        return relId;
    }

    public void setRelId(String relId) {
        this.relId = relId;
    }


    private String userId;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    private String roleId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
