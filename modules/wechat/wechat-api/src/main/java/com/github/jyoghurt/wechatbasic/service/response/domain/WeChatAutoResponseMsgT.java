package com.github.jyoghurt.wechatbasic.service.response.domain;


import com.github.jyoghurt.core.domain.BaseEntity;
import com.github.jyoghurt.wechatbasic.enums.WeChatAutoResponseType;
import com.github.jyoghurt.wechatbasic.enums.WeChatMatchingType;
import com.github.jyoghurt.wechatbasic.enums.WeChatMsgTypeEnum;
import org.hibernate.validator.constraints.Length;


@javax.persistence.Table(name = "WeChatAutoResponseMsgT")
public class WeChatAutoResponseMsgT extends BaseEntity<WeChatAutoResponseMsgT> {
	
	/** 
	 * 回复消息id  
	 */
	@javax.persistence.Id
	@Length(max = 36, message = "autoResponseId长度不能超过36！")
	private String autoResponseId;
	/** 
	 * 关键词用，分割   
	 */
	private String keywords;
	/** 
	 * 匹配类型  
	 */
	private WeChatMatchingType matchingType;
	/** 
	 * 事件类型  
	 */
	private WeChatAutoResponseType responseType;
	/** 
	 * 回复类型  
	 */
	private WeChatMsgTypeEnum msgType;
	/** 
	 * 媒体Id冗余字段用来获取图文消息  
	 */
	private String mediaId;
	/** 
	 * 回复内容  
	 */
	@Length(max = 256, message = "content长度不能超过256！")
	private String content;
	
	public String getAutoResponseId() {
	    return this.autoResponseId;
	}
	
	public WeChatAutoResponseMsgT setAutoResponseId(String autoResponseId) {
		this.autoResponseId = autoResponseId;
		return this;
	}
	
	public String getKeywords() {
	    return this.keywords;
	}
	
	public WeChatAutoResponseMsgT setKeywords(String keywords) {
		this.keywords = keywords;
		return this;
	}

	public WeChatMatchingType getMatchingType() {
		return matchingType;
	}

	public void setMatchingType(WeChatMatchingType matchingType) {
		this.matchingType = matchingType;
	}

	public WeChatAutoResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(WeChatAutoResponseType responseType) {
		this.responseType = responseType;
	}

	public WeChatMsgTypeEnum getMsgType() {
		return msgType;
	}

	public void setMsgType(WeChatMsgTypeEnum msgType) {
		this.msgType = msgType;
	}

	public String getMediaId() {
	    return this.mediaId;
	}
	
	public WeChatAutoResponseMsgT setMediaId(String mediaId) {
		this.mediaId = mediaId;
		return this;
	}
	
	public String getContent() {
	    return this.content;
	}
	
	public WeChatAutoResponseMsgT setContent(String content) {
		this.content = content;
		return this;
	}
}
