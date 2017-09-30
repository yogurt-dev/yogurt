package com.github.jyoghurt.video.dto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: DELL
 * Date: 2017/9/29
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class PlayInfoList {
    List<PlayInfo> PlayInfo;

    public List<com.github.jyoghurt.video.dto.PlayInfo> getPlayInfo() {
        return PlayInfo;
    }

    public void setPlayInfo(List<com.github.jyoghurt.video.dto.PlayInfo> playInfo) {
        PlayInfo = playInfo;
    }
}
