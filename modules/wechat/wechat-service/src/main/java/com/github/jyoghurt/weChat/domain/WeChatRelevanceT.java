package com.github.jyoghurt.weChat.domain;

import com.github.jyoghurt.core.domain.BaseEntity;


@javax.persistence.Table(name = "WeChatRelevanceT")
public class WeChatRelevanceT extends BaseEntity<WeChatRelevanceT>{
	
	/** 
	 * 主键  
	 */
	@javax.persistence.Id
	private String id;
	/** 
	 * appId  
	 */
	private String appId;
	/** 
	 * 微信公共号名称  
	 */
	private String appName;
	/** 
	 * appsecret  
	 */
	private String appsecret;
	/** 
	 * openId  
	 */
	private String openId;
	private String nickName;

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getId() {
	    return this.id;
	}
	
	public WeChatRelevanceT setId(String id) {
		this.id = id;
		return this;
	}
	
	public String getAppId() {
	    return this.appId;
	}
	
	public WeChatRelevanceT setAppId(String appId) {
		this.appId = appId;
		return this;
	}
	
	public String getAppName() {
	    return this.appName;
	}
	
	public WeChatRelevanceT setAppName(String appName) {
		this.appName = appName;
		return this;
	}
	
	public String getAppsecret() {
	    return this.appsecret;
	}
	
	public WeChatRelevanceT setAppsecret(String appsecret) {
		this.appsecret = appsecret;
		return this;
	}
	
	public String getOpenId() {
	    return this.openId;
	}
	
	public WeChatRelevanceT setOpenId(String openId) {
		this.openId = openId;
		return this;
	}
}
