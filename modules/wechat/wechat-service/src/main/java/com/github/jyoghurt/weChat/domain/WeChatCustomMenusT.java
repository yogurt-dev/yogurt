package com.github.jyoghurt.weChat.domain;

import com.github.jyoghurt.wechatbasic.enums.WeChatMenusTypeEnum;
import com.github.jyoghurt.core.domain.BaseEntity;


@javax.persistence.Table(name = "WeChatCustomMenusT")
public class WeChatCustomMenusT extends BaseEntity {

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
    /**
     *
     */
    private String menuName;


    public String getId() {
        return this.id;
    }

    public WeChatCustomMenusT setId(String id) {
        this.id = id;
        return this;
    }

    public String getParentId() {
        return this.parentId;
    }

    public WeChatCustomMenusT setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public WeChatMenusTypeEnum getClickType() {
        return clickType;
    }

    public WeChatCustomMenusT setClickType(WeChatMenusTypeEnum clickType) {
        this.clickType = clickType;
        return this;
    }

    public String getContent() {
        return this.content;
    }

    public WeChatCustomMenusT setContent(String content) {
        this.content = content;
        return this;
    }

    public String getAppId() {
        return this.appId;
    }

    public WeChatCustomMenusT setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getMenuName() {
        return this.menuName;
    }

    public WeChatCustomMenusT setMenuName(String menuName) {
        this.menuName = menuName;
        return this;
    }
}
