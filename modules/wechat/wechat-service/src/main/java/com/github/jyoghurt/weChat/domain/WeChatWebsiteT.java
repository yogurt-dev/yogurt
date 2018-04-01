package com.github.jyoghurt.weChat.domain;

import com.github.jyoghurt.core.domain.BaseEntity;

import javax.persistence.Transient;
import java.util.List;


@javax.persistence.Table(name = "WeChatWebsiteT")
public class WeChatWebsiteT extends BaseEntity{
	
	/** 
	 * 主键  
	 */
	@javax.persistence.Id
	private String webId;
	/** 
	 * 网站名字  
	 */
	private String webName;
	/** 
	 * 图片地址  
	 */
	private String webImg;
	/** 
	 * 模板id  
	 */
	private String mouldId;
	/**
	 * appid
	 */
	private String appId;
	/**
	 * 是否发布1为发布0未发布
	 */
	private String state;
	/**
	 * 菜单
	 */
	@Transient
	private List<WeChatWebsiteMenuT> list;
	public String getWebId() {
	    return this.webId;
	}

	public WeChatWebsiteT setWebId(String webId) {
		this.webId = webId;
		return this;
	}

	public String getWebName() {
	    return this.webName;
	}

	public WeChatWebsiteT setWebName(String webName) {
		this.webName = webName;
		return this;
	}

	public String getWebImg() {
	    return this.webImg;
	}

	public WeChatWebsiteT setWebImg(String webImg) {
		this.webImg = webImg;
		return this;
	}

	public String getMouldId() {
	    return this.mouldId;
	}

	public WeChatWebsiteT setMouldId(String mouldId) {
		this.mouldId = mouldId;
		return this;
	}

	public List<WeChatWebsiteMenuT> getList() {
		return list;
	}

	public void setList(List<WeChatWebsiteMenuT> list) {
		this.list = list;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
