package com.github.jyoghurt.wechatbasic.common.pojo;

/**
 * ý���ļ���Ϣ
 * 
 * @author yutao
 * @date 2013-11-09
 */
public class WeixinMedia {
	//媒体文件类型
	private String type;
	//媒体Id
	private String media_id;
	//文件上传时间
	private int createdAt;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMedia_id() {
		return media_id;
	}

	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}

	public int getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(int createdAt) {
		this.createdAt = createdAt;
	}
}
