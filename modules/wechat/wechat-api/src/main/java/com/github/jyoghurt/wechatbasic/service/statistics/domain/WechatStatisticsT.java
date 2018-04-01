package com.github.jyoghurt.wechatbasic.service.statistics.domain;

import com.github.jyoghurt.core.domain.BaseEntity;
import org.hibernate.validator.constraints.Length;


@javax.persistence.Table(name = "WechatStatisticsT")
public class WechatStatisticsT extends BaseEntity<WechatStatisticsT> {

    /**
     *
     */
    @javax.persistence.Id
    @Length(max = 36, message = "wechatStatisticsId长度不能超过36！")
    private String wechatStatisticsId;
    /**
     * ticket
     */
    private String ticket;
    /**
     * 会员openId
     */
    private String openId;
    /**
     * 事件
     */
    private String event;
    /**
     * 事件场景值
     */
    private String eventKey;


    public String getWechatStatisticsId() {
        return this.wechatStatisticsId;
    }

    public WechatStatisticsT setWechatStatisticsId(String wechatStatisticsId) {
        this.wechatStatisticsId = wechatStatisticsId;
        return this;
    }

    public String getTicket() {
        return this.ticket;
    }

    public WechatStatisticsT setTicket(String ticket) {
        this.ticket = ticket;
        return this;
    }

    public String getOpenId() {
        return this.openId;
    }

    public WechatStatisticsT setOpenId(String openId) {
        this.openId = openId;
        return this;
    }

    public String getEvent() {
        return this.event;
    }

    public WechatStatisticsT setEvent(String event) {
        this.event = event;
        return this;
    }

    public String getEventKey() {
        return this.eventKey;
    }

    public WechatStatisticsT setEventKey(String eventKey) {
        this.eventKey = eventKey;
        return this;
    }
}
