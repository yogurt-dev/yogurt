package com.github.jyoghurt.serverApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtwu on 2016/4/1.
 */
public class ClassEntity {
    private String className;
    private List<ParameterEntity> params = new ArrayList<>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<ParameterEntity> getParams() {
        return params;
    }

    public void setParams(List<ParameterEntity> params) {
        this.params = params;
    }
}
