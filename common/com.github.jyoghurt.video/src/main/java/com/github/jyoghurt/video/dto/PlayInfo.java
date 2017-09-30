package com.github.jyoghurt.video.dto;

/**
 * Created with IntelliJ IDEA.
 * User: DELL
 * Date: 2017/9/29
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
public class PlayInfo {
    private String Bitrate;
    private String Definition;
    private String Duration;
    private String Encrypt;
    private String PlayURL;
    private String Format;
    private String Fps;
    private String Height;
    private String Size;
    private String Width;

    public String getBitrate() {
        return Bitrate;
    }

    public void setBitrate(String bitrate) {
        Bitrate = bitrate;
    }

    public String getDefinition() {
        return Definition;
    }

    public void setDefinition(String definition) {
        Definition = definition;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getEncrypt() {
        return Encrypt;
    }

    public void setEncrypt(String encrypt) {
        Encrypt = encrypt;
    }

    public String getPlayURL() {
        return PlayURL;
    }

    public void setPlayURL(String playURL) {
        PlayURL = playURL;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }

    public String getFps() {
        return Fps;
    }

    public void setFps(String fps) {
        Fps = fps;
    }

    public String getHeight() {
        return Height;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public String getWidth() {
        return Width;
    }

    public void setWidth(String width) {
        Width = width;
    }
}
