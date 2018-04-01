package com.github.jyoghurt.wechatbasic.common.resp;

/**
 * Created by zhangjl on 2015/9/18.
 */


import com.github.jyoghurt.wechatbasic.common.req.ImageMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author morning
 * @date 2015年2月16日 下午2:29:32
 */
@XStreamAlias("xml")
public class OutputMessage {

    @XStreamAlias("ToUserName")
    @XStreamCDATA
    private String ToUserName;

    @XStreamAlias("FromUserName")
    @XStreamCDATA
    private String FromUserName;

    @XStreamAlias("CreateTime")
    private Long CreateTime;

    @XStreamAlias("MsgType")
    @XStreamCDATA
    private String MsgType = "text";

    private ImageMessage Image;

    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public Long getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Long createTime) {
        CreateTime = createTime;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public ImageMessage getImage() {
        return Image;
    }

    public void setImage(ImageMessage image) {
        Image = image;
    }

}
