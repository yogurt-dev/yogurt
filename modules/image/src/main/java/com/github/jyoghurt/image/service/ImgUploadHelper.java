package com.github.jyoghurt.image.service;


import com.github.jyoghurt.image.domain.ImageConfig;
import com.github.jyoghurt.image.exception.ImgException;
import com.github.jyoghurt.core.exception.BaseErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ImgUploadHelper {
    public static final Logger logger = LoggerFactory.getLogger(ImgUploadHelper.class);
    /**
     * 上传图片
     *
     * @param imageConfig
     * @return 上传图片路径
     * @throws IOException
     */
    public static String upload(String uploadPath, String downloadPath,
                                ImageConfig imageConfig, String fileName, InputStream inputStream) {
        return upload(uploadPath, downloadPath, imageConfig, fileName, inputStream,null);
    }
    /**
     * 上传图片
     *
     * @param imageConfig
     * @return 上传图片路径
     * @throws IOException
     */
    public static String upload(String uploadPath, String downloadPath,
                                ImageConfig imageConfig, String fileName, InputStream inputStream,String newFileName) {
        try {
            String imageUrl = "";
            logger.info("上传图片名称==>>" + fileName);
            if (StringUtils.isEmpty(fileName)) {
                return null;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String dirPath = dateFormat.format(new Date());
            String path = StringUtils.join(uploadPath, "/", imageConfig.getModuleName(), "/", StringUtils.isEmpty(newFileName)?dirPath:null);

            File gallerySaveDir = new File(path);
            if (!gallerySaveDir.exists()) {
                gallerySaveDir.mkdirs();// 如果目录不存在就创建
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            File uploadedFile =null;
            String ext = fileName.substring(fileName.lastIndexOf('.'));
            if(StringUtils.isNotEmpty(newFileName)){
                uploadedFile = new File(gallerySaveDir, newFileName + "_original");
            }else{
                newFileName = df.format(new Date()) + "_" + new Random().nextInt(100);

                uploadedFile = new File(gallerySaveDir, newFileName + "_original" + ext);
            }


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;

            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }

            byteArrayOutputStream.flush();
            //保存原图
            FileCopyUtils.copy(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), new FileOutputStream(uploadedFile));
            logger.info("上传图片路径==>>" + path);
            if (null == imageConfig || imageConfig.getWidth() == null) {
                ext = StringUtils.isEmpty(imageConfig.getExtension()) ? ext : imageConfig.getExtension();
                FileCopyUtils.copy(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), new FileOutputStream
                        (new File(gallerySaveDir, newFileName + ext)));
                return StringUtils.join(downloadPath, "/", imageConfig.getModuleName(), "/", dirPath, "/", newFileName,
                        ext);
            }
            BufferedImage read = ImageIO.read(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            newFileName = ImageTools.cutImage(imageConfig, read, path, newFileName + ext);
            imageUrl = StringUtils.join(downloadPath, "/", imageConfig.getModuleName(), "/", dirPath, "/", newFileName + ext);
            return imageUrl;
        } catch (IOException e) {
            throw new BaseErrorException(ImgException.ERROR_9002,e);
        }
    }



    /**
     * 将base64字符解码 保存到文件系统
     *
     * @param base64Code
     * @param
     * @
     */
    public static String decoderBase64File(String base64Code, String uploadPath, String downloadPath, ImageConfig imageConfig) {
        try {
            //生成上传路径
            base64Code = base64Code.substring(base64Code.indexOf(",") + 1, base64Code.length());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String dirPath = dateFormat.format(new Date());
            String path = StringUtils.join(uploadPath, "/", imageConfig.getModuleName(), "/", dirPath);

            File gallerySaveDir = new File(path);
            if (!gallerySaveDir.exists()) {
                gallerySaveDir.mkdirs();// 如果目录不存在就创建
            }

            //生成上传文件
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String newFileName = df.format(new Date()) + "_" + new Random().nextInt(10000);
            File uploadedFile = new File(gallerySaveDir, newFileName + "_original." + imageConfig.getExtension());

            BASE64Decoder decoder = new BASE64Decoder();
            byte[] b = new byte[0];

            b = decoder.decodeBuffer(base64Code);

            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(uploadedFile);
            out.write(b);
            out.flush();
            out.close();

            return StringUtils.join(downloadPath, "/", imageConfig.getModuleName(), "/", dirPath, "/", uploadedFile.getName());
        } catch (IOException e) {
            throw new BaseErrorException(ImgException.ERROR_9002,e);
        }

    }


    /**
     * 删除原来图片
     *
     * @param downloadPath
     * @param logger
     */
    public static void deletePic(String oldImageUrl, String uploadPath, String downloadPath, Logger logger) {
        //删除原来上传的图片
        if (oldImageUrl != null && !"".equals(oldImageUrl)) {
            oldImageUrl = oldImageUrl.replaceFirst(downloadPath, "");
            String imageUrl = uploadPath + oldImageUrl;
            File oldFile = new File(imageUrl);
            if (oldFile.exists()) {
                oldFile.delete();
                logger.info("[]删除原来图片==>>" + imageUrl);
            }
        }
    }
}
