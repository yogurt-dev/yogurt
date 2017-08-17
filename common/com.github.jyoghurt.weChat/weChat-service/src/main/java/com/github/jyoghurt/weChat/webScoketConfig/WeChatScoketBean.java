package com.github.jyoghurt.weChat.webScoketConfig;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUnitT.service.SecurityUnitTService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.security.securityUserT.service.SecurityUserTService;
import com.github.jyoghurt.weChat.domain.WeChatConversationT;
import com.github.jyoghurt.weChat.domain.WeChatRelevanceT;
import com.github.jyoghurt.weChat.service.ConversationService;
import com.github.jyoghurt.weChat.service.RelevanceChatService;
import com.github.jyoghurt.weChat.service.WeChatMsgTService;
import com.github.jyoghurt.webscoket.Bean.BaseScoketBean;
import com.github.jyoghurt.wechatbasic.common.pojo.AccessToken;
import com.github.jyoghurt.wechatbasic.common.pojo.WeixinUserInfo;
import com.github.jyoghurt.wechatbasic.common.util.AdvancedUtil;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

import static com.github.jyoghurt.wechatbasic.common.util.WeixinUtil.getAccessToken;

/**
 * Created by zhangjl on 2015/12/20.
 */
@Component
public class WeChatScoketBean extends BaseScoketBean {
    Logger logger = LoggerFactory.getLogger(WeChatScoketBean.class);
    /*用户与客服关系池*/
    public static final Map<String, String> relationship = new HashMap<>();
    /*webscoket连接池*/
    public static final Map<String, Set<WebSocketSession>> webscoketPool = new HashMap<>();
    /*appId与webscoket连接池*/
    public static final Map<String, Set<String>> appRelationPool = new HashMap<>();
    /*广播类型*/
    private static final String radio = "radio";

    @Resource
    private SecurityUserTService securityUserTService;

    @Resource
    private SecurityUnitTService securityUnitTService;
    /**
     * 微信消息记录表服务类
     */
    @Resource
    private WeChatMsgTService weChatMsgTService;

