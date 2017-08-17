package com.github.jyoghurt.weChat.service.impl;

import com.github.jyoghurt.weChat.dao.WeChatAutoResponseMsgTMapper;
import com.github.jyoghurt.weChat.domain.WeChatAutoResponseMsgT;
import com.github.jyoghurt.weChat.service.WeChatAutoResponseMsgTService;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service("weChatAutoResponseMsgTService")
public class WeChatAutoResponseMsgTServiceImpl extends ServiceSupport<WeChatAutoResponseMsgT, WeChatAutoResponseMsgTMapper> implements WeChatAutoResponseMsgTService {
	@Autowired
    private WeChatAutoResponseMsgTMapper weChatAutoResponseMsgTMapper;

    @Override
	public WeChatAutoResponseMsgTMapper getMapper() {
		return weChatAutoResponseMsgTMapper;
	}

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(WeChatAutoResponseMsgT.class, id);
    }

    @Override
    public WeChatAutoResponseMsgT find(Serializable id)  {
        return getMapper().selectById(WeChatAutoResponseMsgT.class,id);
    }
    public List<WeChatAutoResponseMsgT> findAutoResponseMsgByParent(String AutoResponseId)
            {
        return getMapper().findByAutoResponseId(AutoResponseId);
}
}
