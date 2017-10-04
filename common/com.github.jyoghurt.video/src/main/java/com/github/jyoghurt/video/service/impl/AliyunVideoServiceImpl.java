package com.github.jyoghurt.video.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.signature.Signature;
import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadVideoRequest;
import com.aliyun.vod.upload.resp.UploadVideoResponse;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.http.util.HttpClientUtils;
import com.github.jyoghurt.video.dto.PlayInfoDTO;
import com.github.jyoghurt.video.dto.PublicRequest;
import com.github.jyoghurt.video.service.AliyunVideoService;
import com.github.jyoghurt.video.util.AliyunUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: DELL
 * Date: 2017/9/30
 * Time: 9:46
 * To change this template use File | Settings | File Templates.
 */
@Service("aiyunVideoService")
public class AliyunVideoServiceImpl implements AliyunVideoService {


    @Override
    public String upload(UploadVideoRequest uploadRequest) {
        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadVideoResponse response = uploader.uploadVideo(uploadRequest);
        if ("0" != response.getCode()) {
            throw new BaseErrorException("上传视频至阿里云失败,失败编码:{}", response.getCode());
        }
        return response.getVideoId();
    }

    @Override
    public PlayInfoDTO getPlayInfo(PublicRequest publicRequest) {
        for(int i=0;i<10;i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                return getInfo(publicRequest);
            }catch (Exception e){
                if(i==9){
                    throw new BaseErrorException(e);
                }
            }
        }
        throw new BaseErrorException("阿里云视频上传异常");
    }

    @Override
    public void deleteVideo(PublicRequest publicRequest) {
        String url = "http://vod.cn-shanghai.aliyuncs.com?Action=DeleteVideo&VideoIds=" + publicRequest.getVideoId() + "&Format=JSON";
        url = url + AliyunUtils.buildPublicParam(publicRequest.getAccessKeyId());
        HttpResponse response1 = HttpClientUtils.post(Signature.newBuilder()
                .method("POST")
                .url(url)
                .secret(publicRequest.getAccessKeySecret())
                .build()
                .compose(), null);
        try {
            String xmlStr = EntityUtils.toString(response1.getEntity(), "UTF-8");
            System.out.println(xmlStr);
            JSONObject json = JSONObject.fromObject(xmlStr);
            String code = (String) json.get("Code");
            System.out.println(code);
        } catch (IOException e) {
            throw new BaseErrorException("获取阿里云视频信息IO异常", e);
        }
    }

    private PlayInfoDTO getInfo(PublicRequest publicRequest) {
        String url = "http://vod.cn-shanghai.aliyuncs.com?Action=GetPlayInfo&VideoId=" + publicRequest.getVideoId() + "&Format=JSON";
        url = url + AliyunUtils.buildPublicParam(publicRequest.getAccessKeyId());
        HttpResponse response1 = HttpClientUtils.post(Signature.newBuilder()
                .method("POST")
                .url(url)
                .secret(publicRequest.getAccessKeySecret())
                .build()
                .compose(), null);
        try {
            String xmlStr = EntityUtils.toString(response1.getEntity(), "UTF-8");
            PlayInfoDTO playInfoDTO = JSON.parseObject(xmlStr, PlayInfoDTO.class);
            if (StringUtils.isNotEmpty(playInfoDTO.getMessage())) {
                throw new BaseErrorException("阿里云视频上传异常:" + playInfoDTO.getMessage());
            }
            return playInfoDTO;
        } catch (IOException e) {
            throw new BaseErrorException("获取阿里云视频信息IO异常", e);
        }
    }
}