    public void afterConnectionEstablished(WebSocketSession session) {
        logger.debug("初始化微信客服webscoket......");
        if (session.getAttributes().get("websocket_username") == null) {
            return;
        }
        putWebscoketPool(session.getAttributes().get("websocket_username").toString(), session);
        putAppRelationPool(session.getAttributes().get("websocket_username").toString());
        logger.debug("初始化微信客服webscoket成功！！！！");
    }

    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)   {
        JSONObject messageObj = JSON.parseObject(message.getPayload().toString());
        if (radio.equals(messageObj.get("type"))) {
            answerUser(message);
            radioMsg(message, session.getId());
        }
    }

    public void handleTransportError(WebSocketSession session, Throwable exception)   {
        logger.debug(session.getAttributes().get("websocket_username").toString() + "的websocket connection error......");
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)   {
        deleteScoket(session);
    }

    /*将回复消息广播给所有非本人的人*/
    public void radioMsg(WebSocketMessage<?> message, String sessionId) {
        //解析message
        JSONObject messageObj = JSON.parseObject(message.getPayload().toString());
        messageObj.put("scoketType", "WeChatScoketBean");
        TextMessage textMessage = new TextMessage(messageObj.toString());
        String appId = (String) messageObj.get("appId");
        logger.debug("向appId为:" + appId + "发送消息开始");
        try {
            //根据appId向App关系池中用户发送消息
            Set<String> relationSet = appRelationPool.get(appId);
            for (String keyName : relationSet) {
                if (webscoketPool.get(keyName) != null) {
                    logger.debug("向userId为:" + keyName + "的所有通道发送消息");
                    for (WebSocketSession webSocketSession : webscoketPool.get(keyName)) {
                        if (webSocketSession.getId() != sessionId && webSocketSession.isOpen()) {
                            logger.debug("向通道Id不为:" + sessionId + "的通道发送消息");
                            webSocketSession.sendMessage(textMessage);
                            logger.debug("广播结束");
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.debug("消息广播异常");
        }
    }

    public void reciveMessageByUser(String fromUserName, String context, String appId, String appName, String appsecret)   {
        logger.debug("接收消息内容：" + context + "获取token");
        AccessToken token = getAccessToken(appId, appsecret);
        logger.debug("接收消息内容：" + context + "获取人员在微信中的详细信息");
        WeixinUserInfo weixinUserInfo = AdvancedUtil.getUserInfo(token.getToken(), fromUserName);
        logger.debug("接收消息内容：" + context + "微信内容成功");
        /*构造关系数据*/
        createRelevanceChat(appId, appName, appsecret, fromUserName, weixinUserInfo.getNickname());
        /*构造接收消息*/
        TextMessage message = new TextMessage(createReciveMessage(appId, appName, appsecret, context, fromUserName,
                weixinUserInfo));
        /*保存实体*/
        //判断此OpenId是否跟appId保存过关联 若保存则直接取值 否则保存后再取值
        ConversationService conversationService = (ConversationService) SpringContextUtils.getBean("conversationService");
        logger.debug("保存" + appName + "中OPENID为" + fromUserName + "的消息");
        conversationService.save(createReciveMessageT(weixinUserInfo, context, fromUserName));
        logger.debug("保存" + appName + "中OPENID为" + fromUserName + "的消息成功");

        //根据appId向App关系池中用户发送消息
        logger.debug(appName + "中用户为接收消息");
        Set<String> relationSet = appRelationPool.get(appId);
        if (relationSet != null) {
            for (String keyName : relationSet) {
                if (webscoketPool.get(keyName) != null) {
                    logger.debug("userId为:" + keyName + "的所有通道接收消息");
                    for (WebSocketSession webSocketSession : webscoketPool.get(keyName)) {
                        if (webSocketSession.isOpen()) {
                            try {
                                webSocketSession.sendMessage(message);
                                logger.debug("接收成功");
                            } catch (IOException e) {
                                logger.debug("接收异常成功");
                            }
                        }
                    }
                }
            }
        }
    }

    //客服回复用户
    public void answerUser(WebSocketMessage<?> message) {
        logger.debug("调用客服回复接口开始");
        weChatMsgTService.sendMsgByOpenId(message.getPayload().toString());
        logger.debug("调用客服回复接口结束");

    }

    /*删除所有当前scoket连接*/
    public Boolean deleteScoket(WebSocketSession session) {
        logger.debug("移除当前session");
        if (session.getAttributes().get("websocket_username") == null) {
            return true;
        }
        Set<WebSocketSession> webSocketSessionSet = webscoketPool.get(session.getAttributes().get("websocket_username").toString());
        if (webSocketSessionSet == null) {
            return true;
        }
        webSocketSessionSet.remove(session);
        return true;
    }

    public Boolean putWebscoketPool(String userId, WebSocketSession session) {
        logger.debug("添加" + session.getId() + "到人员为" + userId + "的WebscoketPool");
        if (webscoketPool.get(userId) == null) {
            Set<WebSocketSession> scoketSet = new HashSet();
            scoketSet.add(session);
            webscoketPool.put(userId, scoketSet);
            return true;
        }
        webscoketPool.get(userId).add(session);
        return true;
    }

    /*根据用户部门添加appId与scoket关系池*/
    public void putAppRelationPool(String userId) {
        logger.debug("添加" + userId + "到AppRelationPool");
            /*获取用户信息*/
        SecurityUserT securityUserT = securityUserTService.find(userId);
            /*获取用户部门*/
        SecurityUnitT securityUnitT = securityUnitTService.getFathCompanyInfo(securityUserT.getBelongOrg());
        //存放订阅号关系
        if (securityUnitT.getAppId() != null) {
            putAppRelationPoolByType(securityUnitT.getAppId().trim(), userId);
        }
        //存放服务号关系
        if (securityUnitT.getAppIdF() != null) {
            putAppRelationPoolByType(securityUnitT.getAppIdF().trim(), userId);
        }
        //存放企业号关系
        if (securityUnitT.getAppIdQ() != null) {
            putAppRelationPoolByType(securityUnitT.getAppIdQ().trim(), userId);
        }
        logger.debug("添加成功！");

    }

    public Boolean putAppRelationPoolByType(String appId, String userId) {
        //查看该appId是否已经保存了关系 若已存在用户集合则添加用户 否则关系池中添加appId
        if (appRelationPool.get(appId) != null) {
            Set relationList = appRelationPool.get(appId);
            relationList.add(userId);
            return true;
        }
        Set relationList = new HashSet();
        relationList.add(userId);
        appRelationPool.put(appId, relationList);
        return true;
    }

    /*构造组织接收数据实体*/
    public WeChatConversationT createReciveMessageT(WeixinUserInfo weixinUserInfo, String context, String
            fromUserName) {
        //组织接收数据
        WeChatConversationT weChatConversationT = new WeChatConversationT();
        weChatConversationT.setFromUserName(weixinUserInfo.getNickname());
        weChatConversationT.setImg(weixinUserInfo.getHeadImgUrl());
        weChatConversationT.setReceive("1");
        weChatConversationT.setCreateDateTime(new Date());
        weChatConversationT.setContext(context);
        weChatConversationT.setFromUserId(fromUserName);
        weChatConversationT.setIfreply("0");
        weChatConversationT.setDeleteFlag(false);
        return weChatConversationT;
    }

    /*构造组织接收数据*/
    public String createReciveMessage(String appId, String appName, String appsecret, String context, String
            fromUserName, WeixinUserInfo weixinUserInfo) {
        //组织接收数据
        WeChatConversationT weChatConversationT = new WeChatConversationT();
        weChatConversationT.setFromUserName(weixinUserInfo.getNickname());
        weChatConversationT.setReceive("1");
        weChatConversationT.setCreateDateTime(new Date());
        weChatConversationT.setContext(context);
        weChatConversationT.setFromUserId(fromUserName);
        weChatConversationT.setIfreply("0");
        weChatConversationT.setImg(weixinUserInfo.getHeadImgUrl());
        JSONObject jsonObj = (JSONObject) JSON.toJSON(weChatConversationT);
        jsonObj.put("appId", appId);
        jsonObj.put("appName", appName);
        jsonObj.put("appsecret", appsecret);
        jsonObj.put("scoketType", "WeChatScoketBean");
        return JSON.toJSONString(jsonObj);
    }

    /*构造人员与appId关系对象并保存关系数据*/
    public void createRelevanceChat(String appId, String appName, String appsecret, String
            fromUserName, String nickName) {
        //判断此OpenId是否跟appId保存过关联 若保存则直接取值 否则保存后再取值
        RelevanceChatService relevanceChatService = (RelevanceChatService) SpringContextUtils.getBean("relevanceChatService");
        WeChatRelevanceT myWeChatRelevanceT = relevanceChatService.findByOpenId(fromUserName);
        if (myWeChatRelevanceT == null) {
            WeChatRelevanceT weChatRelevanceT = new WeChatRelevanceT();
            weChatRelevanceT.setAppId(appId);
            weChatRelevanceT.setAppName(appName);
            weChatRelevanceT.setAppsecret(appsecret);
            weChatRelevanceT.setOpenId(fromUserName);
            weChatRelevanceT.setNickName(nickName);
            relevanceChatService.save(weChatRelevanceT);
            return;
        }
        if (!appName.equals(myWeChatRelevanceT.getAppName()) || !nickName.equals(myWeChatRelevanceT.getNickName())) {//若以前有数据而账号名称变更或名称变更 则删除以前记录 新增变更记录
            myWeChatRelevanceT.setAppName(appName);
            myWeChatRelevanceT.setNickName(nickName);
            relevanceChatService.delete(myWeChatRelevanceT.getId());
            myWeChatRelevanceT.setId(null);
            relevanceChatService.save(myWeChatRelevanceT);
        }

    }

}
