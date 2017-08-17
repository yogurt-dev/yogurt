package com.github.jyoghurt.common.msgcen.domain;

import com.github.jyoghurt.common.msgcen.common.enums.MsgFailReasonEnum;
import com.github.jyoghurt.common.msgcen.common.enums.MsgLeveEnum;
import com.github.jyoghurt.common.msgcen.common.enums.MsgSendStatusEnum;
import com.github.jyoghurt.common.msgcen.common.enums.MsgTypeEnum;
import com.github.jyoghurt.core.domain.BaseEntity;

import javax.persistence.Transient;


@javax.persistence.Table(name = "MsgT")
public class MsgT extends BaseEntity<MsgT> {

    /**
     * 消息Id
     */
    @javax.persistence.Id
    private String msgId;
    /**
     * 消息类型
     */
    private MsgTypeEnum msgType;
    @Transient
    private String msgTypeValue;
    /**
     * 消息模板Id
     */
    private String msgTmplCode;
    /**
     * 消息参数
     */
    private String param;
    /**
     * 消息优先级
     */
    private MsgLeveEnum msgLevel;
    @Transient
    private String msgLevelValue;
    /**
     * 消息发送状态
     */
    private MsgSendStatusEnum msgStatus;
    @Transient
    private String msgStatusValue;
    /**
     * 消息异常
     */
    private String msgErrorLog;
    /**
     * 消息发送目标
     */
    private String target;
    /**
     * 失败原因
     */
    private MsgFailReasonEnum failReason;
    @Transient
    private String failReasonValue;

    public String getMsgId() {
        return this.msgId;
    }

    public MsgT setMsgId(String msgId) {
        this.msgId = msgId;
        return this;
    }

    public String getMsgTmplCode() {
        return this.msgTmplCode;
    }

    public MsgT setMsgTmplCode(String msgTmplCode) {
        this.msgTmplCode = msgTmplCode;
        return this;
    }

    public String getParam() {
        return this.param;
    }

    public MsgT setParam(String param) {
        this.param = param;
        return this;
    }

    public MsgLeveEnum getMsgLevel() {
        return msgLevel;
    }

    public void setMsgLevel(MsgLeveEnum msgLevel) {
        this.msgLevel = msgLevel;
        if (null != msgLevel) {
            this.setMsgLevelValue(msgLevel.getLevelValue());
        }
    }

    public String getMsgLevelValue() {
        return msgLevelValue;
    }

    public void setMsgLevelValue(String msgLevelValue) {
        this.msgLevelValue = msgLevelValue;
    }

    public MsgSendStatusEnum getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(MsgSendStatusEnum msgStatus) {
        this.msgStatus = msgStatus;
        if (null != msgStatus) {
            this.setMsgStatusValue(msgStatus.getStatusValue());
        }
    }

    public String getMsgStatusValue() {
        return msgStatusValue;
    }

    public void setMsgStatusValue(String msgStatusValue) {
        this.msgStatusValue = msgStatusValue;
    }


    public String getMsgErrorLog() {
        return this.msgErrorLog;
    }

    public MsgT setMsgErrorLog(String msgErrorLog) {
        this.msgErrorLog = msgErrorLog;
        return this;
    }

    public String getTarget() {
        return target;
    }

    public MsgT setTarget(String target) {
        this.target = target;
        return this;
    }

    public MsgFailReasonEnum getFailReason() {
        return this.failReason;
    }

    public MsgT setFailReason(MsgFailReasonEnum failReason) {
        this.failReason = failReason;
        if (null != failReason) {
            this.setFailReasonValue(failReason.getReasonValue());
        }
        return this;
    }

    public MsgTypeEnum getMsgType() {
        return msgType;
    }

    public MsgT setMsgType(MsgTypeEnum msgType) {
        this.msgType = msgType;
        if (null != msgType) {
            this.setMsgTypeValue(msgType.getTypeValue());
        }
        return this;
    }

    public String getMsgTypeValue() {
        return msgTypeValue;
    }

    public void setMsgTypeValue(String msgTypeValue) {
        this.msgTypeValue = msgTypeValue;
    }


    public String getFailReasonValue() {
        return failReasonValue;
    }

    public void setFailReasonValue(String failReasonValue) {
        this.failReasonValue = failReasonValue;
    }
}
