package com.github.jyoghurt.core.po;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * Created by jtwu on 2015/4/21.
 */
public class BasePO<T extends BasePO<T>> implements Serializable {

    @Transient
    public static final String DEFAULT_OPERATOR = "system";
    @Transient
    public static final String OPERATOR_ID = "operatorId";
    @Transient
    public static final String OPERATOR_NAME = "operatorName";
    @Transient
    public static final String DELETE_FLAG = "delete_flag";

    private static final long serialVersionUID = 6468926052770326495L;
    // 创建时间
    private LocalDateTime createDateTime;
    // 修改时间
    private LocalDateTime modifiedDateTime;
    // 创建人ID
    private String founderId;
    // 创建人姓名
    private String founderName;
    // 修改人ID
    private String modifierId;
    // 修改人姓名
    private String modifierName;
    // 逻辑删除标示
    @Column(name = "is_deleted")
    private Boolean deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public LocalDateTime getModifiedDateTime() {
        return modifiedDateTime;
    }

    public T setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
        return (T) this;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public T setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
        return (T) this;
    }


    public String getFounderId() {
        return founderId;
    }

    public T setFounderId(String founderId) {
        this.founderId = founderId;
        return (T) this;
    }

    public String getFounderName() {
        return founderName;
    }

    public T setFounderName(String founderName) {
        this.founderName = founderName;
        return (T) this;
    }

    public String getModifierId() {
        return modifierId;
    }

    public T setModifierId(String modifierId) {
        this.modifierId = modifierId;
        return (T) this;
    }

    public String getModifierName() {
        return modifierName;
    }

    public T setModifierName(String modifierName) {
        this.modifierName = modifierName;
        return (T) this;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public T setDeleted(Boolean deleted) {
        this.deleted = deleted;
        return (T)this;
    }
}
