package com.github.jyoghurt.wechatbasic.common.resp;


/**
 * Created by zhangjl on 2015/9/30
 */
public class MessageByOpenId {

    private String touser;
    private Text text;
    private String msgtype;


    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }
}
