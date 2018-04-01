package com.github.jyoghurt.weChat.domain;

import com.github.jyoghurt.core.domain.BaseEntity;


@javax.persistence.Table(name = "WeChatConversationT")
public class WeChatConversationT extends BaseEntity<WeChatConversationT>{
	
	/** 
	 * 主键  
	 */
	@javax.persistence.Id
	private String id;
	/** 
	 * 信息来源  
	 */
	private String fromUserId;
	/** 
	 * 内容  
	 */
	private String context;
	/** 
	 * 是否接收  
	 */
	private String receive;
	/** 
	 *   
	 */
	private String fromUserName;
	/** 
	 *   
	 */
	private String ifreply;
	private String img;


	public String getImg() {
		return img;
	}

	public WeChatConversationT setImg(String img) {
		this.img = img;
		return this;
	}

	public String getId() {
	    return this.id;
	}
	
	public WeChatConversationT setId(String id) {
		this.id = id;
		return this;
	}
	
	public String getFromUserId() {
	    return this.fromUserId;
	}
	
	public WeChatConversationT setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
		return this;
	}
	
	public String getContext() {
	    return this.context;
	}
	
	public WeChatConversationT setContext(String context) {
		this.context = context;
		return this;
	}
	
	public String getReceive() {
	    return this.receive;
	}
	
	public WeChatConversationT setReceive(String receive) {
		this.receive = receive;
		return this;
	}
	
	public String getFromUserName() {
	    return this.fromUserName;
	}
	
	public WeChatConversationT setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
		return this;
	}
	
	public String getIfreply() {
	    return this.ifreply;
	}
	
	public WeChatConversationT setIfreply(String ifreply) {
		this.ifreply = ifreply;
		return this;
	}
}
