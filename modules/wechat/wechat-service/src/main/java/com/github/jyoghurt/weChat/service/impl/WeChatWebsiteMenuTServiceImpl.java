package com.github.jyoghurt.weChat.service.impl;

import com.github.jyoghurt.weChat.dao.WeChatWebsiteMenuTMapper;
import com.github.jyoghurt.weChat.domain.WeChatWebsiteMenuT;
import com.github.jyoghurt.weChat.service.WeChatWebsiteMenuTService;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service("weChatWebsiteMenuTService")
public class WeChatWebsiteMenuTServiceImpl extends ServiceSupport<WeChatWebsiteMenuT, WeChatWebsiteMenuTMapper> implements WeChatWebsiteMenuTService {
	@Autowired
    private WeChatWebsiteMenuTMapper weChatWebsiteMenuTMapper;

    @Override
	public WeChatWebsiteMenuTMapper getMapper() {
		return weChatWebsiteMenuTMapper;
	}

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(WeChatWebsiteMenuT.class, id);
    }

    @Override
    public WeChatWebsiteMenuT find(Serializable id)  {
        return getMapper().selectById(WeChatWebsiteMenuT.class,id);
    }

    @Override
    public List<WeChatWebsiteMenuT> findByParentId(String parentId)  {
        return getMapper().findByParentId(parentId);
    }

    @Override
    public void deleteByWebId(String webId)  {
        getMapper().deleteMenuByWebId(webId);
    }

    @Override
    public void updateBySort(String parentId, int sort)  {
        getMapper().updateBySort(parentId,sort);
    }
}
