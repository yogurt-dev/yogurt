package com.github.jyoghurt.common.msgcen.service;

import com.github.jyoghurt.common.msgcen.exception.MsgException;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * user:zjl
 * date: 2016/11/17.
 */
public interface MsgSendService {
    /**
     * 发送信息
     *
     * @param target      目标实体
     * @param triggerCode 触发器编号
     * @param param       发送参数
     */
    void sendByTriggerCode(Object target, String triggerCode, JSONObject param) throws MsgException;

    /**
     * 发送信息
     *
     * @param targets     目标实体集合
     * @param triggerCode 触发器编号
     * @param param       发送参数
     */
    void sendToTargetsByTriggerCode(List targets, String triggerCode, JSONObject param) throws MsgException;
}
