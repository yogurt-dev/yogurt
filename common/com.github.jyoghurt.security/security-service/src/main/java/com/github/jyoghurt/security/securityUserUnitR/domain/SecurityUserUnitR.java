package com.github.jyoghurt.security.securityUserUnitR.domain;

import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.core.domain.BaseEntity;

import javax.persistence.Transient;
import java.util.List;


@javax.persistence.Table(name = "SecurityUserUnitR")
public class SecurityUserUnitR extends BaseEntity<SecurityUserUnitR>{
	
	/** 
	 * 关系ID  
	 */
	@javax.persistence.Id
	private String userUnitId;
	/** 
	 * 用户ID  
	 */
	private String userIdR;
	/** 
	 * 组织机构ID  
	 */
	private String unitIdR;
	/** 
	 * 用户名  
	 */
	private String userNameR;
	/** 
	 * 组织机构名称  
	 */
	private String unitNameR;
	/** 
	 * 上级单位ID  
	 */
	private String parentUnitId;
	/** 
	 * 上级单位名称  
	 */
	private String parentUnitName;

	/**
	 * 机构类型
	 */
	@Transient
	private String compType;

	/**
	 * 下属人员
	 */
	@Transient
	private List<SecurityUserT> underUsers;

	public String getUserUnitId() {
	    return this.userUnitId;
	}
	
	public SecurityUserUnitR setUserUnitId(String userUnitId) {
		this.userUnitId = userUnitId;
		return this;
	}
	
	public String getUserIdR() {
	    return this.userIdR;
	}
	
	public SecurityUserUnitR setUserIdR(String userIdR) {
		this.userIdR = userIdR;
		return this;
	}
	
	public String getUnitIdR() {
	    return this.unitIdR;
	}
	
	public SecurityUserUnitR setUnitIdR(String unitIdR) {
		this.unitIdR = unitIdR;
		return this;
	}
	
	public String getUserNameR() {
	    return this.userNameR;
	}
	
	public SecurityUserUnitR setUserNameR(String userNameR) {
		this.userNameR = userNameR;
		return this;
	}
	
	public String getUnitNameR() {
	    return this.unitNameR;
	}
	
	public SecurityUserUnitR setUnitNameR(String unitNameR) {
		this.unitNameR = unitNameR;
		return this;
	}
	
	public String getParentUnitId() {
	    return this.parentUnitId;
	}
	
	public SecurityUserUnitR setParentUnitId(String parentUnitId) {
		this.parentUnitId = parentUnitId;
		return this;
	}
	
	public String getParentUnitName() {
	    return this.parentUnitName;
	}
	
	public SecurityUserUnitR setParentUnitName(String parentUnitName) {
		this.parentUnitName = parentUnitName;
		return this;
	}

	public List<SecurityUserT> getUnderUsers() {
		return underUsers;
	}

	public void setUnderUsers(List<SecurityUserT> underUsers) {
		this.underUsers = underUsers;
	}

	public String getCompType() {
		return compType;
	}

	public void setCompType(String compType) {
		this.compType = compType;
	}
}
