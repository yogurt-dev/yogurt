package com.github.jyoghurt.video.dto;

/**
 * Created with IntelliJ IDEA.
 * User: DELL
 * Date: 2017/9/30
 * Time: 11:29
 * To change this template use File | Settings | File Templates.
 */
public class PlayInfoDTO {
    private String Code;

    private String Message;

    private String RequestId;

    private VideoBase VideoBase;

    private PlayInfoList PlayInfoList;

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public com.github.jyoghurt.video.dto.VideoBase getVideoBase() {
        return VideoBase;
    }

    public void setVideoBase(com.github.jyoghurt.video.dto.VideoBase videoBase) {
        VideoBase = videoBase;
    }

    public com.github.jyoghurt.video.dto.PlayInfoList getPlayInfoList() {
        return PlayInfoList;
    }

    public void setPlayInfoList(com.github.jyoghurt.video.dto.PlayInfoList playInfoList) {
        PlayInfoList = playInfoList;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
