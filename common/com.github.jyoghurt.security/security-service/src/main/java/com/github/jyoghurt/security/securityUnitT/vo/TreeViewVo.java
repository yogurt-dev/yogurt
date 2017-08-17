package com.github.jyoghurt.security.securityUnitT.vo;

import java.util.List;

/**
 * Created by think on 2016/8/23.
 */
public class TreeViewVo {
    private String unitId;
    private String unitName;
    private String parentId;
    private String compType;
    private Integer sortId;
    private List<TreeViewVo> underUsers;

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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCompType() {
        return compType;
    }

    public void setCompType(String compType) {
        this.compType = compType;
    }

    public Integer getSortId() {
        return sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }


    public List<TreeViewVo> getUnderUsers() {
        return underUsers;
    }

    public void setUnderUsers(List<TreeViewVo> underUsers) {
        this.underUsers = underUsers;
    }
}
