package com.github.jyoghurt.video.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.signature.Signature;
import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadVideoRequest;
import com.aliyun.vod.upload.resp.UploadVideoResponse;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.result.HttpResultHandle;
import com.github.jyoghurt.http.util.HttpClientUtils;
import com.github.jyoghurt.video.dto.PlayInfoDTO;
import com.github.jyoghurt.video.dto.PublicRequest;
import com.github.jyoghurt.video.service.UploadVideoService;
import com.github.jyoghurt.video.util.AliyunUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: DELL
 * Date: 2017/9/30
 * Time: 9:46
 * To change this template use File | Settings | File Templates.
 */
@Service("uploadVideoService")
public class UploadVideoServiceImpl implements UploadVideoService {
    @Value("${uploadPath}")
    private String uploadPath;

    @Value("${downloadPath}")
    private String downloadPath;
    @Value("${videoMaxSize}")
    private Integer videoMaxSize;

    @Override
    public String upload(MultipartFile file) {
        if (file.getSize() > Long.valueOf(videoMaxSize)) {
            throw new BaseErrorException("上传文件不能超过"+videoMaxSize/1024/1024+"M");
        }
        //获取文件名称
        String fileName = file.getOriginalFilename();
        //生成文件路径
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dirPath = dateFormat.format(new Date());
        String path = StringUtils.join(uploadPath, File.separator, "video", File.separator, dirPath, File.separator);
        String downLoad = StringUtils.join(downloadPath, "/video/", dirPath, "/");
        File gallerySaveDir = new File(path);
        if (!gallerySaveDir.exists()) {
            gallerySaveDir.mkdirs();// 如果目录不存在就创建
        }
        try {
            InputStream inputStream = file.getInputStream();
            OutputStream bos = new FileOutputStream(path + fileName);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.close();
            inputStream.close();
        } catch (IOException e) {
            throw new BaseErrorException("上传视频IO异常", e);
        }
        return downLoad+fileName;
    }
}
