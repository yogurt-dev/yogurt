package com.github.jyoghurt.cg;

import org.apache.maven.plugins.annotations.Parameter;
/**
 * 生成内容
 */
public class GenerateBean {

    @Parameter
    private Boolean po;

    @Parameter
    private Boolean dao;

    @Parameter
    private Boolean service;

    @Parameter
    private Boolean controller;

    @Parameter
    private Boolean ui;

    public Boolean getPo() {
        return po;
    }

    public void setPo(Boolean po) {
        this.po = po;
    }

    public Boolean getDao() {
        return dao;
    }

    public void setDao(Boolean dao) {
        this.dao = dao;
    }

    public Boolean getService() {
        return service;
    }

    public void setService(Boolean service) {
        this.service = service;
    }

    public Boolean getController() {
        return controller;
    }

    public void setController(Boolean controller) {
        this.controller = controller;
    }

    public Boolean getUi() {
        return ui;
    }

    public void setUi(Boolean ui) {
        this.ui = ui;
    }
}
