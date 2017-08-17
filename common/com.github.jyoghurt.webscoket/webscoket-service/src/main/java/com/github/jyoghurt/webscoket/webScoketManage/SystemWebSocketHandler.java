package com.github.jyoghurt.webscoket.webScoketManage;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.jyoghurt.webscoket.Bean.BaseScoketBean;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import com.github.jyoghurt.webscoket.common.constants.ScoketConstants;

import java.util.HashSet;
import java.util.Set;


public class SystemWebSocketHandler implements WebSocketHandler {

    Logger logger = LoggerFactory.getLogger(SystemWebSocketHandler.class);

    public static Set<String> registerBeanSet = new HashSet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session)   {
        try {
            for (String registerBeanName : registerBeanSet) {
                BaseScoketBean baseScoketBean = (BaseScoketBean) SpringContextUtils.getBean(toLowerCaseFirstOne(registerBeanName));
                if (baseScoketBean != null) {
                    baseScoketBean.afterConnectionEstablished(session);
                }
            }
        } catch (Exception e) {
            logger.error("webscoket：afterConnectionEstablished方法异常", e);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)   {
        BaseScoketBean baseScoketBean = null;
        try {
            if (message != null && message.getPayloadLength()>0) {
                JSONObject messageObj = JSON.parseObject(message.getPayload().toString());
                if (messageObj.get(ScoketConstants.SCOKETTYPE) != null) {
                    baseScoketBean = (BaseScoketBean) SpringContextUtils.getBean(toLowerCaseFirstOne(messageObj.get(ScoketConstants.SCOKETTYPE).toString()));
                }
            }
            if (baseScoketBean != null) {
                baseScoketBean.handleMessage(session, message);
            }
        } catch (Exception e) {
            logger.error("webscoket：handleMessage方法异常", e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)   {
        try {
            for (String registerBeanName : registerBeanSet) {
                BaseScoketBean baseScoketBean = (BaseScoketBean) SpringContextUtils.getBean(toLowerCaseFirstOne(registerBeanName));
                if (baseScoketBean != null) {
                    baseScoketBean.handleTransportError(session, exception);
                }
            }
        } catch (Exception e) {
            logger.error("webscoket:handleTransportError方法异常", e);
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)   {
        try {
            for (String registerBeanName : registerBeanSet) {
                BaseScoketBean baseScoketBean = (BaseScoketBean) SpringContextUtils.getBean(toLowerCaseFirstOne(registerBeanName));
                if (baseScoketBean != null) {
                    baseScoketBean.afterConnectionClosed(session, closeStatus);
                }
            }
        } catch (Exception e) {
            logger.error("webscoket:afterConnectionClosed方法异常", e);
        }
        logger.info(session.getAttributes().get(ScoketConstants.SCOKETNAME) + "连接关闭！");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private static String toLowerCaseFirstOne(String var) {
        if (Character.isLowerCase(var.charAt(0)))
            return var;
        else
            return (new StringBuilder()).append(Character.toLowerCase(var.charAt(0))).append(var.substring(1)).toString();
    }
}
