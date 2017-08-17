package com.github.jyoghurt.security.securityScoketBean;


import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.vertx.support.VertxSocketSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pub.utils.SessionUtils;

/**
 * Created by zhangjl on 2015/12/21.
 */
@Component
public class SecurityScoketBean extends VertxSocketSupport {
    static Logger logger = LoggerFactory.getLogger(SecurityScoketBean.class);

    public void sendSessionTimeOutMsg(String sessionId) {
        try {
            send("locked");
        } catch (Exception e) {
            logger.error("推送超时消息异常" + sessionId, e);
        }

    }
    public String initAddress() {
        SecurityUserT securityUserT = (SecurityUserT) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession().getAttribute(SessionUtils.SESSION_MANAGER);
        return "SessionTime";
    }


    public void handleMessage(Object message) {
        System.out.print(message);
    }
}
