package com.github.jyoghurt.weChat.controller;


import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUnitT.service.SecurityUnitTService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.weChat.service.WeChatMediaTService;
import com.github.jyoghurt.wechatbasic.enums.WeChatAccountType;
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
import pub.utils.SessionUtils;

import javax.annotation.Resource;

/**
 * 微信本地媒体记录控制器
 */
@RestController
@LogContent(module = "微信本地媒体记录")
@RequestMapping("/weChatMediaT")
public class WeChatMediaTController extends BaseController {
    /**
     * 微信本地媒体记录服务类
     */
    @Resource
    private WeChatMediaTService weChatMediaTService;
    private static final Logger logger = LoggerFactory.getLogger(WeChatMediaTController.class);

    @Resource
    private SecurityUnitTService securityUnitTService;

    @LogContent("上传图片")
    @RequestMapping(value = "/uploadImg/{moduleName}/{weChatAccountType}", method = RequestMethod.POST)
    public HttpResultEntity<?> uploadImg(MultipartFile file, @PathVariable String moduleName, @PathVariable WeChatAccountType weChatAccountType) {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT unitT = securityUnitTService.findSecretByUnitId(userT.getBelongCompany().getUnitId());
        return weChatMediaTService.uploadImg(file, moduleName, weChatAccountType, unitT);
    }
}
