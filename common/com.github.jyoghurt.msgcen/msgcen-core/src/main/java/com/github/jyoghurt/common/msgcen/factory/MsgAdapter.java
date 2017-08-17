package com.github.jyoghurt.common.msgcen.factory;

import com.github.jyoghurt.common.msgcen.domain.MsgTmplT;
import com.github.jyoghurt.common.msgcen.exception.MsgException;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * user:zjl
 * date: 2016/11/17.
 */
public interface MsgAdapter {

    /**
     * 发送信息
     *
     * @param to      发送目标集合
     * @param msgTmpl 消息模板
     * @param param   发送参数
     */
    void send(List<String> to, MsgTmplT msgTmpl, JSONObject param) throws MsgException;
}
