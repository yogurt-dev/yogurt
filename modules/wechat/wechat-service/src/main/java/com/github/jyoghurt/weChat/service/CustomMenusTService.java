package com.github.jyoghurt.weChat.service;

import com.alibaba.fastjson.JSONObject;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.weChat.domain.WeChatCustomMenusT;
import com.github.jyoghurt.weChat.domain.WeChatCustomMenusTVo;
import com.github.jyoghurt.core.service.BaseService;

import java.util.List;

/**
 * 自定义菜单服务层
 *
 */
public interface CustomMenusTService extends BaseService<WeChatCustomMenusT> {
        List<WeChatCustomMenusTVo> getMenus(String appId)  ;
        JSONObject getApp(SecurityUnitT securityUnitT, String appType)  ;
        List<WeChatCustomMenusTVo> saveCustomMenusTList(WeChatCustomMenusTVo customMenusList, SecurityUserT securityUserT, String appId)  ;
        List<WeChatCustomMenusTVo> deletCustomMenusTList(WeChatCustomMenusTVo customMenusList, String appId) ;
        JSONObject saveAndSendList(WeChatCustomMenusTVo customMenusList, SecurityUserT securityUserT,
                                   String appId, String appsecret)  ;
}