package com.github.jyoghurt.wechatbasic.common.pojo;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: DELL
 * Date: 2017/12/29
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
public class CreatePermanentQRCodeParam {

    private String scene;

    private String page;

    private Integer width;

    private Boolean auto_color;

    private Map line_color;

    public String getScene() {
        return scene;
    }

    public CreatePermanentQRCodeParam setScene(String scene) {
        this.scene = scene;
        return this;
    }

    public String getPage() {
        return page;
    }

    public CreatePermanentQRCodeParam setPage(String page) {
        this.page = page;
        return this;
    }

    public Integer getWidth() {
        return width;
    }

    public CreatePermanentQRCodeParam setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public Boolean getAuto_color() {
        return auto_color;
    }

    public CreatePermanentQRCodeParam setAuto_color(Boolean auto_color) {
        this.auto_color = auto_color;
        return this;
    }

    public Map getLine_color() {
        return line_color;
    }

    public CreatePermanentQRCodeParam setLine_color(Map line_color) {
        this.line_color = line_color;
        return this;
    }
}
