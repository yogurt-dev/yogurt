package com.github.jyoghurt.weChat.domain;


import com.github.jyoghurt.wechatbasic.enums.WeChatMenusTypeEnum;
import com.github.jyoghurt.core.domain.BaseEntity;

import java.util.List;

/**
 * Created by zhangjl on 2015/10/13.
 */
public class WeChatCustomMenusTVo extends BaseEntity {
    /**
     * 主键
     */
    @javax.persistence.Id
    private String id;
    /**
     * 父节点Id
     */
    private String parentId;
    /**
     * 点击类型
     */
    private WeChatMenusTypeEnum clickType;
    /**
     * 发送内容
     */
    private String content;
    /**
     * appId
     */
    private String appId;
    private String menuName;
    private String menuId;//hiddenId前台使用

    public String getMenuId() {
        return id;
    }

    public WeChatCustomMenusTVo setMenuId(String menuId) {
        this.menuId = id;
        return this;
    }

    public String getMenuName() {
        return menuName;
    }

    public WeChatCustomMenusTVo setMenuName(String menuName) {
        this.menuName = menuName;
        return this;
    }

    public String getAppId() {
        return appId;
    }

    public WeChatCustomMenusTVo setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    /**
     * 子对象
     */

    private List<WeChatCustomMenusTVo> customMenusTList;

    public List<WeChatCustomMenusTVo> getCustomMenusTList() {
        return customMenusTList;
    }

    public WeChatCustomMenusTVo setCustomMenusTList(List<WeChatCustomMenusTVo> customMenusTList) {
        this.customMenusTList = customMenusTList;
        return this;
    }

    public String getId() {
        return this.id;
    }

    public WeChatCustomMenusTVo setId(String id) {
        this.id = id;
        return this;
    }

    public String getParentId() {
        return this.parentId;
    }

    public WeChatCustomMenusTVo setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public WeChatMenusTypeEnum getClickType() {
        return clickType;
    }

    public void setClickType(WeChatMenusTypeEnum clickType) {
        this.clickType = clickType;
    }

    public String getContent() {
        return this.content;
    }

    public WeChatCustomMenusTVo setContent(String content) {
        this.content = content;
        return this;
    }

}
