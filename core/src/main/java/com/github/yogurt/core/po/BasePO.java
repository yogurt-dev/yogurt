package com.github.yogurt.core.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @author jtwu
 * @date 2015/4/21
 */
@ApiModel(value = "项目")
@DynamicInsert
@DynamicUpdate

@MappedSuperclass
@EqualsAndHashCode(of = {"id"})
public class BasePO<T extends BasePO<T>> implements Serializable {

	private static final long serialVersionUID = 6468926052770326495L;

	@ApiModelProperty("主键")
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ApiModelProperty("创建人id")
	@Column(name = "creator_id")
	private Long creatorId;

	@ApiModelProperty("创建时间")
	@Column(name = "gmt_create")
	private LocalDateTime gmtCreate;

	@ApiModelProperty("修改人ID")
	@Column(name = "modifier_id")
	private Long modifierId;

	@ApiModelProperty("修改时间")
	@Column(name = "gmt_modified")
	private LocalDateTime gmtModified;

	@ApiModelProperty("逻辑删除标识")
	@Column(name = "is_deleted")
	private Boolean deleted;

	public Long getId() {
		return id;
	}

	public T setId(Long id) {
		this.id = id;
		return (T) this;
	}

	public LocalDateTime getGmtCreate() {
		return gmtCreate;
	}

	public T setGmtCreate(LocalDateTime gmtCreate) {
		this.gmtCreate = gmtCreate;
		return (T) this;
	}

	public LocalDateTime getGmtModified() {
		return gmtModified;
	}

	public T setGmtModified(LocalDateTime gmtModified) {
		this.gmtModified = gmtModified;
		return (T) this;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public T setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
		return (T) this;
	}

	public Long getModifierId() {
		return modifierId;
	}

	public T setModifierId(Long modifierId) {
		this.modifierId = modifierId;
		return (T) this;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public T setDeleted(Boolean deleted) {
		this.deleted = deleted;
		return (T) this;
	}
}
