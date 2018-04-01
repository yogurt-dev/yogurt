package com.github.jyoghurt.wechatbasic.common.resp;

import com.github.jyoghurt.wechatbasic.enums.WeChatMsgTypeEnum;

/**
 * Created by Lvyu on 2015/9/23.
 */
public class AllToMessage {

    private Filter filter;
    private Mpnews mpnews;
    private Text text;
    private WeChatMsgTypeEnum msgtype;

    public Mpnews getMpnews() {
        return mpnews;
    }

    public void setMpnews(Mpnews mpnews) {
        this.mpnews = mpnews;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public WeChatMsgTypeEnum getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(WeChatMsgTypeEnum msgtype) {
        this.msgtype = msgtype;
    }
}
