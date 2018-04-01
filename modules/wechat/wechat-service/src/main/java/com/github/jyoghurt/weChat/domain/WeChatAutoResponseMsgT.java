package com.github.jyoghurt.weChat.domain;

import com.github.jyoghurt.wechatbasic.enums.WeChatMsgTypeEnum;
import com.github.jyoghurt.core.domain.BaseEntity;


@javax.persistence.Table(name = "WeChatAutoResponseMsgT")
public class WeChatAutoResponseMsgT extends BaseEntity{
	
	/** 
	 * 主键  
	 */
	@javax.persistence.Id
	private String msgId;
	/** 
	 * 回复规则Id  
	 */
	private String autoResponseId;
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
	private String content;
	
	public String getMsgId() {
	    return this.msgId;
	}
	
	public WeChatAutoResponseMsgT setMsgId(String msgId) {
		this.msgId = msgId;
		return this;
	}
	
	public String getAutoResponseId() {
	    return this.autoResponseId;
	}
	
	public WeChatAutoResponseMsgT setAutoResponseId(String autoResponseId) {
		this.autoResponseId = autoResponseId;
		return this;
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
