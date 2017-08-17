package com.github.jyoghurt.excel.vo;

import java.util.List;

public class ExcelSheet {

    private String sheetName;
    private Class<?> entityType;
    private List<?> entityList;

    public String getSheetName() {
        return sheetName;
    }

    public ExcelSheet setSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public ExcelSheet setEntityType(Class<?> entityType) {
        this.entityType = entityType;
        return this;
    }

    public List<?> getEntityList() {
        return entityList;
    }

    public ExcelSheet setEntityList(List<?> entityList) {
        this.entityList = entityList;
        return this;
    }
}
