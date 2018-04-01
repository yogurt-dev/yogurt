package com.github.jyoghurt.msgcen.adapter;


import com.github.jyoghurt.core.utils.SpringContextUtils;
import com.github.jyoghurt.msgcen.common.enums.MsgLeveEnum;
import com.github.jyoghurt.msgcen.common.enums.MsgSendStatusEnum;
import com.github.jyoghurt.msgcen.domain.MsgT;
import com.github.jyoghurt.msgcen.domain.MsgTmplT;
import com.github.jyoghurt.msgcen.exception.MsgException;
import com.github.jyoghurt.msgcen.service.MsgService;
import net.sf.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * user:zjl
 * date: 2016/11/17.
 */
public interface MsgTarget {
    /**
     * 发送信息
     *
     * @param to      发送目标集合
     * @param msgTmpl 消息模板
     * @param param   发送参数
     */
    void send(List<String> to, MsgTmplT msgTmpl, JSONObject param) throws MsgException;

    /**
     * 消息记录
     *
     * @param to      发送目标
     * @param msgTmpl 消息模板
     * @param param   发送参数
     */
    default void recordMsg(String to, MsgTmplT msgTmpl, String param, Throwable e) {
        MsgService msgService = (MsgService) SpringContextUtils.getBean("msgService");
        MsgT msg = new MsgT();
        msg.setTarget(to);
        msg.setMsgTmplCode(msgTmpl.getTmplCode());
        msg.setMsgType(msgTmpl.getTmplType());
        msg.setParam(param);
        msg.setMsgLevel(MsgLeveEnum.COMMON);
        if (null != e) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            e.printStackTrace(new java.io.PrintWriter(buf, true));
            String expMessage = buf.toString();
            msg.setMsgStatus(MsgSendStatusEnum.FAIL);
            msg.setMsgErrorLog(expMessage);
            msgService.save(msg);
            try {
                buf.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
        msg.setMsgStatus(MsgSendStatusEnum.SUCCESS);
        msgService.save(msg);
    }
}
