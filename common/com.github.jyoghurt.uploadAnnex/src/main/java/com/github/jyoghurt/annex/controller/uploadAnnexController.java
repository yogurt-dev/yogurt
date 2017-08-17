package com.github.jyoghurt.annex.controller;

import com.github.jyoghurt.annex.domain.AnnexT;
import com.github.jyoghurt.annex.service.UploadAnnexService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;

/**
 * Created by dell on 2016/1/22.
 */
@RestController
@LogContent(module = "附件上传")
@RequestMapping("/uploadAnnex")
public class uploadAnnexController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(uploadAnnexController.class);
    @Resource
    private UploadAnnexService uploadAnnexService;

    @LogContent("上传图片")
    @RequestMapping(method = RequestMethod.POST)
    public HttpResultEntity<?> uploadAnnex(MultipartFile file, String entityClassName, String expand)   {
        try {
            return uploadAnnexService.uploadAnnex(file, entityClassName, expand);
        } catch (Exception e) {
            logger.error("上传文件失败", e);
            return getErrorResult("上传文件失败");
        }
    }

    @LogContent("查询附件列表")
    @RequestMapping(value = "{entityId}/{entityClassName}", method = RequestMethod.GET)
    public HttpResultEntity<?> uploadAnnex(@PathVariable String entityId, @PathVariable String entityClassName, String expand)   {
        try {
            AnnexT annexT = new AnnexT();
            annexT.setEntityId(entityId);
            annexT.setEntityName(entityClassName);
            annexT.setExpand(expand);
            return getSuccessResult(uploadAnnexService.findAll(annexT));
        } catch (Exception e) {
            logger.error("上传文件失败", e);
            return getErrorResult("上传文件失败");
        }
    }
}

