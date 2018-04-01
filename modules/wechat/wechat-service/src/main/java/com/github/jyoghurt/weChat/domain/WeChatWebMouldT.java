package com.github.jyoghurt.weChat.domain;

import com.github.jyoghurt.core.domain.BaseEntity;


@javax.persistence.Table(name = "WeChatWebMouldT")
public class WeChatWebMouldT extends BaseEntity{
	
	/** 
	 * 主键  
	 */
	@javax.persistence.Id
	private String mouldId;
	/** 
	 * 预览图URL  
	 */
	private String mouldImg;
	/** 
	 * 模板url  
	 */
	private String mouldUrl;
	/**
	 * 主页模板
	 */
	private String mainMoban;
	/**
	 * 二级页集合模板
	 */
	private String secondMoban;
	/**
	 * 文本模板
	 */
	private String textMoban;
	/** 
	 * 一级菜单个数  
	 */
	private String oneLevel;
	/** 
	 * 二级菜单个数  
	 */
	private String twoLevel;
	
	public String getMouldId() {
	    return this.mouldId;
	}
	
	public WeChatWebMouldT setMouldId(String mouldId) {
		this.mouldId = mouldId;
		return this;
	}
	
	public String getMouldImg() {
	    return this.mouldImg;
	}
	
	public WeChatWebMouldT setMouldImg(String mouldImg) {
		this.mouldImg = mouldImg;
		return this;
	}
	
	public String getMouldUrl() {
	    return this.mouldUrl;
	}
	
	public WeChatWebMouldT setMouldUrl(String mouldUrl) {
		this.mouldUrl = mouldUrl;
		return this;
	}
	
	public String getOneLevel() {
	    return this.oneLevel;
	}
	
	public WeChatWebMouldT setOneLevel(String oneLevel) {
		this.oneLevel = oneLevel;
		return this;
	}
	
	public String getTwoLevel() {
	    return this.twoLevel;
	}
	
	public WeChatWebMouldT setTwoLevel(String twoLevel) {
		this.twoLevel = twoLevel;
		return this;
	}

	public String getMainMoban() {
		return mainMoban;
	}

	public void setMainMoban(String mainMoban) {
		this.mainMoban = mainMoban;
	}

	public String getSecondMoban() {
		return secondMoban;
	}

	public void setSecondMoban(String secondMoban) {
		this.secondMoban = secondMoban;
	}

	public String getTextMoban() {
		return textMoban;
	}

	public void setTextMoban(String textMoban) {
		this.textMoban = textMoban;
	}
}
