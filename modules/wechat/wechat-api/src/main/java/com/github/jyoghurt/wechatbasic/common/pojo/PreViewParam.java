package com.github.jyoghurt.wechatbasic.common.pojo;

import com.github.jyoghurt.wechatbasic.common.resp.Mpnews;

/**
 * Created by zhangjl on 2015/11/11.
 */
/*预览参数实体*/
public class PreViewParam {
    private String towxname;
    private String touser;
    private Mpnews mpnews;
    private String msgtype;

    public String getTowxname() {
        return towxname;
    }

    public void setTowxname(String towxname) {
        this.towxname = towxname;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public Mpnews getMpnews() {
        return mpnews;
    }

    public void setMpnews(Mpnews mpnews) {
        this.mpnews = mpnews;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }
}
