package com.github.jyoghurt.weChat.service;

import com.github.jyoghurt.weChat.domain.WeChatWebsiteMenuT;
import com.github.jyoghurt.core.service.BaseService;

import java.util.List;

/**
 * WebsiteMenuT服务层
 *
 */
public interface WeChatWebsiteMenuTService extends BaseService<WeChatWebsiteMenuT> {
    List<WeChatWebsiteMenuT> findByParentId(String parentId);
    void  deleteByWebId(String webId);
    void updateBySort(String parentId, int sort);
}
