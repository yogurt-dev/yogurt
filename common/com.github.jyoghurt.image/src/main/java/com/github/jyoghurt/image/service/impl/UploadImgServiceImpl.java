package com.github.jyoghurt.image.service.impl;


import com.github.jyoghurt.image.domain.ImageConfig;
import com.github.jyoghurt.image.domain.ImgT;
import com.github.jyoghurt.image.enums.ImgExceptionEnums;
import com.github.jyoghurt.image.exception.ImgException;
import com.github.jyoghurt.image.service.ImgService;
import com.github.jyoghurt.image.service.ImgUploadHelper;
import com.github.jyoghurt.image.service.UploadImgService;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.result.HttpResultHandle;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by Administrator on 2015/4/2.
 */
@Service()
public class UploadImgServiceImpl implements UploadImgService {

    @Value("${uploadPath}")
    private String uploadPath;

    @Value("${downloadPath}")
    private String downloadPath;

    @Autowired
    private ImgService imgService;

    //图片大小限制为2M
    private static final long MAXSIZE = 1000000;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public HttpResultEntity uploadImg(MultipartFile file, ImageConfig imageConfig) throws ImgException {
        String suffix = "gif,jpg,jpeg,png,bmp";
        String fileName = file.getOriginalFilename();
        if (file.getSize() > MAXSIZE) {
            throw new ImgException(ImgException.ERROR_9001);
        }

        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (!Arrays.asList(suffix.split(",")).contains(fileExt)) {
            throw new ImgException(ImgException.ERROR_9003);
        }
        try {
            return HttpResultHandle.getSuccessResult(ImgUploadHelper.upload(uploadPath, downloadPath, imageConfig, fileName, file.getInputStream()));
        } catch (IOException e) {
            throw new ImgException(ImgException.ERROR_9002, e);
        }
    }

    /**
     * 上传图片，存储到文件系统中
     *
     * @param fileName 文件名称
     * @return 图片ID
     * @
     * @author baoxiaobing@lvyushequ.com
     */
    private ImgT uploadImgDataBase(String moduleName, String fileName, String imgPath, String imgDesc) {
        ImgT imgT = new ImgT();
        imgT.setName(fileName);
        imgT.setPath(imgPath);
        imgT.setImgDesc(imgDesc);
        imgT.setType(moduleName);
        imgService.save(imgT);
        return imgT;
    }

    /**
     * 上传图片，存储到文件系统中
     *
     * @param file        文件
     * @param imageConfig 文件配置
     * @param fileName    文件名称
     * @
     * @author baoxiaobing@lvyushequ.com
     */
    private String uploadImgFileSystem(MultipartFile file, ImageConfig imageConfig, String fileName) {
        try {
            return ImgUploadHelper.upload(uploadPath, downloadPath, imageConfig, fileName, file.getInputStream());
        } catch (IOException e) {
            throw new BaseErrorException(ImgExceptionEnums.ERROR_9002.getMessage(), e);
        }
    }


    @Override
    public String uploadImgByCkeditor(MultipartFile file, ImageConfig imageConfig, HttpServletRequest request,
                                      HttpServletResponse response) {
        try {
            String suffix = "gif,jpg,jpeg,png,bmp";
            String fileName = file.getOriginalFilename();
            if (file.getSize() > MAXSIZE) {
                return "文件过大";
            }
            String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if (!Arrays.asList(suffix.split(","))
                    .contains(fileExt)) {
                return "上传文件失败";
            }
            PrintWriter out = response.getWriter();

            String callback = request.getParameter("CKEditorFuncNum");
            out.println("<script type=\"text/javascript\">");
            out.println("window.parent.CKEDITOR.tools.callFunction(" + callback
                    + ",'http://" + request.getHeader("Host") + ImgUploadHelper
                    .upload(uploadPath,
                            downloadPath, imageConfig,
                            fileName, file.getInputStream()) + "','')");
            out.println("</script>");
            return null;
        } catch (IOException e) {
            throw new BaseErrorException(ImgExceptionEnums.ERROR_9002.getMessage(), e);
        }
    }

    public HttpResultEntity uploadImgDb(String file, ImageConfig imageConfig) throws ImgException {
        String suffix = "gif,jpg,jpeg,png,bmp";
        if (file.length() > MAXSIZE) {
            throw new ImgException(ImgException.ERROR_9001);
        }

        String fileExt = imageConfig.getExtension();
        if (!Arrays.asList(suffix.split(",")).contains(fileExt)) {
            throw new BaseErrorException(ImgException.ERROR_9003.getMessage());
        }
        String imgPath = ImgUploadHelper.decoderBase64File(file, uploadPath, downloadPath, imageConfig);
        return HttpResultHandle.getSuccessResult(uploadImgDataBase(imageConfig.getModuleName(), imageConfig.getImgDesc(), imgPath, imageConfig.getImgDesc()));
    }

    @Override
    public HttpResultEntity uploadMultipartImgDb(MultipartFile file, ImageConfig imageConfig) {

        JSONObject result = new JSONObject();

        String suffix = "gif,jpg,jpeg,png,bmp";
        String fileName = file.getOriginalFilename();
        if (file.getSize() > MAXSIZE) {
            throw new BaseErrorException("上传图片不能超过1M");
        }

        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (!Arrays.asList(suffix.split(",")).contains(fileExt)) {
            return HttpResultHandle.getErrorResult("系统不支持此类型图片");
        }
        //设置返回路径
        String path = null;

        try {
            path = ImgUploadHelper.upload(uploadPath, downloadPath, imageConfig, fileName, file.getInputStream());
        } catch (IOException e) {
            throw new BaseErrorException(ImgException.ERROR_9002, e);
        }
        ImgT imgT = new ImgT();
        imgT.setPath(path);
        imgT.setType(imageConfig.getModuleName());
        imgT.setName(file.getName());
        imgService.save(imgT);
        result.put("imgId", imgT.getImgId());
        result.put("imgPath", path);
        return HttpResultHandle.getSuccessResult(result);
    }


    @Override
    public String downLoad(String remoteFilePath, String moduleName, String fileName) {
        try {
            ImageConfig imageConfig = new ImageConfig();
            URL urlfile = new URL(remoteFilePath);
            HttpURLConnection httpUrl = (HttpURLConnection) urlfile.openConnection();
            httpUrl.connect();
            return ImgUploadHelper.upload(uploadPath, downloadPath, imageConfig, fileName, httpUrl.getInputStream());
        } catch (Exception e) {
            throw new BaseErrorException("下载微信图片失败", e);
        }
    }
}
