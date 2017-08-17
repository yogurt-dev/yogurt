package com.github.jyoghurt.security.securityUserResourceR.domain;

import com.github.jyoghurt.core.domain.BaseEntity;


@javax.persistence.Table(name = "SecurityUserResourceR")
public class SecurityUserResourceR extends BaseEntity<SecurityUserResourceR>{
	
	/** 
	 * 用户资源关系ID  
	 */
	@javax.persistence.Id
	private String userResourceRId;
	/** 
	 * 用户ID  
	 */
	private String userId;
	/** 
	 * 所属模块  
	 */
	private String belongModel;
	/** 
	 * 资源类型  
	 */
	private String resourceType;
	/** 
	 * 资源ID  
	 */
	private String resourceId;
	/** 
	 * 资源名称  
	 */
	private String resourceName;
	
	public String getUserResourceRId() {
	    return this.userResourceRId;
	}
	
	public SecurityUserResourceR setUserResourceRId(String userResourceRId) {
		this.userResourceRId = userResourceRId;
		return this;
	}
	
	public String getUserId() {
	    return this.userId;
	}
	
	public SecurityUserResourceR setUserId(String userId) {
		this.userId = userId;
		return this;
	}
	
	public String getBelongModel() {
	    return this.belongModel;
	}
	
	public SecurityUserResourceR setBelongModel(String belongModel) {
		this.belongModel = belongModel;
		return this;
	}
	
	public String getResourceType() {
	    return this.resourceType;
	}
	
	public SecurityUserResourceR setResourceType(String resourceType) {
		this.resourceType = resourceType;
		return this;
	}
	
	public String getResourceId() {
	    return this.resourceId;
	}
	
	public SecurityUserResourceR setResourceId(String resourceId) {
		this.resourceId = resourceId;
		return this;
	}
	
	public String getResourceName() {
	    return this.resourceName;
	}
	
	public SecurityUserResourceR setResourceName(String resourceName) {
		this.resourceName = resourceName;
		return this;
	}
}
