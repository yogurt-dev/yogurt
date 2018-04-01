package com.github.jyoghurt.weChat.domain;

import com.github.jyoghurt.core.domain.BaseEntity;


@javax.persistence.Table(name = "WeChatMpnewsMsgT")
public class WeChatMpnewsMsgT extends BaseEntity{
	
	/** 
	 *  主键
	*/
	@javax.persistence.Id
	private String mpnewsId;
	/**
	 *  外键WeChatMsgT表的主键
	*/
	private String messageId;
	/**
	 *  图文消息的作者
	*/
	private String author;
	/**
	 *  图文消息的标题
	*/
	private String newsTitle;
	/**
	 *  在图文消息页面点击“阅读原文”后的页面
	*/
	private String contentSourceUrl;
	/**
	 *  图文消息页面的内容，支持HTML标签
	*/
	private String content;
	/**
	 *  图文消息的描述
	*/
	private String digest;

	/**
	 *  界面浏览图片相对地址
	 */
   private String thumUrl;
	/**
	 *  排序
	 */
	private String sort;
	/**
	 *  图片是否显示在正文中
	 */
	private Boolean showCoverPic;


	public String getMessageId() {
	    return this.messageId;
	}
	public WeChatMpnewsMsgT setMessageId(String messageId) {
		this.messageId=messageId;
		return this;
	}
	public String getAuthor() {
	    return this.author;
	}
	public WeChatMpnewsMsgT setAuthor(String author) {
		this.author=author;
		return this;
	}

	public String getContentSourceUrl() {
	    return this.contentSourceUrl;
	}
	public WeChatMpnewsMsgT setContentSourceUrl(String contentSourceUrl) {
		this.contentSourceUrl=contentSourceUrl;
		return this;
	}
	public String getContent() {
	    return this.content;
	}
	public WeChatMpnewsMsgT setContent(String content) {
		this.content=content;
		return this;
	}
	public String getDigest() {
	    return this.digest;
	}
	public WeChatMpnewsMsgT setDigest(String digest) {
		this.digest=digest;
		return this;
	}


	public String getThumUrl() {
		return thumUrl;
	}

	public void setThumUrl(String thumUrl) {
		this.thumUrl = thumUrl;
	}

	public Boolean getShowCoverPic() {
		return showCoverPic;
	}

	public void setShowCoverPic(Boolean showCoverPic) {
		this.showCoverPic = showCoverPic;
	}

	public String getMpnewsId() {
		return mpnewsId;
	}

	public void setMpnewsId(String mpnewsId) {
		this.mpnewsId = mpnewsId;
	}

	public String getNewsTitle() {
		return newsTitle;
	}

	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
}
