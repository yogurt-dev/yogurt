package com.github.jyoghurt.weChat.service.impl;

import com.github.jyoghurt.weChat.dao.RelevanceChatMapper;
import com.github.jyoghurt.weChat.domain.WeChatRelevanceT;
import com.github.jyoghurt.weChat.service.RelevanceChatService;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service("relevanceChatService")
public class RelevanceChatServiceImpl extends ServiceSupport<WeChatRelevanceT, RelevanceChatMapper> implements RelevanceChatService {
    @Autowired
    private RelevanceChatMapper relevanceChatMapper;

    @Override
    public RelevanceChatMapper getMapper() {
        return relevanceChatMapper;
    }

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(WeChatRelevanceT.class, id);
    }

    @Override
    public WeChatRelevanceT find(Serializable id)  {
        return getMapper().selectById(WeChatRelevanceT.class, id);
    }

    @Override
    public WeChatRelevanceT findByOpenId(String openId)  {
        return relevanceChatMapper.findByOpenId(openId);
    }
    @Override
    public List<WeChatRelevanceT> findGroup(String appId, String appIdF, String appIdQ) {
        return relevanceChatMapper.findGroup(appId,appIdF,appIdQ);
    }
}
