package com.github.jyoghurt.weChat.common.service;


import com.github.jyoghurt.weChat.domain.WeChatAutoResponseMsgT;
import com.github.jyoghurt.weChat.domain.WeChatAutoResponseT;
import com.github.jyoghurt.weChat.service.WeChatAutoResponseMsgTService;
import com.github.jyoghurt.weChat.service.WeChatAutoResponseTService;
import com.github.jyoghurt.weChat.webScoketConfig.WeChatScoketBean;
import com.github.jyoghurt.wechatbasic.common.pojo.Articles;
import com.github.jyoghurt.wechatbasic.common.pojo.MaterialNewsMap;
import com.github.jyoghurt.wechatbasic.common.req.InputMessage;
import com.github.jyoghurt.wechatbasic.common.resp.Article;
import com.github.jyoghurt.wechatbasic.common.resp.NewsMessage;
import com.github.jyoghurt.wechatbasic.common.resp.OutputMessage;
import com.github.jyoghurt.wechatbasic.common.resp.TextMessage;
import com.github.jyoghurt.wechatbasic.common.util.MessageUtil;
import com.github.jyoghurt.wechatbasic.common.util.SerializeXmlUtil;
import com.github.jyoghurt.wechatbasic.enums.WeChatAutoResponseType;
import com.github.jyoghurt.wechatbasic.exception.WeChatException;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class CoreService {
    /**
     * 处理微信发来的请求
     *
     * @param request
     * @return
     */
    /**
     * 对话窗口服务类
     */
    private static Logger logger = LoggerFactory.getLogger(CoreService.class);
    //注册service
    private static WeChatAutoResponseTService weChatAutoResponseTService = (WeChatAutoResponseTService) SpringContextUtils.getBean("weChatAutoResponseTService");
    private static WeChatAutoResponseMsgTService weChatAutoResponseMsgTService = (WeChatAutoResponseMsgTService) SpringContextUtils.getBean("weChatAutoResponseMsgTService");

    public static String processRequest(HttpServletRequest request) {
        String respMessage = MessageUtil.RESP_MESSAGE_TYPE_SUCCESS;
        try {
            // 默认返回的文本消息内容
            String respContent = "请求处理异常，请稍候尝试！";
            //appId
            String appId = request.getParameter("appId");
            //appName
            String appName = request.getParameter("appName");
            //appsecret
            String appsecret = request.getParameter("appsecret");
            // 处理接收消息
            ServletInputStream in = request.getInputStream();
            // 将POST流转换为XStream对象
            XStream xs = SerializeXmlUtil.createXstream();
            xs.processAnnotations(InputMessage.class);
            xs.processAnnotations(OutputMessage.class);
            // 将指定节点下的xml节点数据映射为对象
            xs.alias("xml", InputMessage.class);
            // 将流转换为字符串
            StringBuilder xmlMsg = new StringBuilder();
            byte[] b = new byte[4096];
            for (int n; (n = in.read(b)) != -1; ) {
                xmlMsg.append(new String(b, 0, n, "UTF-8"));
            }
            logger.info("**************************接收微信的xml************************:",xmlMsg.toString());
            // 将xml内容转换为InputMessage对象
            InputMessage inputMsg = (InputMessage) xs.fromXML(xmlMsg.toString());
            // xml请求解析
            Map<String, String> requestMap = new HashMap<>();

            // 发送方帐号（open_id）  
            String fromUserName = inputMsg.getFromUserName();
            // 公众帐号
            String toUserName = inputMsg.getToUserName();
            // 消息类型
            String msgType = inputMsg.getMsgType();
            //消息创建时间
            Long createTime = inputMsg.getCreateTime();
            //消息内容
            String context = inputMsg.getContent();
            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                logger.info("wechatbasic receive text Message By appName:" + appName);
                respMessage = checkAndReplyAutoResponse(appId,  context, fromUserName, toUserName);
                pushMsgByOpenId(fromUserName, createTime, context, appId, appName, appsecret);
            }
            // 图片消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
                respContent = "您发送的是图片消息！";
            }
            // 地理位置消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
                respContent = "您发送的是地理位置消息！";
            }
            // 链接消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
                respContent = "您发送的是链接消息！";
            }
            // 音频消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
                respContent = "您发送的是音频消息！";
            }
            // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                // 订阅
                if (MessageUtil.EVENT_TYPE_SUBSCRIBE.equals(inputMsg.getEvent())) {
                    return checkAndReplyFocus(appId, fromUserName, toUserName);
                }
            }
        } catch (Exception e) {
            logger.error("解析微信公众账号请求错误", e);
        }
        return respMessage;
    }

    /**
     * emoji表情转换(hex -> utf-16)
     *
     * @param hexEmoji
     * @return
     */
    public static String emoji(int hexEmoji) {
        return String.valueOf(Character.toChars(hexEmoji));
    }

    //客服接收消息并完成消息推送fromUserName,createTime,content
    public static void pushMsgByOpenId(String fromUserName, Long createTime, String context, String appId, String
            appName, String appsecret)   {
        WeChatScoketBean weChatWebScoketConfig = new WeChatScoketBean();
        weChatWebScoketConfig.reciveMessageByUser(fromUserName, context, appId, appName, appsecret);
    }

    /*校验关注自动回复*/
    public static String checkAndReplyFocus(String appId, String fromUserName, String toUserName) throws WeChatException {
        try {
            for (WeChatAutoResponseT weChatAutoResponseT : weChatAutoResponseTService.findAutoResponseList(appId, WeChatAutoResponseType.subscribe)) {
                for (WeChatAutoResponseMsgT weChatAutoResponseMsgT : weChatAutoResponseMsgTService.findAutoResponseMsgByParent(weChatAutoResponseT.getAutoResponseId())) {
                    return analysisAutoResponseMsgT(fromUserName, toUserName, weChatAutoResponseMsgT);
                }
            }
        } catch (Exception e) {
            logger.error("校验并发送关注时自动回复报错", e);
        }
        return MessageUtil.RESP_MESSAGE_TYPE_SUCCESS;
    }

    /*校验自动回复*/
    public static String checkAndReplyAutoResponse(String appId,String context, String fromUserName, String toUserName) throws WeChatException {
        String responseMsg = MessageUtil.RESP_MESSAGE_TYPE_SUCCESS;
        try {
            /*关键词回复解析*/
            for (WeChatAutoResponseT weChatAutoResponseT : weChatAutoResponseTService.findAutoResponseList(appId, WeChatAutoResponseType.KEYWORDS)) {
                //判断匹配类型
                switch (weChatAutoResponseT.getMatchingType()) {
                    case LIKE: {//模糊匹配关键字
                        if (weChatAutoResponseT.getKeywords().indexOf(context) > -1) {
                            String msg = analysisAutoResponseT(weChatAutoResponseT, fromUserName, toUserName);
                            if (!MessageUtil.RESP_MESSAGE_TYPE_SUCCESS.equals(msg)) {
                                return msg;
                            }
                        }
                        break;
                    }
                    case EQUAL: {
                        if (weChatAutoResponseT.getKeywords().equals(context)) {
                            String msg = analysisAutoResponseT(weChatAutoResponseT, fromUserName, toUserName);
                            if (!MessageUtil.RESP_MESSAGE_TYPE_SUCCESS.equals(msg)) {
                                return msg;
                            }
                        }
                        break;
                    }
                }
            }
            /*解析消息自动回复*/
            for (WeChatAutoResponseT weChatAutoResponseT : weChatAutoResponseTService.findAutoResponseList(appId, WeChatAutoResponseType.MESSAGE)) {
                //遍历消息列表
                for (WeChatAutoResponseMsgT weChatAutoResponseMsgT : weChatAutoResponseMsgTService.findAutoResponseMsgByParent(weChatAutoResponseT.getAutoResponseId())) {
                    //解析消息并返回xml
                    return analysisAutoResponseMsgT(fromUserName, toUserName, weChatAutoResponseMsgT);
                }
            }
        } catch (Exception e) {
            logger.error("解析自动回复报错", e);
        }
        return responseMsg;
    }

    //解析自动回复规则实体
    public static String analysisAutoResponseT(WeChatAutoResponseT weChatAutoResponseT, String fromUserName, String toUserName) throws WeChatException {
        for (WeChatAutoResponseMsgT weChatAutoResponseMsgT : weChatAutoResponseMsgTService.findAutoResponseMsgByParent(weChatAutoResponseT.getAutoResponseId())) {
            //解析消息并返回xml
            return analysisAutoResponseMsgT(fromUserName, toUserName, weChatAutoResponseMsgT);
        }
        return MessageUtil.RESP_MESSAGE_TYPE_SUCCESS;
    }

    //解析自动回复消息实体并生成xml返回
    public static String analysisAutoResponseMsgT(String fromUserName, String toUserName, WeChatAutoResponseMsgT weChatAutoResponseMsgT) throws WeChatException {
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