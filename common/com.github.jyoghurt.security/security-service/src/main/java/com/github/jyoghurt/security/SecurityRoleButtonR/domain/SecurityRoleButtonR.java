package com.github.jyoghurt.security.SecurityRoleButtonR.domain;

import com.github.jyoghurt.core.domain.BaseEntity;


@javax.persistence.Table(name = "SecurityRoleButtonR")
public class SecurityRoleButtonR extends BaseEntity{
	
	/** 
	 * 主键id  
	 */
	@javax.persistence.Id
	private String mrbId;
	/** 
	 * 角色id  
	 */
	private String roleId;
	/** 
	 * 按钮id  
	 */
	private String buttonId;
	
	public String getMrbId() {
	    return this.mrbId;
	}
	
	public SecurityRoleButtonR setMrbId(String mrbId) {
		this.mrbId = mrbId;
		return this;
	}

	
	public String getRoleId() {
	    return this.roleId;
	}
	
	public SecurityRoleButtonR setRoleId(String roleId) {
		this.roleId = roleId;
		return this;
	}

	public String getButtonId() {
	    return this.buttonId;
	}

	public SecurityRoleButtonR setButtonId(String buttonId) {
		this.buttonId = buttonId;
		return this;
	}
}
