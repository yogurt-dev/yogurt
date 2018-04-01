package com.github.jyoghurt.dataDict.domain;


import com.github.jyoghurt.core.domain.BaseEntity;

import javax.persistence.Transient;
import java.util.List;

@javax.persistence.Table(name = "DataDictValue")
public class DataDictValue extends BaseEntity<DataDictValue> {

    private static final long serialVersionUID = -56879755669907845L;

    /**
     * 主键
     */
    @javax.persistence.Id
    private String dictValueId;
    /**
     * 字典项编码
     */
    private String dictItemCode;
    /**
     * 字典值编码
     */
    private String dictValueCode;
    /**
     * 字典值名称
     */
    private String dictValueName;
    /**
     * 描述
     */
    private String dictValueDesc;
    /**
     * 父字典项编码
     */
    private String parentDictItemCode;
    /**
     * 父字典值编码
     */
    private String parentDictValueCode;
    /**
     * 排序
     */
    private Integer sortNum;
    /**
     * 子枚举项列表
     */
    @Transient
    private List<DataDictValue> children;
    /**
     * 枚举项
     */
    @Transient
    private DataDictItem dataDictItem;

    @Transient
    private boolean match = false;

    @Transient
    private boolean isParent = false;

    @Transient
    private String parentTreeNodeStr;

    @Transient
    private String treeNodeStr;

    /**
     * 父枚举项名称
     */
    @Transient
    private String parentDictValueName;

    public String getDictValueId() {
        return this.dictValueId;
    }

    public DataDictValue setDictValueId(String dictValueId) {
        this.dictValueId = dictValueId;
        return this;
    }

    public String getDictItemCode() {
        return this.dictItemCode;
    }

    public DataDictValue setDictItemCode(String dictItemCode) {
        this.dictItemCode = dictItemCode;
        return this;
    }

    public String getDictValueCode() {
        return this.dictValueCode;
    }

    public DataDictValue setDictValueCode(String dictValueCode) {
        this.dictValueCode = dictValueCode;
        return this;
    }

    public String getDictValueName() {
        return this.dictValueName;
    }

    public DataDictValue setDictValueName(String dictValueName) {
        this.dictValueName = dictValueName;
        return this;
    }

    public String getDictValueDesc() {
        return this.dictValueDesc;
    }

    public DataDictValue setDictValueDesc(String dictValueDesc) {
        this.dictValueDesc = dictValueDesc;
        return this;
    }

    public String getParentDictItemCode() {
        return this.parentDictItemCode;
    }

    public DataDictValue setParentDictItemCode(String parentDictItemCode) {
        this.parentDictItemCode = parentDictItemCode;
        return this;
    }

    public String getParentDictValueCode() {
        return this.parentDictValueCode;
    }

    public DataDictValue setParentDictValueCode(String parentDictValueCode) {
        this.parentDictValueCode = parentDictValueCode;
        return this;
    }

    public Integer getSortNum() {
        return this.sortNum;
    }

    public DataDictValue setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
        return this;
    }

    public List<DataDictValue> getChildren() {
        return children;
    }

    public DataDictValue setChildren(List<DataDictValue> children) {
        this.children = children;
        return this;
    }

    public DataDictItem getDataDictItem() {
        return dataDictItem;
    }

    public DataDictValue setDataDictItem(DataDictItem dataDictItem) {
        this.dataDictItem = dataDictItem;
        return null;
    }

    public boolean isMatch() {
        return match;
    }

    public DataDictValue setMatch(boolean match) {
        this.match = match;
        return this;
    }

    public String getTreeNodeStr() {
        return treeNodeStr;
    }

    public void setTreeNodeStr(String treeNodeStr) {
        this.treeNodeStr = treeNodeStr;
    }

    public String getParentTreeNodeStr() {
        return parentTreeNodeStr;
    }

    public void setParentTreeNodeStr(String parentTreeNodeStr) {
        this.parentTreeNodeStr = parentTreeNodeStr;
    }

    public String getParentDictValueName() {
        return parentDictValueName;
    }

    public void setParentDictValueName(String parentDictValueName) {
        this.parentDictValueName = parentDictValueName;
    }


    public boolean getIsParent() {
        return isParent;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataDictValue that = (DataDictValue) o;

        if (!dictItemCode.equals(that.dictItemCode)) return false;
        return dictValueCode.equals(that.dictValueCode);

    }

    @Override
    public int hashCode() {
        int result = dictItemCode.hashCode();
        result = 31 * result + dictValueCode.hashCode();
        return result;
    }
}
