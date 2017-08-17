package com.github.jyoghurt.security.onlineManage.service;

import com.github.jyoghurt.core.result.QueryResult;
import com.github.jyoghurt.core.service.BaseService;
import com.github.jyoghurt.security.onlineManage.domain.SecurityOnlineT;
import org.springframework.web.socket.WebSocketSession;

/**
 * 登录用户统计服务层
 *
 */
public interface SecurityOnlineTService extends BaseService<SecurityOnlineT> {

    /**
     * 用戶登録的時候存儲信息
     * @param session

     */
    void userLogin(WebSocketSession session) ;

    /**
     * 用戶登出的時候去除信息
     * @param session

     */
    void userLogout(WebSocketSession session) ;

    QueryResult getOnlineUsers() ;

    /**
     * 服務器關閉的時候，清楚登録信息

     */
    void deleteAll() ;
}
