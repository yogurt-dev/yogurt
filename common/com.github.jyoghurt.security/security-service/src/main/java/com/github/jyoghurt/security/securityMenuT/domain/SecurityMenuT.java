package com.github.jyoghurt.security.securityMenuT.domain;

import com.github.jyoghurt.core.domain.BaseEntity;

import javax.persistence.Transient;



@javax.persistence.Table(name = "SecurityMenuT")
public class SecurityMenuT extends BaseEntity {

    /**
     * 菜单ID
     */
    @javax.persistence.Id
    private String menuId;
    /**
     * 父菜单ID
     */
    private String parentId;
    /**
     * 菜单名称
     */
    private String menuName;
    /**
     * 菜单链接
     */
    private String menuUrl;
    /**
     * 图标
     */
    private String icon;

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    @Transient
    private String menuType;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
     * 所属角色
     */
    @Transient
    private String roleId;

    public String getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(String isLeaf) {
        this.isLeaf = isLeaf;
    }

    /**
     * 是否为叶子节点
     */
    private String isLeaf;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * 父节点名称
     */
    @Transient
    private String parentName;

    /**
     * 是否是待办任务
     */
    private String isGtask;

    /**
     * 待办详情跳转链接
     */
    private String gTaskUrl;

    /**
     * 待办类型
     */
    private String gTaskType;


    /**
     * 排序ID
     */


    private Integer sortId;

    public String getMenuId() {
        return this.menuId;
    }

    public SecurityMenuT setMenuId(String menuId) {
        this.menuId = menuId;
        return this;
    }

    public String getParentId() {
        return this.parentId;
    }

    public SecurityMenuT setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getMenuName() {
        return this.menuName;
    }

    public SecurityMenuT setMenuName(String menuName) {
        this.menuName = menuName;
        return this;
    }

    public String getMenuUrl() {
        return this.menuUrl;
    }

    public SecurityMenuT setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
        return this;
    }

    public String getIcon() {
        return this.icon;
    }

    public SecurityMenuT setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public Integer getSortId() {
        return this.sortId;
    }

    public SecurityMenuT setSortId(Integer sortId) {
        this.sortId = sortId;
        return this;
    }

    public String getIsGtask() {
        return isGtask;
    }

    public void setIsGtask(String isGtask) {
        this.isGtask = isGtask;
    }

    public String getgTaskType() {
        return gTaskType;
    }

    public void setgTaskType(String gTaskType) {
        this.gTaskType = gTaskType;
    }

    public String getgTaskUrl() {
        return gTaskUrl;
    }

    public void setgTaskUrl(String gTaskUrl) {
        this.gTaskUrl = gTaskUrl;
    }
}
