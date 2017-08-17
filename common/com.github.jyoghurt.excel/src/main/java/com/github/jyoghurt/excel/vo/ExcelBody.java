package com.github.jyoghurt.excel.vo;


/**
 * Excel内容，用于根据order排序
 */
public class ExcelBody implements Comparable<ExcelBody> {

    private Class fieldType;
    private String content;
    private int order;

    public ExcelBody(Class fieldType, String content, int order) {
        this.fieldType = fieldType;
        this.content = content;
        this.order = order;
    }

    public Class getFieldType() {
        return fieldType;
    }

    public void setFieldType(Class fieldType) {
        this.fieldType = fieldType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int compareTo(ExcelBody o) {
        return this.order > o.order ? 1 : -1;
    }
}
