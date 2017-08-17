package com.github.jyoghurt.excel.vo;


/**
 * Excel标题，用于根据order排序
 */
public class ExcelHeader implements Comparable<ExcelHeader> {

    private Class fieldType;
    private String title;
    private int order;

    public ExcelHeader(Class fieldType, String title, int order) {
        this.fieldType = fieldType;
        this.title = title;
        this.order = order;
    }

    public Class getFieldType() {
        return fieldType;
    }

    public void setFieldType(Class fieldType) {
        this.fieldType = fieldType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int compareTo(ExcelHeader o) {
        return this.order > o.order ? 1 : -1;
    }
}
