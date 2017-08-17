package com.github.jyoghurt.webscoket.webScoketManage;

/**
 * Created by zhangjl on 2015/9/29.
 */


import com.github.jyoghurt.webscoket.common.constants.ScoketConstants;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;


/**
 * 功能描述:
 * 作者: LDL
 * 创建时间: 2014/8/18 15:40
 */
@Order(5)
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private static Logger logger = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes)   {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            attributes.put(session.getId(), session);
            if (session != null) {
                if(null!=session.getAttribute("cosDfCid")){
                    attributes.put(ScoketConstants.CASHIERID, session.getAttribute("cosDfCid"));
                }
                //使用userName区分WebSocketHandler，以便定向发送消息
                if (null != session.getAttribute(ScoketConstants.USERMANAGE)) {
                    JSONObject user = JSONObject.fromObject(session.getAttribute(ScoketConstants.USERMANAGE));
                    if (user.get(ScoketConstants.USERNAME) != null) {
                        attributes.put(ScoketConstants.SCOKETNAME, user.get(ScoketConstants.USERNAME));
                    } else {
                        attributes.put(ScoketConstants.SCOKETNAME, "empty");
                    }
                    attributes.put(ScoketConstants.SESSIONID, session.getId());
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}