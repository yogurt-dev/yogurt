package com.github.jyoghurt.sw.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.jyoghurt.core.domain.BaseEntity;
import com.github.jyoghurt.core.result.BaseResult;


@javax.persistence.Table(name = "SwitchT")
public class SwitchT extends BaseEntity<SwitchT>{
	
	/** 
	 *   
	 */
	@javax.persistence.Id
	private String switchId;
	/** 
	 * 开关组key  
	 */
	private String switchGroupKey;
	/** 
	 * 开关状态  
	 */
	private Integer switchStatus;
	/** 
	 * 生效时间  
	 */
	private java.util.Date availableTime;
	/**
	 * 备注
	 */
	private String remark;

	
	public String getSwitchId() {
	    return this.switchId;
	}
	
	public SwitchT setSwitchId(String switchId) {
		this.switchId = switchId;
		return this;
	}

	@JsonView(BaseResult.class)
	public String getSwitchGroupKey() {
	    return this.switchGroupKey;
	}
	
	public SwitchT setSwitchGroupKey(String switchGroupKey) {
		this.switchGroupKey = switchGroupKey;
		return this;
	}

	public Integer getSwitchStatus() {
	    return this.switchStatus;
	}
	
	public SwitchT setSwitchStatus(Integer switchStatus) {
		this.switchStatus = switchStatus;
		return this;
	}
	
	public java.util.Date getAvailableTime() {
	    return this.availableTime;
	}
	
	public SwitchT setAvailableTime(java.util.Date availableTime) {
		this.availableTime = availableTime;
		return this;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
