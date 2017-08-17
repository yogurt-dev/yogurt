package com.github.jyoghurt.security.onlineManage.service.impl;

import com.github.jyoghurt.security.onlineManage.bean.OnlineUserResultBean;
import com.github.jyoghurt.security.onlineManage.dao.SecurityOnlineTMapper;
import com.github.jyoghurt.security.onlineManage.domain.SecurityOnlineT;
import com.github.jyoghurt.security.onlineManage.service.SecurityOnlineTService;
import com.github.jyoghurt.core.result.QueryResult;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.Serializable;
import java.util.List;

@Service("securityOnlineTService")
public class SecurityOnlineTServiceImpl extends ServiceSupport<SecurityOnlineT, SecurityOnlineTMapper> implements SecurityOnlineTService {
	@Autowired
    private SecurityOnlineTMapper securityOnlineTMapper;

    @Override
	public SecurityOnlineTMapper getMapper() {
		return securityOnlineTMapper;
	}

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(SecurityOnlineT.class, id);
    }

    @Override
    public SecurityOnlineT find(Serializable id)  {
        return getMapper().selectById(SecurityOnlineT.class,id);
    }

    @Override
    public void userLogin(WebSocketSession session) {
        SecurityOnlineT securityOnlineT = new SecurityOnlineT();
        securityOnlineT.setUri(session.getUri().getPath());
        securityOnlineT.setLocalAddress(session.getLocalAddress().getAddress().toString());
        securityOnlineT.setRemoteAddress(session.getRemoteAddress().getAddress().toString());
        securityOnlineT.setSessionId(session.getAttributes().get("sessionId").toString());
        logger.info("用户 {} 登录系统，ip{}", securityOnlineT.getLocalAddress(), securityOnlineT.getRemoteAddress());
        this.save(securityOnlineT);
    }
    public QueryResult getOnlineUsers() {
        QueryResult queryResult=this.newQueryResult();
        OnlineUserResultBean onlineUserResultBean=new OnlineUserResultBean();
        List<SecurityOnlineT> userList=onlineUserResultBean.getOnLineUsers();
        queryResult.setData(userList);
        queryResult.setRecordsTotal(userList.size());
        return queryResult;
    }
    @Override
    public void userLogout(WebSocketSession session)  {
        String sessionId = session.getAttributes().get("sessionId").toString();
        securityOnlineTMapper.deleteSession(sessionId);
    }

    public void deleteAll() {
        securityOnlineTMapper.deleteAll();
    }
}
