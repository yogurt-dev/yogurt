package com.github.jyoghurt.weChat.domain;

import com.github.jyoghurt.core.domain.BaseEntity;


@javax.persistence.Table(name = "WeChatWebsiteMenuT")
public class WeChatWebsiteMenuT extends BaseEntity{
	
	/** 
	 * 主键  
	 */
	@javax.persistence.Id
	private String menuId;
	/** 
	 *   
	 */
	private String webId;
	/** 
	 * 上层id  
	 */
	private String parentId;
	/** 
	 * 菜单名称  
	 */
	private String menuName;
	/** 
	 * 菜单图片  
	 */
	private String menuImg;
	/** 
	 * 菜单点击类型  
	 */
	private String clickType;
	/** 
	 * 链接地址url  
	 */
	private String url;
	/** 
	 * 概要  
	 */
	private String resume;
	/** 
	 * 内容  
	 */
	private String text;
	/**
	 * 排序
	 */
	private int sort;
	public String getMenuId() {
	    return this.menuId;
	}
	
	public WeChatWebsiteMenuT setMenuId(String menuId) {
		this.menuId = menuId;
		return this;
	}
	
	public String getWebId() {
	    return this.webId;
	}
	
	public WeChatWebsiteMenuT setWebId(String webId) {
		this.webId = webId;
		return this;
	}
	
	public String getParentId() {
	    return this.parentId;
	}
	
	public WeChatWebsiteMenuT setParentId(String parentId) {
		this.parentId = parentId;
		return this;
	}
	
	public String getMenuName() {
	    return this.menuName;
	}
	
	public WeChatWebsiteMenuT setMenuName(String menuName) {
		this.menuName = menuName;
		return this;
	}
	
	public String getMenuImg() {
	    return this.menuImg;
	}
	
	public WeChatWebsiteMenuT setMenuImg(String menuImg) {
		this.menuImg = menuImg;
		return this;
	}
	
	public String getClickType() {
	    return this.clickType;
	}
	
	public WeChatWebsiteMenuT setClickType(String clickType) {
		this.clickType = clickType;
		return this;
	}
	
	public String getUrl() {
	    return this.url;
	}
	
	public WeChatWebsiteMenuT setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public String getResume() {
	    return this.resume;
	}
	
	public WeChatWebsiteMenuT setResume(String resume) {
		this.resume = resume;
		return this;
	}
	
	public String getText() {
	    return this.text;
	}
	
	public WeChatWebsiteMenuT setText(String text) {
		this.text = text;
		return this;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}
}
