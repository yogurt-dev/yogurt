package com.github.jyoghurt.security.securityRoleT.domain;

import com.github.jyoghurt.core.domain.BaseEntity;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;

import javax.persistence.Transient;
import java.util.List;


//@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@javax.persistence.Table(name = "SecurityRoleT")
public class SecurityRoleT extends BaseEntity {

    /**
     * 角色ID
     */
    @javax.persistence.Id
    private String roleId;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 所属组织机构
     */
    private String belongUnit;

    public String getBelongUnitName() {
        return belongUnitName;
    }

    public void setBelongUnitName(String belongUnitName) {
        this.belongUnitName = belongUnitName;
    }

    @Transient
    private String belongUnitName;

    public String getBtrow() {
        return btrow;
    }

    public void setBtrow(String btrow) {
        this.btrow = btrow;
    }

    @Transient
    private String btrow;

    /**
     * 角色类型
     */
    private String roleType;


    public String getMenuTs() {
        return menuTs;
    }

    public void setMenuTs(String menuTs) {
        this.menuTs = menuTs;
    }

    /**
     * 包含的菜单
     */
    @Transient
    private String menuTs;

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public List<SecurityUserT> getUsers() {
        return users;
    }

    public void setUsers(List<SecurityUserT> users) {
        this.users = users;
    }

    @Transient
    private List<SecurityUserT> users;

    public String getRoleId() {
        return this.roleId;
    }

    public SecurityRoleT setRoleId(String roleId) {
        this.roleId = roleId;
        return this;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public SecurityRoleT setRoleName(String roleName) {
        this.roleName = roleName;
        return this;
    }

    public String getBelongUnit() {
        return this.belongUnit;
    }

    public SecurityRoleT setBelongUnit(String belongUnit) {
        this.belongUnit = belongUnit;
        return this;
    }
}
