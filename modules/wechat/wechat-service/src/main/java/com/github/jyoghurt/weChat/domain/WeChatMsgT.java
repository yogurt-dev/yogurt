package com.github.jyoghurt.weChat.domain;

import com.github.jyoghurt.wechatbasic.enums.WeChatAccountType;
import com.github.jyoghurt.core.domain.BaseEntity;

import javax.persistence.Transient;
import java.util.List;


@javax.persistence.Table(name = "WeChatMsgT")
public class WeChatMsgT extends BaseEntity{
	
	/** 
	 *  主键
	*/
	@javax.persistence.Id
	private String messageId;
	/**
	 *  信息内容
	*/
	private String textContent;
	/**
	 *  消息类型
图文消息为mpnews，文本消息为text，语音为voice，音乐为music，图片为image，视频为video

	*/
	private String msgtype;
	/**
	 *  消息的标题
	*/
	private String title;
	/**
	 *  语音为voice，音乐为music，图片为image，视频为video在本服务器的地址url
	*/
	private String url;
	/**
	 *  状态0：未发送1：已发送
	 */
	private String state;
	/**
	 *  账号类型TYPE_SUBSCRIPTION：订阅号，TYPE_SERVICE：服务号，TYPE_ENTERPRISE：企业号
	 */
	private WeChatAccountType accountType;
	private  String	unitId;

	/**
	 *
	 * 与图文信息表关联
	 */
	@Transient
	private List<WeChatMpnewsMsgT> list;
	public String getMessageId() {
	    return this.messageId;
	}
	public WeChatMsgT setMessageId(String messageId) {
		this.messageId=messageId;
		return this;
	}
	public String getTextContent() {
	    return this.textContent;
	}
	public WeChatMsgT setTextContent(String textContent) {
		this.textContent=textContent;
		return this;
	}
	public String getMsgtype() {
	    return this.msgtype;
	}
	public WeChatMsgT setMsgtype(String msgtype) {
		this.msgtype=msgtype;
		return this;
	}
	public String getTitle() {
	    return this.title;
	}
	public WeChatMsgT setTitle(String title) {
		this.title=title;
		return this;
	}
	public String getUrl() {
	    return this.url;
	}
	public WeChatMsgT setUrl(String url) {
		this.url=url;
		return this;
	}

	public List<WeChatMpnewsMsgT> getList() {
		return list;
	}

	public void setList(List<WeChatMpnewsMsgT> list) {
		this.list = list;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}


	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public WeChatAccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(WeChatAccountType accountType) {
		this.accountType = accountType;
	}
}
