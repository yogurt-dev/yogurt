package com.github.jyoghurt.excel.vo;

import java.lang.reflect.Field;

public class ImportExcelVo {

    private Field field;
    private int order;
    private String dateFormat;
    private String javaScriptBody;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getJavaScriptBody() {
        return javaScriptBody;
    }

    public void setJavaScriptBody(String javaScriptBody) {
        this.javaScriptBody = javaScriptBody;
    }
}
