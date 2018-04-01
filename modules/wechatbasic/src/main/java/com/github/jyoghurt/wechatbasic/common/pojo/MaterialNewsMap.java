package com.github.jyoghurt.wechatbasic.common.pojo;

/**
 * Created by zhangjl on 2015/11/11.
 */
/*用于接收图文消息素材列表*/
public class MaterialNewsMap {
    private String media_id;
    private MaterialNewsContent content;
    private String update_time;

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public MaterialNewsContent getContent() {
        return content;
    }

    public void setContent(MaterialNewsContent content) {
        this.content = content;
    }

}
