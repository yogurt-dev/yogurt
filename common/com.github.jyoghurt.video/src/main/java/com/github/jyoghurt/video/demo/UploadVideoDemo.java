package com.github.jyoghurt.video.demo;

import com.alibaba.fastjson.JSON;
import com.aliyun.signature.Signature;
import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadVideoRequest;
import com.aliyun.vod.upload.resp.UploadVideoResponse;
import com.github.jyoghurt.http.util.HttpClientUtils;
import com.github.jyoghurt.video.dto.PlayInfoDTO;
import com.github.jyoghurt.video.util.AliyunUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;


public class UploadVideoDemo {
    public static void main(String[] args) {
        String accessKeyId = "LTAILyJsdySZnaGX";         //帐号AK
        String accessKeySecret = "tGXOdHSFNVovdP4Rhkxf6MQPJhz3eq"; //帐号AK
        String fileName = "C:\\Users\\DELL\\Desktop\\VID20170817145748.mp4";      //指定上传文件绝对路径(文件名称必须包含扩展名)
        String title = "张剑林测试";                  //视频标题
        UploadVideoRequest request = new UploadVideoRequest(accessKeyId, accessKeySecret, title, fileName);
        request.setCateId(0);                                         //视频分类ID         //视频标签,多个用逗号分隔
        request.setDescription("视频描述");                           //视频描述
        request.setCoverURL("http://cover.sample.com/sample.jpg");    //视频自定义封面URL
        request.setPartSize(10 * 1024 * 1024L);     //可指定分片上传时每个分片的大小，默认为10M字节
        request.setTaskNum(1);                      //可指定分片上传时的并发线程数，默认为1，(注：该配置会占用服务器CPU资源，需根据服务器情况指定）
        request.setIsShowWaterMark(true);           //是否使用水印

        try {
            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadVideoResponse response = uploader.uploadVideo(request);
            System.out.print(response.getVideoId()); //上传成功后返回视频ID
            String url = "http://vod.cn-shanghai.aliyuncs.com?Action=GetPlayInfo&VideoId=8ee66e83ab114da48d0fc2ff71104607&Format=JSON";
            url = url + AliyunUtils.buildPublicParam("LTAILyJsdySZnaGX");
            HttpResponse response1 = HttpClientUtils.post(Signature.newBuilder()
                    .method("POST")
                    .url(url)
                    .secret("tGXOdHSFNVovdP4Rhkxf6MQPJhz3eq")
                    .build()
                    .compose(), null);
            String xmlStr = EntityUtils.toString(response1.getEntity(), "UTF-8");
            PlayInfoDTO playInfoDTO = JSON.parseObject(xmlStr, PlayInfoDTO.class);
            System.out.print("123");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print(e.getCause());
            System.out.print(e.getMessage());
            return;
        }
    }

}
