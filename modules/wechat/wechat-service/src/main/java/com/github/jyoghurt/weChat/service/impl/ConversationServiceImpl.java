package com.github.jyoghurt.weChat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.jyoghurt.weChat.dao.ConversationMapper;
import com.github.jyoghurt.weChat.domain.WeChatConversationT;
import com.github.jyoghurt.weChat.service.ConversationService;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service("conversationService")
public class ConversationServiceImpl extends ServiceSupport<WeChatConversationT, ConversationMapper> implements ConversationService {
    @Autowired
    private ConversationMapper conversationMapper;

    @Override
    public ConversationMapper getMapper() {
        return conversationMapper;
    }

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(WeChatConversationT.class, id);
    }

    @Override
    public WeChatConversationT find(Serializable id)  {
        return getMapper().selectById(WeChatConversationT.class, id);
    }

    @Override
    public List<JSONObject> initConversation(int limitStart, int limitEnd, String
            appId, String appIdF, String appIdQ)  {
        return conversationMapper.initConversation(limitStart, limitEnd, appId, appIdF, appIdQ);
    }

    @Override
    public List<JSONObject> initHisConversation(int limitStart, int limitEnd, String appId)   {
        return conversationMapper.initHisConversation(limitStart, limitEnd, appId);
    }

    @Override
    public List<JSONObject> searchConversation(String type, String appName,String nickName)   {
        appName="null".equals(appName)?"":appName;
        nickName="null".equals(nickName)?"":nickName;
        if ("online".equals(type)) {
            return conversationMapper.initSearch(appName, nickName);
        } else {
            return conversationMapper.initSearchHis(appName, nickName);
        }
    }
    @Override
    public List<JSONObject> refurbishChat(String type, String appName,String nickName)   {
        appName="null".equals(appName)?"":appName;
        nickName="null".equals(nickName)?"":nickName;
            return conversationMapper.refurbishChat(appName, nickName);
    }
}
