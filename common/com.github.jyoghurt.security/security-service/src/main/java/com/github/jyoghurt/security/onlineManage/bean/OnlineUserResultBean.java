package com.github.jyoghurt.security.onlineManage.bean;

import com.github.jyoghurt.security.onlineManage.domain.SecurityOnlineT;
import com.github.jyoghurt.security.onlineManage.service.SecurityOnlineTService;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.security.securityUserT.domain
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-02-03 15:16
 */
@Component("onlineUserResultBean")
public class OnlineUserResultBean {

    //日志记录器
    static Logger logger = LoggerFactory.getLogger(OnlineUserResultBean.class);

    private static SecurityOnlineTService securityOnlineTService;

    //在线用户集合
    private static Map<String, SecurityOnlineT> onlineUserMap = new HashMap<>();


    /**
     * 有用户登录
     *
     * @param session
     */
    public static void userLogin(WebSocketSession session) {
        securityOnlineTService = (SecurityOnlineTService) SpringContextUtils.getBean("securityOnlineTService");
        SecurityOnlineT securityOnlineT = new SecurityOnlineT();
        securityOnlineT.setLocalAddress(session.getLocalAddress().getAddress().toString());
        securityOnlineT.setRemoteAddress(session.getRemoteAddress().getAddress().toString());
        if (checkSession(session)) {
            String sessionId = session.getAttributes().get("sessionId").toString();
            securityOnlineT.setSessionId(sessionId);
            securityOnlineT.setUserName(((HttpSession) session.getAttributes().get(sessionId)).getAttribute
                    ("operatorName").toString());
            securityOnlineT.setBsflag("0");
            logger.info("用户 {} 登录系统，ip{}", securityOnlineT.getLocalAddress(), securityOnlineT.getRemoteAddress());
            onlineUserMap.put(session.getAttributes().get("sessionId").toString(), securityOnlineT);
        }
    }

    /**
     * 處理用戶登出
     *
     * @param session
     */
    public static void userLogout(WebSocketSession session) {
        if (checkSession(session)) {
            logger.info("用户 {} 登录系统，ip{}", session.getLocalAddress().getAddress().toString(), session.getRemoteAddress().getAddress().toString());
            onlineUserMap.remove(session.getAttributes().get("sessionId").toString());
        }
    }

    public List<SecurityOnlineT> getOnLineUsers() {
        Set<SecurityOnlineT> onlineUsers = new LinkedHashSet<>();
        for (Map.Entry entry : onlineUserMap.entrySet()) {
            onlineUsers.add((SecurityOnlineT) entry.getValue());
        }
        List<SecurityOnlineT> onlineUserList = new ArrayList<>(onlineUsers);
        return onlineUserList;
    }

    private static boolean checkSession(WebSocketSession session) {
        if (session != null) {
            if (session.getAttributes() != null) {
                if (session.getAttributes().get("sessionId") != null) {
                    return true;
                }
            }
        }
        return false;
    }
}
