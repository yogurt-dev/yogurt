package com.github.jyoghurt.weChat.controller;


import com.github.jyoghurt.security.securityUnitT.service.SecurityUnitTService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.weChat.domain.WeChatCustomMenusTVo;
import com.github.jyoghurt.weChat.service.CustomMenusTService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.web.bind.annotation.*;
import pub.utils.SessionUtils;

import javax.annotation.Resource;

/**
 * 自定义菜单控制器
 */
@RestController
@LogContent(module = "自定义菜单")
@RequestMapping("/customMenusT")
public class CustomMenusTController extends BaseController {
    /**
     * 自定义菜单服务类
     */
    @Resource
    private CustomMenusTService customMenusTService;

    @Resource
    private SecurityUnitTService securityUnitTService;
    /**
     * 列出自定义菜单
     */
    @LogContent("查询自定义菜单")
    @RequestMapping(value = "/getMenus/{appId}", method = RequestMethod.GET)
         public HttpResultEntity<?> list(@PathVariable String appId)   {
        return getSuccessResult(customMenusTService.getMenus(appId));

    }
    /**
     * 根据用户查询appId
     */
    @LogContent("根据用户查询appId")
    @RequestMapping(value = "/getApp/{appType}", method = RequestMethod.GET)
    public HttpResultEntity<?> getApp(@PathVariable String appType)   {
        SecurityUserT securityUserT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        return getSuccessResult(customMenusTService.getApp(securityUnitTService.findSecretByUnitId(securityUserT
                .getBelongCompany().getUnitId()),appType));
    }
    /**
     * 保存自定义菜单
     */
    @LogContent("保存自定义菜单")
    @RequestMapping(value = "/insertMenuList/{appId}", method = RequestMethod.POST)
    public HttpResultEntity<?> insertMenuList(@RequestBody WeChatCustomMenusTVo menuList, @PathVariable String appId)   {
        SecurityUserT securityUserT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        return getSuccessResult(customMenusTService.saveCustomMenusTList(menuList,securityUserT,appId));
    }
    /**
     * 删除自定义菜单
     */
    @LogContent("删除自定义菜单")
    @RequestMapping(value = "/deletMenuList/{appId}", method = RequestMethod.DELETE)
    public HttpResultEntity<?> deletMenuList(@RequestBody WeChatCustomMenusTVo menuList, @PathVariable String appId)   {
        return getSuccessResult(customMenusTService.deletCustomMenusTList(menuList,appId));
    }
    @LogContent("发布自定义菜单")
    @RequestMapping(value = "/saveAndSendMenuList/{appId}/{appsecret}", method = RequestMethod.POST)
    public HttpResultEntity<?> insertMenuList(@RequestBody WeChatCustomMenusTVo menuList, @PathVariable String appId, @PathVariable String appsecret)   {
        SecurityUserT securityUserT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        return getSuccessResult(customMenusTService.saveAndSendList(menuList,securityUserT,appId,appsecret));
    }

}
