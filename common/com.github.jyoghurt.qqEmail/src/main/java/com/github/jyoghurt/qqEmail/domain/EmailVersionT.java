package com.github.jyoghurt.qqEmail.domain;

import com.github.jyoghurt.core.domain.BaseEntity;


@javax.persistence.Table(name = "EmailVersionT")
public class EmailVersionT extends BaseEntity {
	
	/** 
	 * 主键  
	 */
	@javax.persistence.Id
	private String id;
	/** 
	 * 上个版本  
	 */
	private String lastVersion;
	/** 
	 * 当前版本  
	 */
	private String currentVersion;
	/** 
	 *   
	 */
	private String clientId;
	
	public String getId() {
	    return this.id;
	}
	
	public EmailVersionT setId(String id) {
		this.id = id;
		return this;
	}
	
	public String getLastVersion() {
	    return this.lastVersion;
	}
	
	public EmailVersionT setLastVersion(String lastVersion) {
		this.lastVersion = lastVersion;
		return this;
	}
	
	public String getCurrentVersion() {
	    return this.currentVersion;
	}
	
	public EmailVersionT setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
		return this;
	}
	
	public String getClientId() {
	    return this.clientId;
	}
	
	public EmailVersionT setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}
}
