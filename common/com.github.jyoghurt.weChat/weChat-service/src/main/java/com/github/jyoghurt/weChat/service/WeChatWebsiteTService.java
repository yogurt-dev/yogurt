package com.github.jyoghurt.weChat.service;

import com.github.jyoghurt.weChat.domain.WeChatWebsiteT;
import com.github.jyoghurt.core.service.BaseService;

/**
 * WebsiteT服务层
 *
 */
public interface WeChatWebsiteTService extends BaseService<WeChatWebsiteT> {
    public String addWeb(WeChatWebsiteT weChatWebsiteT);

    WeChatWebsiteT findByEebId(String id) ;
}
