package com.github.jyoghurt.wechatbasic.common.pojo;

/**
 * ��ʱ��ά����Ϣ
 * 
 * @author yutao
 * @date 2013-11-10
 */
public class WeixinQRCode {
	// ��ȡ�Ķ�ά��ticket
	private String ticket;
	// ��ά�����Чʱ�䣬��λΪ�룬��󲻳���1800
	private int expireSeconds;

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public int getExpireSeconds() {
		return expireSeconds;
	}

	public void setExpireSeconds(int expireSeconds) {
		this.expireSeconds = expireSeconds;
	}
}
