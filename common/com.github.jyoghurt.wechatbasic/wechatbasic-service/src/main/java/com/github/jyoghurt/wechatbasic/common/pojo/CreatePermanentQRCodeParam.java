package com.github.jyoghurt.wechatbasic.common.pojo;

/**
 * Created with IntelliJ IDEA.
 * User: DELL
 * Date: 2017/12/29
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
public class CreatePermanentQRCodeParam {

    private String scene;

    private String path;

    private Integer width;

    private Boolean auto_color;

    private Object line_color;

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Boolean getAuto_color() {
        return auto_color;
    }

    public void setAuto_color(Boolean auto_color) {
        this.auto_color = auto_color;
    }

    public Object getLine_color() {
        return line_color;
    }

    public void setLine_color(Object line_color) {
        this.line_color = line_color;
    }
}
