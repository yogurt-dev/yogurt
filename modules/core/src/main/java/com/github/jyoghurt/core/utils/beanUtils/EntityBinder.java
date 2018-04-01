package com.github.jyoghurt.core.utils.beanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: jietianwu Date: 13-11-22 Time: 上午9:17 业务实体的注解信息
 */
public class EntityBinder {
    private List<IdBinder> idBinderList = new ArrayList<IdBinder>();
    /**
     * 存储所有有注解的属性
     */
    private List<AnnotationBinder> annotationBinders = new ArrayList<>();

    public List<IdBinder> getIdBinderList() {
        return idBinderList;
    }

    public void setIdBinderList(List<IdBinder> idBinderList) {
        this.idBinderList = idBinderList;
    }


    public List<AnnotationBinder> getAnnotationBinders() {
        return annotationBinders;
    }

    public void setAnnotationBinders(List<AnnotationBinder> annotationBinders) {
        this.annotationBinders = annotationBinders;
    }
}
