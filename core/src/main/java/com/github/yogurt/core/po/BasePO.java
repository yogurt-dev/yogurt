package com.github.yogurt.core.po;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * Created by jtwu on 2015/4/21.
 */
public class BasePO<T extends BasePO<T>> implements Serializable {

    private static final long serialVersionUID = 6468926052770326495L;
    /**
     * 主键
     */
    @Column(name = "id")
    private Long id;
    /**
     * 创建人ID
     */
    @Column(name = "creator_id")
    private Long creatorId;
    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private LocalDateTime gmtCreate;
    /**
     * 修改人ID
     */
    @Column(name = "modifier_id")
    private Long modifierId;
    /**
     * 修改时间
     */
    @Column(name = "gmt_modified")
    private LocalDateTime gmtModified;
    /**
     * 逻辑删除标识
     */
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
