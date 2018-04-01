package com.github.jyoghurt.wechatbasic.common.pojo;

/**
 * Created by zhangjl on 2015/11/11.
 */
/*修改永久图文素材实体*/
public class UpdateMaterialNewsMap {
    private String media_id;
    private int index;
    Articles articles;

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Articles getArticles() {
        return articles;
    }

    public void setArticles(Articles articles) {
        this.articles = articles;
    }
}
