package com.github.jyoghurt.image.domain;

import com.github.jyoghurt.core.domain.BaseEntity;

import javax.persistence.Transient;
import java.io.File;


@javax.persistence.Table(name = "ImgT")
public class ImgT extends BaseEntity<ImgT>{
	
	/** 
	 * 图片ID  
	 */
	@javax.persistence.Id
	private String imgId;
	/** 
	 * 图片名称  
	 */
	private String name;
	/** 
	 * 业务主键  
	 */
	private String naturalkey;
	/** 
	 * 存储路径  
	 */
	private String path;

	/**
	 * 类型
	 */
	private String type;

	/**
	 * 图片二进制流
	 */
	@Transient
	private String imgContent;

	/**
	 * 图片描述
	 */
	private String imgDesc;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String relPath;

	public String getRelPath() {
		return path+ File.separatorChar+name;
	}

	public void setRelPath(String relPath) {
		this.relPath = relPath;
	}

	
	public String getImgId() {
	    return this.imgId;
	}
	
	public ImgT setImgId(String imgId) {
		this.imgId = imgId;
		return this;
	}
	
	public String getName() {
	    return this.name;
	}
	
	public ImgT setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getNaturalkey() {
	    return this.naturalkey;
	}
	
	public ImgT setNaturalkey(String naturalkey) {
		this.naturalkey = naturalkey;
		return this;
	}
	
	public String getPath() {
	    return this.path;
	}
	
	public ImgT setPath(String path) {
		this.path = path;
		return this;
	}

	public String getImgDesc() {
		return imgDesc;
	}

	public ImgT setImgDesc(String imgDesc) {
		this.imgDesc = imgDesc;
		return this;
	}

	public String getImgContent() {
		return imgContent;
	}

	public void setImgContent(String imgContent) {
		this.imgContent = imgContent;
	}
}
