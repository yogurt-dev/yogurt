package com.github.jyoghurt.dataDict.domain;


import com.github.jyoghurt.core.domain.BaseEntity;

import javax.persistence.Transient;

@javax.persistence.Table(name = "DataDictItem")
public class DataDictItem extends BaseEntity<DataDictItem> {

    private static final long serialVersionUID = -56879755649907845L;

    /**
     * 主键
     */
    @javax.persistence.Id
    private String dictItemId;
    /**
     * 字典项编码
     */
    private String dictItemCode;
    /**
     * 字典项名称
     */
    private String dictItemName;
     /**
     * 描述
     */
    private String dicItemDesc;
    /**
     * 父枚举项Code
     */
    private String parentDictItemCode;

    /**
     * 排序字段
     */
    private Integer sortNum;

    public String getParentDictItemName() {
        return parentDictItemName;
    }

    public void setParentDictItemName(String parentDictItemName) {
        this.parentDictItemName = parentDictItemName;
    }

    /**
     * 父枚举项id
     */
    @Transient
    private String parentDictItemName;
    /**
     * 是否支持界面配置
     */
    private Boolean uiConfigurable;


    public String getSingleFlag() {
        return singleFlag;
    }

    public void setSingleFlag(String singleFlag) {
        this.singleFlag = singleFlag;
    }

    /**
     * 是否单独显示
     */
    private String singleFlag;

    public String getDictItemId() {
        return this.dictItemId;
    }

    public DataDictItem setDictItemId(String dictItemId) {
        this.dictItemId = dictItemId;
        return this;
    }

    public String getDictItemCode() {
        return this.dictItemCode;
    }

    public DataDictItem setDictItemCode(String dictItemCode) {
        this.dictItemCode = dictItemCode;
        return this;
    }

    public String getDictItemName() {
        return this.dictItemName;
    }

    public DataDictItem setDictItemName(String dictItemName) {
        this.dictItemName = dictItemName;
        return this;
    }

    public String getDicItemDesc() {
        return this.dicItemDesc;
    }

    public DataDictItem setDicItemDesc(String dicItemDesc) {
        this.dicItemDesc = dicItemDesc;
        return this;
    }

    public String getParentDictItemCode() {
        return this.parentDictItemCode;
    }

    public DataDictItem setParentDictItemCode(String parentDictItemCode) {
        this.parentDictItemCode = parentDictItemCode;
        return this;
    }

    public Boolean getUiConfigurable() {
        return this.uiConfigurable;
    }

    public DataDictItem setUiConfigurable(Boolean uiConfigurable) {
        this.uiConfigurable = uiConfigurable;
        return this;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }
}
