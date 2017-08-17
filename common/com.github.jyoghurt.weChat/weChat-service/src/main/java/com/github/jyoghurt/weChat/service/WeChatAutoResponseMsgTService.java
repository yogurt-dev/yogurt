package com.github.jyoghurt.weChat.service;

import com.github.jyoghurt.weChat.domain.WeChatAutoResponseMsgT;
import com.github.jyoghurt.wechatbasic.exception.WeChatException;
import com.github.jyoghurt.core.service.BaseService;

import java.util.List;

/**
 * 微信自动回复消息服务层
 *
 */
public interface WeChatAutoResponseMsgTService extends BaseService<WeChatAutoResponseMsgT> {
    public List<WeChatAutoResponseMsgT> findAutoResponseMsgByParent(String AutoResponseId) throws WeChatException;
}
