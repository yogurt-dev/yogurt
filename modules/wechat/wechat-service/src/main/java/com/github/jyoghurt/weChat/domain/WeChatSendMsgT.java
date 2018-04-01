package com.github.jyoghurt.weChat.domain;


import com.github.jyoghurt.wechatbasic.enums.WeChatMsgSendEnum;
import com.github.jyoghurt.wechatbasic.enums.WeChatMsgTypeEnum;
import com.github.jyoghurt.core.domain.BaseEntity;


@javax.persistence.Table(name = "WeChatSendMsgT")
public class WeChatSendMsgT extends BaseEntity {

    /**
     * 主键
     */
    @javax.persistence.Id
    private String id;
    /**
     * 消息发送任务的ID
     */
    private String msgId;
    /**
     * 消息发送类型
     */
    private WeChatMsgTypeEnum msgType;
    /**
     * 消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     */
    private String msgDataId;
    /**
     * 消息发送内容
     */
    private String content;
    /**
     * 消息发送目标
     */
    private String msgTarget;
    /**
     * 是否已经发送
     */
    private WeChatMsgSendEnum send;
    /**
     * appId
     */
    private String appId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getId() {
        return this.id;
    }

    public WeChatSendMsgT setId(String id) {
        this.id = id;
        return this;
    }

    public String getMsgId() {
        return this.msgId;
    }

    public WeChatSendMsgT setMsgId(String msgId) {
        this.msgId = msgId;
        return this;
    }

    public WeChatSendMsgT setMsgType(WeChatMsgTypeEnum msgType) {
        this.msgType = msgType;
        return this;
    }

    public WeChatMsgTypeEnum getMsgType() {
        return msgType;
    }

    public String getMsgDataId() {
        return this.msgDataId;
    }

    public WeChatSendMsgT setMsgDataId(String msgDataId) {
        this.msgDataId = msgDataId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public WeChatSendMsgT setContent(String content) {
        this.content = content;
        return this;
    }

    public String getMsgTarget() {
        return this.msgTarget;
    }

    public WeChatSendMsgT setMsgTarget(String msgTarget) {
        this.msgTarget = msgTarget;
        return this;
    }

    public WeChatMsgSendEnum getSend() {
        return send;
    }

    public void setSend(WeChatMsgSendEnum send) {
        this.send = send;
    }
}
