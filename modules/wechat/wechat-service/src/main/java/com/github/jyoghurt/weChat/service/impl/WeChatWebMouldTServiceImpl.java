package com.github.jyoghurt.weChat.service.impl;

import com.github.jyoghurt.weChat.dao.WeChatWebMouldTMapper;
import com.github.jyoghurt.weChat.domain.WeChatWebMouldT;
import com.github.jyoghurt.weChat.service.WeChatWebMouldTService;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("weChatWebMouldTService")
public class WeChatWebMouldTServiceImpl extends ServiceSupport<WeChatWebMouldT, WeChatWebMouldTMapper> implements WeChatWebMouldTService {
	@Autowired
    private WeChatWebMouldTMapper weChatWebMouldTMapper;

    @Override
	public WeChatWebMouldTMapper getMapper() {
		return weChatWebMouldTMapper;
	}

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(WeChatWebMouldT.class, id);
    }

    @Override
    public WeChatWebMouldT find(Serializable id)  {
        return getMapper().selectById(WeChatWebMouldT.class,id);
    }
}
