package com.github.jyoghurt.security.SecurityButtonT.domain;

import com.github.jyoghurt.core.domain.BaseEntity;


@javax.persistence.Table(name = "SecurityButtonT")
public class SecurityButtonT extends BaseEntity{
	
	/** 
	 * 按钮名称  
	 */
	@javax.persistence.Id
	private String buttonId;
	/** 
	 * 按钮名称  
	 */
	private String buttonName;
	/**
	 * 按钮名称
	 */
	private String buttonCode;
	/** 
	 * 功能id  
	 */
	private String menuId;
	
	public String getButtonId() {
	    return this.buttonId;
	}
	
	public SecurityButtonT setButtonId(String buttonId) {
		this.buttonId = buttonId;
		return this;
	}
	
	public String getButtonName() {
	    return this.buttonName;
	}
	
	public SecurityButtonT setButtonName(String buttonName) {
		this.buttonName = buttonName;
		return this;
	}
	
	public String getMenuId() {
	    return this.menuId;
	}
	
	public SecurityButtonT setMenuId(String menuId) {
		this.menuId = menuId;
		return this;
	}

	public String getButtonCode() {
		return buttonCode;
	}

	public void setButtonCode(String buttonCode) {
		this.buttonCode = buttonCode;
	}
}
