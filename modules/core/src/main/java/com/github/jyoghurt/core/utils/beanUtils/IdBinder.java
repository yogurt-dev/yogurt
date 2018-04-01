package com.github.jyoghurt.core.utils.beanUtils;

import java.beans.PropertyDescriptor;

/**
 * Created with IntelliJ IDEA.
 * User: jietianwu
 * Date: 13-11-22
 * Time: 上午9:25
 * Id属性信息
 */
public class IdBinder {
    private String fieldName;
    private PropertyDescriptor pd;
    private Class annotationClass;

    public IdBinder() {
    }

    public IdBinder(String fieldName, PropertyDescriptor pd, Class annotationClass) {
        this.fieldName = fieldName;
        this.pd = pd;
        this.annotationClass = annotationClass;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public PropertyDescriptor getPd() {
        return pd;
    }

    public void setPd(PropertyDescriptor pd) {
        this.pd = pd;
    }

    public Class getAnnotationClass() {
        return annotationClass;
    }

    public void setAnnotationClass(Class annotationClass) {
        this.annotationClass = annotationClass;
    }
}
