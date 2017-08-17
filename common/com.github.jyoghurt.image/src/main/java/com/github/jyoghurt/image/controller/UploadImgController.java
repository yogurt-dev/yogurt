package com.github.jyoghurt.image.controller;

import com.github.jyoghurt.image.domain.ImageConfig;
import com.github.jyoghurt.image.domain.ImgT;
import com.github.jyoghurt.image.exception.ImgException;
import com.github.jyoghurt.image.service.UploadImgService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * Created by jtwu on 2015/9/22.
 * 图片保存接口
 */
@RestController()
@RequestMapping("/uploadImg")
public class UploadImgController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(UploadImgController.class);
    @Resource
    private UploadImgService uploadImgService;

    @LogContent("上传图片")
    @RequestMapping(value = "/{moduleName}", method = RequestMethod.POST)
    public HttpResultEntity<?> uploadImg(MultipartFile file, @PathVariable String moduleName) {
        try {
            return uploadImgService.uploadImg(file, new ImageConfig().setModuleName(moduleName));
        } catch (Exception e) {
            logger.error("上传文件失败", e);
            return getErrorResult("上传文件失败");
        }
    }

    @LogContent("上传图片")
    @RequestMapping(value = "/{moduleName}/{extension}", method = RequestMethod.POST)
    public HttpResultEntity<?> uploadImg(MultipartFile file, @PathVariable String moduleName
            , @PathVariable String extension) {
        try {
            return uploadImgService.uploadImg(file, new ImageConfig().setModuleName(moduleName).setExtension(extension));
        } catch (Exception e) {
            logger.error("上传文件失败", e);
            return getErrorResult("上传文件失败");
        }
    }


    @LogContent("上传图片入库，描述改造后")
    @RequestMapping(value = "/uploadImgDb/{moduleName}/{extension}", method = RequestMethod.POST)
    public HttpResultEntity<?> uploadImgDb(@RequestBody ImgT file, @PathVariable String moduleName
            , @PathVariable String extension) throws ImgException {
        String fileContent = file.getImgContent().substring(5, file.getImgContent().length());
        HttpResultEntity result = uploadImgService.uploadImgDb(fileContent, new ImageConfig().setModuleName
                (moduleName).setExtension(extension).setImgDesc(file.getImgDesc()));
        return result;
    }

    @LogContent("上传图片入库")
    @RequestMapping(value = "/uploadImgDb/{moduleName}/{imgDesc}/{extension}", method = RequestMethod.POST)
    public HttpResultEntity<?> uploadImgDb(@RequestBody String file, @PathVariable String moduleName
            , @PathVariable String extension, @PathVariable String imgDesc) throws UnsupportedEncodingException, ImgException {
        file = java.net.URLDecoder.decode(file.substring(5, file.length()), "utf-8");
        HttpResultEntity result = uploadImgService.uploadImgDb(file, new ImageConfig().setModuleName(moduleName).setExtension(extension).setImgDesc(imgDesc));
        return result;
    }

    @LogContent("MultipartFile方式入库")
    @RequestMapping(value = "/uploadMultipartImgDb/{moduleName}", method = RequestMethod.POST)
    public HttpResultEntity<?> uploadMultipartImgDb(MultipartFile file, @PathVariable String moduleName) {
        return uploadImgService.uploadMultipartImgDb(file, new ImageConfig().setModuleName(moduleName));
    }


    @LogContent("上传CKeditor图片")
    @RequestMapping(value = "/ckeditor/{moduleName}", method = RequestMethod.POST)
    public String uploadImgByCkeditor(MultipartFile upload, @PathVariable String moduleName, HttpServletRequest request,
                                      HttpServletResponse response) {
        try {
            return uploadImgService.uploadImgByCkeditor(upload, new ImageConfig().setModuleName(moduleName), request,
                    response);
        } catch (Exception e) {
            logger.error("上传文件失败", e);
            return "上传文件失败";
        }
    }
}
