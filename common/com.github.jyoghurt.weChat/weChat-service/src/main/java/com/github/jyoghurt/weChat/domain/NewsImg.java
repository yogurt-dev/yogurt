package com.github.jyoghurt.weChat.domain;

/**
 * Created by chihang on 2015/9/18.
 */
public class NewsImg {
    private String Imgurl;
    private String thumUrl;
    public String getImgurl() {
        return Imgurl;
    }

    public void setImgurl(String imgurl) {
        Imgurl = imgurl;
    }

    public String getThumUrl() {
        return thumUrl;
    }

    public void setThumUrl(String thumUrl) {
        this.thumUrl = thumUrl;
    }
}
