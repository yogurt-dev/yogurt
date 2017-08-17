package com.github.jyoghurt.wechatbasic.common.pojo;

/**
 * Created by zhangjl on 2015/11/11.
 */
/*除图文消息外的素材实体*/
public class Material {
    private String media_id;
    private String name;
    private String update_time;
    private String url;

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

}
