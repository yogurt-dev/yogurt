package com.github.jyoghurt.webscoket.Bean;



import com.github.jyoghurt.webscoket.webScoketManage.SystemWebSocketHandler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import com.github.jyoghurt.webscoket.callBack.ScoketCallBack;

/**
 * Created by zhangjl on 2015/12/20.
 */
public abstract class BaseScoketBean implements ScoketCallBack {
    public BaseScoketBean(){
        this.register(this.getClass().getSimpleName());
    }

    public abstract void afterConnectionEstablished(WebSocketSession session)  ;


    public abstract void handleMessage(WebSocketSession session, WebSocketMessage<?> message)  ;


    public abstract void handleTransportError(WebSocketSession session, Throwable exception)  ;

    public abstract void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)  ;


    public boolean supportsPartialMessages() {
        return false;
    }


    @Override
    public void register(String registerBeanName) {
        SystemWebSocketHandler.registerBeanSet.add(registerBeanName);
    }
}
