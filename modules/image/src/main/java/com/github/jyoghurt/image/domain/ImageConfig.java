package com.github.jyoghurt.image.domain;

/**
 * Created by Administrator on 2015/9/9.
 */
public class ImageConfig {
    /**
     * 分类名称
     */
    private String moduleName;
    /**
     * 处理后的文件扩展名
     */
    private String extension = ".jpg";

    /**
     * 是否裁剪
     */
    private Boolean crop = false;
    /**
     * 最大宽度
     */
    private Integer width;
    /**
     * 最大高度
     */
    private Integer height;

    private String imgDesc;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Boolean getCrop() {
        return crop;
    }

    public void setCrop(Boolean crop) {
        this.crop = crop;
    }

    public String getModuleName() {
        return moduleName;
    }

    public ImageConfig setModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public String getExtension() {
        return extension;
    }

    public ImageConfig setExtension(String extension) {
        this.extension = extension;
        return this;
    }

    /**
     * 获取生成图片的实际宽度
     *
     * @param width 原始图片宽度
     * @return
     */
    public Integer getRealWidth(Integer width) {
        //高度等于null时，根据width信息计算出height信息，该信息只用于文件名存储
        if (this.height == null) {
            return new Float(new Float(this.width) / (new Float(width)) * new Float(height).intValue()).intValue();
        } else if (this.height > height) {
            return height;
        }
        return this.width;
    }


    /**
     * 获取生成图片的实际高度
     *
     * @param width  原始图片宽度
     * @param height 原始图片高度
     * @return
     */
    public Integer getRealHeight(Integer width, Integer height) {

        //高度等于null时，根据width信息计算出height信息，该信息只用于文件名存储
        if (this.height == null || this.height > height) {
            return height;
        }
        return this.height;

    }

    public String getImgDesc() {
        return imgDesc;
    }

    public ImageConfig setImgDesc(String imgDesc) {
        this.imgDesc = imgDesc;
        return this;
    }
}
