package com.github.jyoghurt.serverApi;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.github.jyoghurt.serverApi.StringUtils.trim;

/**
 * Created by jtwu on 2016/3/30.
 */
public class ParameterEntity {
    /**
     * 参数名称
     */
    private String paramName;
    /**
     * 参数编码
     */
    private String paramCode;
    /**
     * 参数说明
     */
    private String paramDesc;
    /**
     * 参数类型
     */
    private String paramType;
    /**
     * 参数校验规则
     */
    private List<String> paramValidRules = new ArrayList<>();
    /**
     * 参数是否必填
     */
    private Boolean required = false;
    /**
     * 参数的泛型列表
     */
    private List<ClassEntity> genericTypeList = new ArrayList<>();

    public String getParamName() {

        return trim(paramName);
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamCode() {
        return paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    public String getParamDesc() {

        return trim(paramDesc);
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public List getParamValidRules() {
        return paramValidRules;
    }

    public void setParamValidRules(List paramValidRules) {
        this.paramValidRules = paramValidRules;
    }

    public void addParamValidRule(String paramValidRule) {
        this.paramValidRules.add(paramValidRule);
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public List<ClassEntity> getGenericTypeList() {
        return genericTypeList;
    }

    public void setGenericTypeList(List<ClassEntity> genericTypeList) {
        this.genericTypeList = genericTypeList;
    }
}
