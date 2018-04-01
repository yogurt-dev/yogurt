package com.github.jyoghurt.wechatbasic.common.pojo;

/**
 * Created by zhangjl on 2015/11/11.
 */
public class MaterialCount {
    private String voice_count;/*语音总数量*/
    private String video_count;/*视频总数量*/
    private String image_count;/*图片总数量*/
    private String news_count;/*图文总数量*/

    public String getVoice_count() {
        return voice_count;
    }

    public void setVoice_count(String voice_count) {
        this.voice_count = voice_count;
    }

    public String getVideo_count() {
        return video_count;
    }

    public void setVideo_count(String video_count) {
        this.video_count = video_count;
    }

    public String getImage_count() {
        return image_count;
    }

    public void setImage_count(String image_count) {
        this.image_count = image_count;
    }

    public String getNews_count() {
        return news_count;
    }

    public void setNews_count(String news_count) {
        this.news_count = news_count;
    }
}
