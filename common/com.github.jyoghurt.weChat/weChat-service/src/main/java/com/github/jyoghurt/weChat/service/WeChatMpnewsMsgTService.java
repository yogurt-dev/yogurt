package com.github.jyoghurt.weChat.service;

import com.github.jyoghurt.weChat.domain.WeChatMpnewsMsgT;
import com.github.jyoghurt.core.service.BaseService;

import java.util.List;

/**
 * 微信消息记录表服务层
 *
 */
public interface WeChatMpnewsMsgTService extends BaseService<WeChatMpnewsMsgT> {

    public void deleteByMessageId(String messageId);
    public List<WeChatMpnewsMsgT> getListByMessageId(String messageId);

}
