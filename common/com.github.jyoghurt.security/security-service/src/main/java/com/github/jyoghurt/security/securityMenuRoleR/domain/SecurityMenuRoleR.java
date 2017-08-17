package com.github.jyoghurt.security.securityMenuRoleR.domain;

import com.github.jyoghurt.core.domain.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@javax.persistence.Table(name = "SecurityMenuRoleR")
public class SecurityMenuRoleR extends BaseEntity{
	
	/** 
	 *  关系ID  
	*/
	@javax.persistence.Id
	private String relId;
	/** 
	 *  角色ID  
	*/
	private String roleId;


	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	/**
	 *  菜单ID  
	*/
	private String menuId;
	public String getRelId() {
	    return this.relId;
	}
	public SecurityMenuRoleR setRelId(String relId) {
		this.relId=relId;
		return this;
	}
	public String getRoleId() {
	    return this.roleId;
	}
	public SecurityMenuRoleR setRoleId(String roleId) {
		this.roleId=roleId;
		return this;
	}

}
