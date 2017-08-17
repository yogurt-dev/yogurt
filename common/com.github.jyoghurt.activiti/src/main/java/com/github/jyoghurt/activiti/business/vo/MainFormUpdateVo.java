package com.github.jyoghurt.activiti.business.vo;

/**
 * user:dell
 * date: 2016/9/19.
 */
public class MainFormUpdateVo {
    private String mainFormTableName;
    private String mainFormIdName;
    private String mainFormId;
    private String updateColumnName;
    private String updateColumnValue;

    public String getMainFormTableName() {
        return mainFormTableName;
    }

    public void setMainFormTableName(String mainFormTableName) {
        this.mainFormTableName = mainFormTableName;
    }

    public String getUpdateColumnName() {
        return updateColumnName;
    }

    public void setUpdateColumnName(String updateColumnName) {
        this.updateColumnName = updateColumnName;
    }

    public String getMainFormId() {
        return mainFormId;
    }

    public void setMainFormId(String mainFormId) {
        this.mainFormId = mainFormId;
    }

    public String getUpdateColumnValue() {
        return updateColumnValue;
    }

    public void setUpdateColumnValue(String updateColumnValue) {
        this.updateColumnValue = updateColumnValue;
    }

    public String getMainFormIdName() {
        return mainFormIdName;
    }

    public void setMainFormIdName(String mainFormIdName) {
        this.mainFormIdName = mainFormIdName;
    }
}
