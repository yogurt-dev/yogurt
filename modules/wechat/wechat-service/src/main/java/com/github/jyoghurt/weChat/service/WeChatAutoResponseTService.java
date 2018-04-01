package com.github.jyoghurt.weChat.service;

import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.weChat.domain.WeChatAutoResponseT;
import com.github.jyoghurt.weChat.domain.WeChatAutoResponseTVo;
import com.github.jyoghurt.wechatbasic.enums.WeChatAccountType;
import com.github.jyoghurt.wechatbasic.enums.WeChatAutoResponseType;
import com.github.jyoghurt.core.service.BaseService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 微信自动回复服务层
 *
 */
public interface WeChatAutoResponseTService extends BaseService<WeChatAutoResponseT> {
     WeChatAutoResponseT addAutoResponse(WeChatAutoResponseT weChatAutoResponseT,
                                         WeChatAccountType weChatAccountType, SecurityUnitT unitT, HttpServletRequest request);

     WeChatAutoResponseT editAutoResponse(WeChatAutoResponseT weChatAutoResponseT, WeChatAccountType weChatAccountType, SecurityUnitT unitT, HttpServletRequest request);

     WeChatAutoResponseT delAutoResponse(String autoResponseId,
                                         WeChatAccountType weChatAccountType, SecurityUnitT unitT);

     List<WeChatAutoResponseT> findAutoResponseList(String appId, WeChatAutoResponseType
             weChatAutoResponseType);

     List<WeChatAutoResponseTVo> findAutoResponseVoList(WeChatAccountType weChatAccountType, WeChatAutoResponseType weChatAutoResponseType, SecurityUnitT unitT);

}
