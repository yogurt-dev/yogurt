package com.github.jyoghurt.weChat.service;

import com.github.jyoghurt.weChat.domain.WeChatRelevanceT;
import com.github.jyoghurt.core.service.BaseService;

import java.util.List;

/**
 * 对话窗口服务层
 *
 */
public interface RelevanceChatService extends BaseService<WeChatRelevanceT> {
    WeChatRelevanceT findByOpenId(String openId)  ;
    List<WeChatRelevanceT> findGroup(String appId, String appIdF, String appIdQ) ;
}
