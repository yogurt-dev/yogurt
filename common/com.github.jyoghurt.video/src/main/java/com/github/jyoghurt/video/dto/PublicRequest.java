package com.github.jyoghurt.video.dto;

/**
 * Created with IntelliJ IDEA.
 * User: DELL
 * Date: 2017/9/30
 * Time: 10:02
 * To change this template use File | Settings | File Templates.
 */
public class PublicRequest {

    public String accessKeyId;         //帐号AK
    public String accessKeySecret; //帐号AK
    public String fileName;      //指定上传文件绝对路径(文件名称必须包含扩展名)
    public String title;
    public String videoId;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
