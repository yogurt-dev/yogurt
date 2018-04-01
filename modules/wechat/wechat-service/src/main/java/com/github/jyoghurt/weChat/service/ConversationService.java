package com.github.jyoghurt.weChat.service;

import com.alibaba.fastjson.JSONObject;
import com.github.jyoghurt.weChat.domain.WeChatConversationT;
import com.github.jyoghurt.core.service.BaseService;

import java.util.List;

/**
 * 对话窗口服务层
 *
 */
public interface ConversationService extends BaseService<WeChatConversationT> {
    List<JSONObject> initConversation(int limitStart, int limitEnd, String
            appId, String appIdF, String appIdQ) ;
    List<JSONObject> initHisConversation(int limitStart, int limitEnd, String appId)  ;
    List<JSONObject>searchConversation(String type, String searchValue, String nickName)  ;
    List<JSONObject>refurbishChat(String type, String searchValue, String nickName)  ;
}
