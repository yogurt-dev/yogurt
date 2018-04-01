package com.github.jyoghurt.weChat.controller;

import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUnitT.service.SecurityUnitTService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.weChat.domain.WeChatAutoResponseT;
import com.github.jyoghurt.weChat.service.WeChatAutoResponseTService;
import com.github.jyoghurt.wechatbasic.enums.WeChatAccountType;
import com.github.jyoghurt.wechatbasic.enums.WeChatAutoResponseType;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pub.utils.SessionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 微信自动回复控制器
 */
@RestController
@LogContent(module = "微信自动回复")
@RequestMapping("/weChatAutoResponseT")
public class WeChatAutoResponseTController extends BaseController {


    /**
     * 微信自动回复服务类
     */
    @Resource
    private WeChatAutoResponseTService weChatAutoResponseTService;
    @Autowired
    private SecurityUnitTService securityUnitTService;

    @LogContent("添加自动回复规则")
    @RequestMapping(value = "/addAutoResponse/{weChatAccountType}", method = RequestMethod.POST)
    public HttpResultEntity<?> addAutoResponse(@RequestBody WeChatAutoResponseT weChatAutoResponseT, @PathVariable
            WeChatAccountType weChatAccountType, HttpServletRequest request)   {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT unitT = securityUnitTService.findSecretByUnitId(userT.getBelongCompany().getUnitId());
        return getSuccessResult(weChatAutoResponseTService.addAutoResponse(weChatAutoResponseT, weChatAccountType,
                unitT, request));
    }

    @LogContent("修改自动回复规则")
    @RequestMapping(value = "/editAutoResponse/{weChatAccountType}", method = RequestMethod.PUT)
    public HttpResultEntity<?> editAutoResponse(@RequestBody WeChatAutoResponseT weChatAutoResponseT, @PathVariable
    WeChatAccountType weChatAccountType, HttpServletRequest request)   {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT unitT = securityUnitTService.findSecretByUnitId(userT.getBelongCompany().getUnitId());
        return getSuccessResult(weChatAutoResponseTService.editAutoResponse(weChatAutoResponseT, weChatAccountType, unitT, request));
    }

    @LogContent("删除自动回复规则")
    @RequestMapping(value = "/delAutoResponse/{weChatAccountType}/{autoResponseId}", method = RequestMethod.DELETE)
    public HttpResultEntity<?> delAutoResponse(@PathVariable WeChatAccountType weChatAccountType, @PathVariable String autoResponseId)   {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT unitT = securityUnitTService.findSecretByUnitId(userT.getBelongCompany().getUnitId());
        return getSuccessResult(weChatAutoResponseTService.delAutoResponse(autoResponseId, weChatAccountType, unitT));
    }

    @LogContent("查询自动回复")
    @RequestMapping(value = "/findAutoResponse/{weChatAccountType}/{weChatAutoResponseType}", method = RequestMethod.GET)
    public HttpResultEntity<?> findAutoResponseList(@PathVariable WeChatAccountType weChatAccountType, @PathVariable WeChatAutoResponseType weChatAutoResponseType)   {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT unitT = securityUnitTService.findSecretByUnitId(userT.getBelongCompany().getUnitId());
        return getSuccessResult(weChatAutoResponseTService.findAutoResponseVoList(weChatAccountType, weChatAutoResponseType, unitT));
    }

}
