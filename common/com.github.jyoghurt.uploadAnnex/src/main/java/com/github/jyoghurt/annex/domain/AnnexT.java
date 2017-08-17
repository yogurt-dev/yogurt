package com.github.jyoghurt.annex.domain;

import com.github.jyoghurt.core.domain.BaseEntity;


@javax.persistence.Table(name = "AnnexT")
public class AnnexT extends BaseEntity<AnnexT>{
	
	/** 
	 * 主键  
	 */
	@javax.persistence.Id
	private String annexId;
	/** 
	 * 实体名称  
	 */
	private String entityName;
	/** 
	 * 实体主键  
	 */
	private String entityId;
	/** 
	 * 附件扩展字段  
	 */
	private String expand;
	/** 
	 * 附件地址  
	 */
	private String annexPath;
	/** 
	 * 附件大小  
	 */
	private String attachmentSize;
	/**
	 * 附件名称
	 */
	private String  fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAnnexId() {
	    return this.annexId;
	}
	
	public AnnexT setAnnexId(String annexId) {
		this.annexId = annexId;
		return this;
	}
	
	public String getEntityName() {
	    return this.entityName;
	}
	
	public AnnexT setEntityName(String entityName) {
		this.entityName = entityName;
		return this;
	}
	
	public String getEntityId() {
	    return this.entityId;
	}
	
	public AnnexT setEntityId(String entityId) {
		this.entityId = entityId;
		return this;
	}
	
	public String getExpand() {
	    return this.expand;
	}
	
	public AnnexT setExpand(String expand) {
		this.expand = expand;
		return this;
	}
	
	public String getAnnexPath() {
	    return this.annexPath;
	}
	
	public AnnexT setAnnexPath(String annexPath) {
		this.annexPath = annexPath;
		return this;
	}
	
	public String getAttachmentSize() {
	    return this.attachmentSize;
	}
	
	public AnnexT setAttachmentSize(String attachmentSize) {
		this.attachmentSize = attachmentSize;
		return this;
	}
}
