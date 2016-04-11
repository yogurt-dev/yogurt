package com.github.jyoghurt.serverApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.jyoghurt.serverApi.StringUtils.trim;

/**
 * Created by jtwu on 2016/3/30.
 */
class InterfaceEntity {

    /**
     * 接口名称
     */
    private String ifName;
    /**
     * 接口地址
     */
    private String ifUrl;

    /**
     * 接口描述
     */
    private String ifDesc;
    /**
     * 接口类型
     */
    private String ifType;
    /**
     * 起始版本
     */
    private String since;
    /**
     * 接口请求参数
     */
    private List<ParameterEntity> requestParams = new ArrayList<>();
    /**
     * 接口返回参数
     */
    private List<ClassEntity> responseClass = new ArrayList<>();
    /**
     * 错误码
     */
    private Map<String, String> errors = new HashMap();

    public String getIfName() {
        return trim(ifName);
    }

    public void setIfName(String ifName) {
        this.ifName = ifName;
    }

    public String getIfUrl() {
        return ifUrl;
    }

    public void setIfUrl(String ifUrl) {
        this.ifUrl = ifUrl;
    }

    public String getIfDesc() {
        return trim(ifDesc);
    }

    public void setIfDesc(String ifDesc) {
        this.ifDesc = ifDesc;
    }

    public String getIfType() {
        return ifType;
    }

    public void setIfType(String ifType) {
        this.ifType = ifType;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public List<ParameterEntity> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(List<ParameterEntity> requestParams) {
        this.requestParams = requestParams;
    }

    public List<ClassEntity> getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(List<ClassEntity> responseClass) {
        this.responseClass = responseClass;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
