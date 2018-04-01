package com.github.jyoghurt.wechatbasic.service.response.service;


import com.github.jyoghurt.core.service.BaseService;
import com.github.jyoghurt.wechatbasic.common.req.InputMessage;
import com.github.jyoghurt.wechatbasic.service.response.domain.WeChatAutoResponseMsgT;

/**
 * 微信统计服务层
 *
 */
public interface WeChatAutoResponseMsgService extends BaseService<WeChatAutoResponseMsgT> {

    String matchAutoResponse(InputMessage inputMsg);
}
