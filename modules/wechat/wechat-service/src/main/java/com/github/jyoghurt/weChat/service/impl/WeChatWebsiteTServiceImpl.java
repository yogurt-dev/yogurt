package com.github.jyoghurt.weChat.service.impl;

import com.github.jyoghurt.weChat.dao.WeChatWebsiteTMapper;
import com.github.jyoghurt.weChat.domain.WeChatWebsiteMenuT;
import com.github.jyoghurt.weChat.domain.WeChatWebsiteT;
import com.github.jyoghurt.weChat.service.WeChatWebsiteMenuTService;
import com.github.jyoghurt.weChat.service.WeChatWebsiteTService;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service("weChatWebsiteTService")
public class WeChatWebsiteTServiceImpl extends ServiceSupport<WeChatWebsiteT, WeChatWebsiteTMapper> implements WeChatWebsiteTService {
	@Autowired
    private WeChatWebsiteTMapper weChatWebsiteTMapper;
    @Autowired
    private WeChatWebsiteMenuTService weChatWebsiteMenuTService;
    @Override
	public WeChatWebsiteTMapper getMapper() {
		return weChatWebsiteTMapper;
	}

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(WeChatWebsiteT.class, id);
    }

    @Override
    public WeChatWebsiteT find(Serializable id)  {
        return getMapper().selectById(WeChatWebsiteT.class,id);
    }

    @Override
    public String addWeb(WeChatWebsiteT weChatWebsiteT) {
        //保存主表
        //todo
         weChatWebsiteT.setState("0");
          this.save(weChatWebsiteT);
        //保存菜单表
        List<WeChatWebsiteMenuT> list = weChatWebsiteT.getList();

        for (int i = 0; i < list.size(); i++) {
            WeChatWebsiteMenuT weChatWebsiteMenuT = list.get(i);
            weChatWebsiteMenuT.setParentId("0");
            weChatWebsiteMenuT.setWebId(weChatWebsiteT.getWebId());
            weChatWebsiteMenuT.setUrl("#");
           /// weChatWebsiteMenuT.setMenuImg(list.get(i).getMenuImg());
          //  weChatWebsiteMenuT.setMenuName(list.get(i).getMenuName());
            weChatWebsiteMenuT.setSort(i);
            weChatWebsiteMenuTService.save(weChatWebsiteMenuT);
        }
        return weChatWebsiteT.getWebId();
    }

    @Override
    public WeChatWebsiteT findByEebId(String id)  {
        return getMapper().findByWebId(id);
    }
}
