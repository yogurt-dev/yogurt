package com.github.jyoghurt.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;


/**
 * Created by jtwu on 2015/4/21.
 */
public class BaseEntity implements Serializable {

    @Transient
    public static final String DEFAULT_OPERATOR = "system";
    @Transient
    public static final String OPERATOR_ID = "operatorId";
    @Transient
    public static final String OPERATOR_NAME = "operatorName";

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getModifyDateTime() {
        return modifyDateTime;
    }

    public void setModifyDateTime(Date modifyDateTime) {
        this.modifyDateTime = modifyDateTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateDateTime() {
        return createDateTime;
    }

    public BaseEntity setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
        return this;
    }


    public String getFounderId() {
        return founderId;
    }

    public BaseEntity setFounderId(String founderId) {
        this.founderId = founderId;
        return this;
    }

    public String getFounderName() {
        return founderName;
    }

    public BaseEntity setFounderName(String founderName) {
        this.founderName = founderName;
        return this;
    }

    public String getModifierId() {
        return modifierId;
    }

    public BaseEntity setModifierId(String modifierId) {
        this.modifierId = modifierId;
        return this;
    }

    public String getModifierName() {
        return modifierName;
    }

    public BaseEntity setModifierName(String modifierName) {
        this.modifierName = modifierName;
        return this;
    }
}
