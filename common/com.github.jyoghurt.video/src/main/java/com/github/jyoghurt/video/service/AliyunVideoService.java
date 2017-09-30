package com.github.jyoghurt.video.service;

import com.aliyun.vod.upload.req.UploadVideoRequest;
import com.github.jyoghurt.video.dto.PlayInfoDTO;
import com.github.jyoghurt.video.dto.PublicRequest;

/**
 * Created with IntelliJ IDEA.
 * User: DELL
 * Date: 2017/9/30
 * Time: 11:45
 * To change this template use File | Settings | File Templates.
 */
public interface AliyunVideoService {
    /**
     * 上传视频至阿里云
     *
     * @param uploadRequest 上传参数
     * @return videoId
     */
    String upload(UploadVideoRequest uploadRequest);

    /**
     * 获取阿里云视频资料
     *
     * @param publicRequest 公共参数
     * @return 视频资料
     */
    PlayInfoDTO getPlayInfo(PublicRequest publicRequest);
}
