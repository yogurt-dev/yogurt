package com.github.jyoghurt.weChat.domain;

import java.util.List;

/**
 * Created by zhangjl on 2015/10/14.
 */
public class RequestCustomMenusListVo {
    private List<WeChatCustomMenusTVo> weChatCustomMenusTVoList;

    public List<WeChatCustomMenusTVo> getWeChatCustomMenusTVoList() {
        return weChatCustomMenusTVoList;
    }

    public void setWeChatCustomMenusTVoList(List<WeChatCustomMenusTVo> weChatCustomMenusTVoList) {
        this.weChatCustomMenusTVoList = weChatCustomMenusTVoList;
    }
}
