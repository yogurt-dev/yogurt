package com.github.jyoghurt.security.onlineManage.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class SecurityOnlineT {
	
	/** 
	 *   
	 */
	@javax.persistence.Id
	private String uuid;
	/** 
	 *   
	 */
	private String uri;
	/** 
	 *   
	 */
	private String sessionId;
	/** 
	 *   
	 */
	private String localAddress;
	/** 
	 *   
	 */
	private String remoteAddress;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 登録用戶
	 */
	private String userName;



	private String bsflag;

	public String getBsflag() {
		return bsflag;
	}

	public void setBsflag(String bsflag) {
		this.bsflag = bsflag;
	}

	public String getUuid() {
	    return this.uuid;
	}
	
	public SecurityOnlineT setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}
	
	public String getUri() {
	    return this.uri;
	}
	
	public SecurityOnlineT setUri(String uri) {
		this.uri = uri;
		return this;
	}
	
	public String getSessionId() {
	    return this.sessionId;
	}
	
	public SecurityOnlineT setSessionId(String sessionId) {
		this.sessionId = sessionId;
		return this;
	}
	
	public String getLocalAddress() {
	    return this.localAddress;
	}
	
	public SecurityOnlineT setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
		return this;
	}
	
	public String getRemoteAddress() {
	    return this.remoteAddress;
	}
	
	public SecurityOnlineT setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
		return this;
	}
}
