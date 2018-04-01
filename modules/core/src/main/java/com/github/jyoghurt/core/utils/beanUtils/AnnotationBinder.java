package com.github.jyoghurt.core.utils.beanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.github.jyoghurt.core.annotationResolver.BaseResolver;

/**
 * Created by Administrator on 2015/6/3.
 * 注解包装类，用于辅助注解解析
 */
public class AnnotationBinder {
    private List<Field>  fields= new ArrayList<>();
    private BaseResolver resolver;
    private Class<Annotation> annotationClass;

    public AnnotationBinder(Class<Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public AnnotationBinder( BaseResolver resolver, Class<Annotation> annotationClass) {
        this.resolver = resolver;
        this.annotationClass = annotationClass;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public BaseResolver getResolver() {
        return resolver;
    }

    public void setResolver(BaseResolver resolver) {
        this.resolver = resolver;
    }

    public Class<Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public void setAnnotationClass(Class<Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnnotationBinder that = (AnnotationBinder) o;

        if (annotationClass != null ? !annotationClass.equals(that.annotationClass) : that.annotationClass != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return annotationClass != null ? annotationClass.hashCode() : 0;
    }
}
