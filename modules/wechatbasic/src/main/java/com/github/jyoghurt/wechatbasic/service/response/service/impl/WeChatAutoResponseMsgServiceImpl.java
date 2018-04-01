package com.github.jyoghurt.wechatbasic.service.response.service.impl;


import com.github.jyoghurt.core.handle.OperatorHandle;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import com.github.jyoghurt.wechatbasic.common.pojo.Articles;
import com.github.jyoghurt.wechatbasic.common.pojo.MaterialNewsMap;
import com.github.jyoghurt.wechatbasic.common.req.InputMessage;
import com.github.jyoghurt.wechatbasic.common.resp.Article;
import com.github.jyoghurt.wechatbasic.common.resp.NewsMessage;
import com.github.jyoghurt.wechatbasic.common.resp.TextMessage;
import com.github.jyoghurt.wechatbasic.common.util.MessageUtil;
import com.github.jyoghurt.wechatbasic.enums.WeChatAutoResponseType;
import com.github.jyoghurt.wechatbasic.enums.WeChatMatchingType;
import com.github.jyoghurt.wechatbasic.exception.WeChatException;
import com.github.jyoghurt.wechatbasic.service.response.dao.WeChatAutoResponseMsgMapper;
import com.github.jyoghurt.wechatbasic.service.response.domain.WeChatAutoResponseMsgT;
import com.github.jyoghurt.wechatbasic.service.response.service.WeChatAutoResponseMsgService;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("weChatAutoResponseMsgService")
public class WeChatAutoResponseMsgServiceImpl extends ServiceSupport<WeChatAutoResponseMsgT, WeChatAutoResponseMsgMapper> implements WeChatAutoResponseMsgService {
    private static final String responseMsg = MessageUtil.RESP_MESSAGE_TYPE_SUCCESS;

    @Autowired
    private WeChatAutoResponseMsgMapper weChatAutoResponseMsgMapper;

    @Override
    public WeChatAutoResponseMsgMapper getMapper() {
        return weChatAutoResponseMsgMapper;
    }

    @Override
    public void logicDelete(Serializable id) {
        getMapper().logicDelete(WeChatAutoResponseMsgT.class, id);
    }

    @Override
    public WeChatAutoResponseMsgT find(Serializable id) {
        return getMapper().selectById(WeChatAutoResponseMsgT.class, id);
    }

    @Override
    public String matchAutoResponse(InputMessage inputMsg) {
        WeChatAutoResponseMsgT weChatAutoResponseMsgT = matchRule(inputMsg);
        if (null == weChatAutoResponseMsgT) {
            return responseMsg;
        }
        return analysisAutoResponseMsgT(inputMsg.getFromUserName(), inputMsg.getToUserName(), weChatAutoResponseMsgT);
    }


    private WeChatAutoResponseMsgT matchRule(InputMessage inputMsg) {
        WeChatAutoResponseMsgT weChatAutoResponseMsgT = new WeChatAutoResponseMsgT();
        QueryHandle queryHandle = new QueryHandle();
        String matchStr = StringUtils.isNotEmpty(inputMsg.getContent()) ? inputMsg.getContent() : inputMsg.getEventKey();
        if (StringUtils.isNotEmpty(inputMsg.getEvent())) {
            weChatAutoResponseMsgT.setResponseType(WeChatAutoResponseType.valueOf(inputMsg.getEvent()));
        } else {
            queryHandle.addOperatorHandle("keywords", OperatorHandle.operatorType.LIKE, matchStr);
        }
        List<WeChatAutoResponseMsgT> list = this.findAll(weChatAutoResponseMsgT, queryHandle);
        for (WeChatAutoResponseMsgT weChatAutoResponseMsg : list) {
            if (WeChatMatchingType.LIKE == weChatAutoResponseMsg.getMatchingType()) {
                return weChatAutoResponseMsg;
            }
            if (WeChatMatchingType.EQUAL == weChatAutoResponseMsg.getMatchingType()) {
                if (matchStr.equals(weChatAutoResponseMsg.getKeywords())) {
                    return weChatAutoResponseMsg;
                }
            }
            if(weChatAutoResponseMsgT.getResponseType()==WeChatAutoResponseType.subscribe||weChatAutoResponseMsgT.getResponseType()==WeChatAutoResponseType.unsubscribe){
                return weChatAutoResponseMsg;
            }
        }
        return null;
    }

    //解析自动回复消息实体并生成xml返回
    private String analysisAutoResponseMsgT(String fromUserName, String toUserName, WeChatAutoResponseMsgT weChatAutoResponseMsgT) throws WeChatException {
        switch (weChatAutoResponseMsgT.getMsgType()) {
            case mpnews: {
                Gson gson = new Gson();
                NewsMessage newsMessage = new NewsMessage();
                newsMessage.setToUserName(fromUserName);
                newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
                newsMessage.setCreateTime(new Date().getTime());
                newsMessage.setFromUserName(toUserName);
                MaterialNewsMap materialNewsMap = gson.fromJson(weChatAutoResponseMsgT.getContent(), MaterialNewsMap.class);
                List<Article> articleList = new ArrayList<>();
                for (Articles articles : materialNewsMap.getContent().getNews_item()) {
                    Article article = new Article();
                    article.setDescription(articles.getDigest());
                    article.setPicUrl(articles.getDownloadPath());
                    article.setTitle(articles.getTitle());
                    article.setUrl(articles.getUrl());
                    articleList.add(article);
                }
                newsMessage.setArticles(articleList);
                newsMessage.setArticleCount(materialNewsMap.getContent().getNews_item().size());
                return MessageUtil.newsMessageToXml(newsMessage);
            }
            case text: {
                TextMessage textMessage = new TextMessage();
                textMessage.setContent(weChatAutoResponseMsgT.getContent());
                textMessage.setToUserName(fromUserName);
                textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                textMessage.setCreateTime(new Date().getTime());
                textMessage.setFromUserName(toUserName);
                return MessageUtil.textMessageToXml(textMessage);
            }
        }
        return MessageUtil.RESP_MESSAGE_TYPE_SUCCESS;
    }
}
