package com.github.jyoghurt.weChat.domain;

import com.github.jyoghurt.wechatbasic.enums.WeChatAutoResponseType;
import com.github.jyoghurt.wechatbasic.enums.WeChatMatchingType;
import com.github.jyoghurt.core.domain.BaseEntity;

import javax.persistence.Transient;
import java.util.List;


@javax.persistence.Table(name = "WeChatAutoResponseT")
public class WeChatAutoResponseT extends BaseEntity{
	
	/** 
	 * 主键  
	 */
	@javax.persistence.Id
	private String autoResponseId;
	/** 
	 * appId  
	 */
	private String appId;
	/** 
	 * 关键词用，分割  
	 */
	private String keywords;
	/** 
	 * 回复类型  
	 */
	private WeChatAutoResponseType responseType;
	/** 
	 * 匹配类型  
	 */
	private WeChatMatchingType matchingType;
	/*规则名称*/
	private String ruleName;

	@Transient
	private List<WeChatAutoResponseMsgT> weChatAutoResponseMsgTList;

	public List<WeChatAutoResponseMsgT> getWeChatAutoResponseMsgTList() {
		return weChatAutoResponseMsgTList;
	}

	public void setWeChatAutoResponseMsgTList(List<WeChatAutoResponseMsgT> weChatAutoResponseMsgTList) {
		this.weChatAutoResponseMsgTList = weChatAutoResponseMsgTList;
	}

	/*状态0为启用，1为禁用*/
	private String state;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAutoResponseId() {
	    return this.autoResponseId;
	}
	
	public WeChatAutoResponseT setAutoResponseId(String autoResponseId) {
		this.autoResponseId = autoResponseId;
		return this;
	}
	
	public String getAppId() {
	    return this.appId;
	}
	
	public WeChatAutoResponseT setAppId(String appId) {
		this.appId = appId;
		return this;
	}
	
	public String getKeywords() {
	    return this.keywords;
	}
	
	public WeChatAutoResponseT setKeywords(String keywords) {
		this.keywords = keywords;
		return this;
	}

	public WeChatAutoResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(WeChatAutoResponseType responseType) {
		this.responseType = responseType;
	}

	public WeChatMatchingType getMatchingType() {
		return matchingType;
	}

	public void setMatchingType(WeChatMatchingType matchingType) {
		this.matchingType = matchingType;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
}
