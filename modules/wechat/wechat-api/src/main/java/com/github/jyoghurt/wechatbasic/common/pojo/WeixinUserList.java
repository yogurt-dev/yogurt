package com.github.jyoghurt.wechatbasic.common.pojo;

import java.util.List;

/**
 * ��ע�û��б�
 * 
 * @author yutao
 * @date 2013-11-09
 */
public class WeixinUserList {
	// �����˺ŵ��ܹ�ע�û���
	private int total;
	// ��ȡ��OpenID����
	private int count;
	// OpenID�б�
	private List<String> openIdList;
	// ��ȡ�б�ĺ�һ���û���OPENID
	private String nextOpenId;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<String> getOpenIdList() {
		return openIdList;
	}

	public void setOpenIdList(List<String> openIdList) {
		this.openIdList = openIdList;
	}

	public String getNextOpenId() {
		return nextOpenId;
	}

	public void setNextOpenId(String nextOpenId) {
		this.nextOpenId = nextOpenId;
	}
}
