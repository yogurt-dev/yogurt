package com.github.jyoghurt.image.service;

import com.github.jyoghurt.image.domain.ImageConfig;
import com.github.jyoghurt.image.exception.ImgException;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2015/4/2.
 * �ļ��ϴ��ӿ�
 */
public interface UploadImgService {
    /**
     * �ϴ�ͼƬ
     *
     * @param imageConfig ͼƬ����
     * @return
     * @
     */
    HttpResultEntity uploadImg(MultipartFile file, ImageConfig imageConfig) throws ImgException;

    HttpResultEntity uploadImgDb(String file, ImageConfig imageConfig) throws ImgException;

    String uploadImgByCkeditor(MultipartFile file, ImageConfig imageConfig, HttpServletRequest request,
                               HttpServletResponse response);


    /**
     * 返回图片路径
     *
     * @param file
     * @param imageConfig
     * @return
     * @
     */
    HttpResultEntity uploadMultipartImgDb(MultipartFile file, ImageConfig imageConfig);

    /**
     * 远程下载
     *
     * @param remoteFilePath 下载地址
     * @param moduleName     模块名称
     * @param fileName       文件名称
     * @return 本地路径
     */
    String downLoad(String remoteFilePath, String moduleName, String fileName);
}
