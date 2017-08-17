package com.github.jyoghurt.weChat.service.impl;

import com.github.jyoghurt.weChat.dao.WeChatMpnewsMsgTMapper;
import com.github.jyoghurt.weChat.domain.WeChatMpnewsMsgT;
import com.github.jyoghurt.weChat.service.WeChatMpnewsMsgTService;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service("weChatMpnewsMsgTService")
public class WeChatMpnewsMsgTServiceImpl extends ServiceSupport<WeChatMpnewsMsgT, WeChatMpnewsMsgTMapper> implements WeChatMpnewsMsgTService {
	@Autowired
    private WeChatMpnewsMsgTMapper weChatMpnewsMsgTMapper;

    @Override
	public WeChatMpnewsMsgTMapper getMapper() {
		return weChatMpnewsMsgTMapper;
	}

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(WeChatMpnewsMsgT.class, id);
    }

    @Override
    public WeChatMpnewsMsgT find(Serializable id)  {
        return getMapper().selectById(WeChatMpnewsMsgT.class,id);
    }

    @Override
    public void deleteByMessageId(String messageId) {

         getMapper().deleteByMessageId(messageId);
    }

    @Override
    public List<WeChatMpnewsMsgT> getListByMessageId(String messageId) {
        return getMapper().getListByMessageId(messageId);
    }


}
