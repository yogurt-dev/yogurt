package com.github.jyoghurt.weChat.controller;


import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUnitT.service.SecurityUnitTService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.weChat.domain.WeChatSendMsgT;
import com.github.jyoghurt.weChat.service.WeChatSendMsgTService;
import com.github.jyoghurt.wechatbasic.common.util.WeiXinConstants;
import com.github.jyoghurt.wechatbasic.enums.WeChatAccountType;
import com.github.jyoghurt.wechatbasic.enums.WeChatMsgSendEnum;
import com.github.jyoghurt.wechatbasic.enums.WeChatMsgTypeEnum;
import com.github.jyoghurt.wechatbasic.exception.WeChatException;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pub.utils.SessionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 微信消息发送记录控制器
 */
@RestController
@LogContent(module = "微信消息发送记录")
@RequestMapping("/weChatSendMsgT")
public class WeChatSendMsgTController extends BaseController {


    /**
     * 微信消息发送记录服务类
     */
    @Resource
    private WeChatSendMsgTService weChatSendMsgTService;
    @Resource
    private SecurityUnitTService securityUnitTService;

    /**
     * 列出微信消息发送记录
     */
    @LogContent("查询微信消息发送记录")
    @RequestMapping(value = "/list/{weChatAccountType}", method = RequestMethod.GET)
    public HttpResultEntity<?> list(WeChatSendMsgT weChatSendMsgT, QueryHandle queryHandle,
                                    @PathVariable WeChatAccountType weChatAccountType, HttpServletRequest request)   {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT unitT = securityUnitTService.findSecretByUnitId(userT.getBelongCompany().getUnitId());
        String appId = "";
        switch (weChatAccountType) {
            case TYPE_SUBSCRIPTION: {
                appId = unitT.getAppId();
                break;
            }
            case TYPE_SERVICE: {
                appId = unitT.getAppIdF();
                break;
            }

            case TYPE_ENTERPRISE: {
                appId = unitT.getAppIdQ();
                break;
            }
        }
        weChatSendMsgT.setSend(WeChatMsgSendEnum.sent);
        weChatSendMsgT.setAppId(appId);
        return getSuccessResult(weChatSendMsgTService.getData(weChatSendMsgT, queryHandle.configPage().addOrderBy
                ("createDateTime",
                        "desc")));
    }

    /**
     * 添加微信消息发送记录
     */
    @LogContent("添加微信消息发送记录")
    @RequestMapping(value = "/{content}/{weChatAccountType}/{weChatMsgTypeEnum}", method = RequestMethod.POST)
    public HttpResultEntity<?> sendMsg(@PathVariable String content, @PathVariable WeChatAccountType
            weChatAccountType, @PathVariable WeChatMsgTypeEnum weChatMsgTypeEnum)   {
        SecurityUserT userT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT unitT = securityUnitTService.findSecretByUnitId(userT.getBelongCompany().getUnitId());
        try {
            return getSuccessResult(weChatSendMsgTService.sendAll(content, weChatAccountType, weChatMsgTypeEnum, unitT));
        } catch (WeChatException e) {
            HttpResultEntity<?> httpResultEntity = new HttpResultEntity<>();
            httpResultEntity.setErrorCode("0");
            httpResultEntity.setMessage(WeiXinConstants.weChatErrorMap.get(e.getErrorCode()) == null ? e.getErrorCode
                    () : WeiXinConstants.weChatErrorMap.get(e.getErrorCode()));
            return httpResultEntity;
        }
    }

    /**
     * 删除单个微信消息发送记录
     */
    @LogContent("删除微信消息发送记录")
    @RequestMapping(value = "/{Id}", method = RequestMethod.DELETE)
    public HttpResultEntity<?> delete(@PathVariable String Id)   {
        weChatSendMsgTService.delete(Id);
        return getSuccessResult();
    }
}
