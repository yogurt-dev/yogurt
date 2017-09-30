package com.github.jyoghurt.video.service;

import com.aliyun.vod.upload.req.UploadVideoRequest;
import com.github.jyoghurt.video.dto.PlayInfoDTO;
import com.github.jyoghurt.video.dto.PublicRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created with IntelliJ IDEA.
 * User: DELL
 * Date: 2017/9/30
 * Time: 9:44
 * To change this template use File | Settings | File Templates.
 */
public interface UploadVideoService {
    /**
     * 上传视频至本地
     *
     * @param file 文件流
     * @return videoId
     */
    String upload(MultipartFile file);


}
