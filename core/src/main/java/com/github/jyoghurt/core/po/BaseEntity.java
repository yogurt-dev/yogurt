package com.github.jyoghurt.core.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;


/**
 * Created by jtwu on 2015/4/21.
 */
@Table
public class BaseEntity<T extends BaseEntity<T>> implements Serializable {

    @Transient
    public static final String DEFAULT_OPERATOR = "system";
    @Transient
    public static final String OPERATOR_ID = "operatorId";
    @Transient
    public static final String OPERATOR_NAME = "operatorName";
    @Transient
    public static final String DELETE_FLAG = "deleteFlag";

    private static final long serialVersionUID = 6468926052770326495L;
    // 创建时间
    private Date createDateTime;
    // 修改时间
    private Date modifyDateTime;
    // 创建人ID
    private String founderId;
    // 创建人姓名
    private String founderName;
    // 修改人ID
    private String modifierId;
    // 修改人姓名
    private String modifierName;
    // 删除标示
    @Column(nullable = false)
    private Boolean deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JSONField(format= "yyyy-MM-dd HH:mm:ss")
    public Date getModifyDateTime() {
        return modifyDateTime;
    }

    public T setModifyDateTime(Date modifyDateTime) {
        this.modifyDateTime = modifyDateTime;
        return (T) this;
    }

    @JSONField(format= "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateDateTime() {
        return createDateTime;
    }

    public T setCreateDateTime(Date createDateTime) {
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

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public T setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
        return (T) this;
    }
}
