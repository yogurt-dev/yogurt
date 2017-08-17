package com.github.jyoghurt.weChat.domain;

import com.github.jyoghurt.core.domain.BaseEntity;


public class WeChatMediaT extends BaseEntity{
	
	/**
	 * 主键
	 */
	private String id;
	/** 
	 * 文件保存路径  
	 */
	private String filePath;
	/** 
	 * 微信媒体Id  
	 */
	private String thumbMediaId;
	
	public String getId() {
	    return this.id;
	}
	
	public WeChatMediaT setId(String id) {
		this.id = id;
		return this;
	}
	
	public String getFilePath() {
	    return this.filePath;
	}
	
	public WeChatMediaT setFilePath(String filePath) {
		this.filePath = filePath;
		return this;
	}
	
	public String getThumbMediaId() {
	    return this.thumbMediaId;
	}
	
	public WeChatMediaT setThumbMediaId(String thumbMediaId) {
		this.thumbMediaId = thumbMediaId;
		return this;
	}
}
