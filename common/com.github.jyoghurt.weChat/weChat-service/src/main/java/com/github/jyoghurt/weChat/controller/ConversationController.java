package com.github.jyoghurt.weChat.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUnitT.service.SecurityUnitTService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.weChat.domain.WeChatConversationT;
import com.github.jyoghurt.weChat.service.ConversationService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.web.bind.annotation.*;
import pub.utils.SessionUtils;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 对话窗口控制器
 */
@RestController
@LogContent(module = "对话窗口")
@RequestMapping("/conversation")
public class ConversationController extends BaseController {


    /**
     * 对话窗口服务类
     */
    @Resource
    private ConversationService conversationService;
    @Resource
    private SecurityUnitTService securityUnitTService;

    /**
     * 获取本人信息
     */
    @LogContent("获取本人信息")
    @RequestMapping(value = "/getMe", method = RequestMethod.GET)
    public HttpResultEntity<?> getMe()   {
        SecurityUserT securityUserT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        SecurityUnitT securityUnitT = securityUnitTService.findSecretByUnitId(securityUserT.getBelongCompany().getUnitId());
        /*为满足传参若appId为空则置为1*/
        if(securityUnitT.getAppId()==null||"".equals(securityUnitT.getAppId())){
            securityUnitT.setAppId("1");
        }
        if(securityUnitT.getAppIdF()==null||"".equals(securityUnitT.getAppIdF())){
            securityUnitT.setAppIdF("1");
        }
        if (securityUnitT.getAppIdQ()==null||"".equals(securityUnitT.getAppIdQ())){
            securityUnitT.setAppIdQ("1");
        }
        JSONObject returnObj=new JSONObject();
        returnObj.put("user",securityUserT);
        returnObj.put("unit",securityUnitT);
        return getSuccessResult(returnObj);
    }

    /**
     * 列出对话窗口
     */
    @LogContent("查询对话窗口")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public HttpResultEntity<?> list(WeChatConversationT weChatConversationT, QueryHandle queryHandle, Date expandTime)   {
        return getSuccessResult(conversationService.getData(weChatConversationT, queryHandle.configPage().addOrderBy("createDateTime",
                "desc").addWhereSql("unix_timestamp(createDateTime)<unix_timestamp(#{data.expandTime})")
                .addExpandData("expandTime", expandTime)));
    }

    /**
     * 窗口联系人数据加载
     */
    @LogContent("窗口联系人数据加载")
    @RequestMapping(value = "/initChat/{limitStart}/{limitEnd}/{appId}/{appIdF}/{appIdQ}", method = RequestMethod.GET)
    public HttpResultEntity<?> initChat(@PathVariable int limitStart, @PathVariable int limitEnd, @PathVariable String
            appId, @PathVariable String appIdF, @PathVariable String appIdQ)   {
        return getSuccessResult(conversationService.initConversation(limitStart, limitEnd, appId, appIdF, appIdQ));
    }

    /*历史联系人数据加载*/
    @LogContent("历史联系人数据加载")
    @RequestMapping(value = "/initHisChat/{limitStart}/{limitEnd}/{appId}", method = RequestMethod.GET)
    public HttpResultEntity<?> initHisChat(@PathVariable int limitStart, @PathVariable int limitEnd, @PathVariable
    String appId)   {
        return getSuccessResult(conversationService.initHisConversation(limitStart,
                limitEnd, appId));
    }

    /**
     * 添加对话窗口
     */
    @LogContent("添加对话窗口")
    @RequestMapping(method = RequestMethod.POST)
    public HttpResultEntity<?> add(@RequestBody WeChatConversationT weChatConversationT)   {
        conversationService.save(weChatConversationT);
        return getSuccessResult();
    }

    /**
     * 查询对话窗口
     */
    @LogContent("查询对话窗口")
    @RequestMapping(value = "/search/{type}/{appName}/{nickName}", method = RequestMethod.GET)
    public HttpResultEntity<?> searchConversation(@PathVariable String type,@PathVariable String appName,@PathVariable String nickName)   {
        return getSuccessResult(conversationService.searchConversation(type,appName,nickName));
    }
    /**
     * 刷新对话窗口
     */
    @LogContent("刷新对话窗口")
    @RequestMapping(value = "/refurbishChat/{type}/{appName}/{nickName}", method = RequestMethod.GET)
    public HttpResultEntity<?> refurbishChat(@PathVariable String type,@PathVariable String appName,@PathVariable String nickName)   {
        return getSuccessResult(conversationService.searchConversation(type,appName,nickName));
    }
    /**
     * 编辑对话窗口
     */
    @LogContent("编辑对话窗口")
    @RequestMapping(method = RequestMethod.PUT)
    public HttpResultEntity<?> edit(@RequestBody WeChatConversationT weChatConversationT)   {
        conversationService.updateForSelective(weChatConversationT);
        return getSuccessResult();
    }

    /**
     * 删除单个对话窗口
     */
    @LogContent("删除对话窗口")
    @RequestMapping(value = "/{Id}", method = RequestMethod.DELETE)
    public HttpResultEntity<?> delete(@PathVariable String Id)   {
        conversationService.delete(Id);
        return getSuccessResult();
    }


}
