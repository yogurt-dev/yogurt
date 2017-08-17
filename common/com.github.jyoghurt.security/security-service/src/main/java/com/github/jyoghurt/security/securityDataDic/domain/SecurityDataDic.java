package com.github.jyoghurt.security.securityDataDic.domain;

import com.github.jyoghurt.core.domain.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@javax.persistence.Table(name = "SecurityDataDic")
public class SecurityDataDic extends BaseEntity{
	
	/** 
	 *  字典ID  
	*/
	@javax.persistence.Id
	private String id;
	/** 
	 *  字典名称  
	*/
	private String dicName;
	/** 
	 *  key  
	*/
	private String key;
	/** 
	 *  value  
	*/
	private String value;
	public String getId() {
	    return this.id;
	}
	public SecurityDataDic setId(String id) {
		this.id=id;
		return this;
	}
	public String getDicName() {
	    return this.dicName;
	}
	public SecurityDataDic setDicName(String dicName) {
		this.dicName=dicName;
		return this;
	}
	public String getKey() {
	    return this.key;
	}
	public SecurityDataDic setKey(String key) {
		this.key=key;
		return this;
	}
	public String getValue() {
	    return this.value;
	}
	public SecurityDataDic setValue(String value) {
		this.value=value;
		return this;
	}
}
