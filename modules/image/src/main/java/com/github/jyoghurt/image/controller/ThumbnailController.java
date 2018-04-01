package com.github.jyoghurt.image.controller;

import com.github.jyoghurt.image.service.ImageTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * Created by Administrator on 2015/3/11.
 */
@RestController
public class ThumbnailController {
    Logger logger = Logger.getLogger(ThumbnailController.class);
    @Value("${uploadPath}")
    private String uploadPath;
    @Value("${downloadPath}")
    private String downloadPath;

    @RequestMapping("/getThumbnail/{filePath}")
    public void getThumbnail(@PathVariable String filePath, HttpServletRequest request,HttpServletResponse
            response) {
        filePath = filePath.replace(downloadPath,"");
        int width = Integer.valueOf(filePath.substring(filePath.lastIndexOf("_")+1, filePath.lastIndexOf("x")));
        String heightString = filePath.substring(filePath.lastIndexOf("x") + 1, filePath.lastIndexOf("."));
        Integer height = null;
        if(StringUtils.isNotEmpty(heightString)){
            height  = Integer.valueOf(heightString);
        }

        //获取默认图片路径
        String exp = filePath.substring(filePath.lastIndexOf("."));
        String defaultImagePath = filePath.substring(0,filePath.lastIndexOf("_")) + exp;
        //超过1024返回默认配置
        if(1280<=width||(null!=height&&1280<=height)){
            returnDefImg(request, response, defaultImagePath);
            return;
        }

        File f = new File(request.getServletContext().getRealPath("")+filePath.substring(0,filePath.lastIndexOf
                ("/")));
        if (f.getParentFile() != null && !f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        f.mkdirs();
        String destImagePath = uploadPath + filePath;
        try {
            ImageTools.cutImage(width, height, uploadPath + defaultImagePath, destImagePath);
        } catch (Exception e) {
            logger.error("图片压缩失败",e);
            returnDefImg(request, response, defaultImagePath);
            return;
        }

        returnDefImg(request,response,filePath);
    }

    private void returnDefImg(HttpServletRequest request, HttpServletResponse response, String defaultImagePath) {
        try {
            response.sendRedirect(StringUtils.join(downloadPath, defaultImagePath));
//            request.getRequestDispatcher(GetProp.prop("imageRequestPath")+defaultImagePath).forward(request, response);
        } catch (Exception e) {
            logger.error("获取缩略图失败",e);
        }
    }

}
