package com.github.jyoghurt.weChat.service;


import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.weChat.domain.WeChatSendMsgT;
import com.github.jyoghurt.wechatbasic.enums.WeChatAccountType;
import com.github.jyoghurt.wechatbasic.enums.WeChatMsgTypeEnum;
import com.github.jyoghurt.wechatbasic.exception.WeChatException;
import com.github.jyoghurt.core.service.BaseService;

/**
 * 微信消息发送记录服务层
 */
public interface WeChatSendMsgTService extends BaseService<WeChatSendMsgT> {
    public String sendAll(String content, WeChatAccountType weChatAccountType, WeChatMsgTypeEnum weChatMsgTypeEnum,
                          SecurityUnitT unitT) throws  WeChatException;
}
