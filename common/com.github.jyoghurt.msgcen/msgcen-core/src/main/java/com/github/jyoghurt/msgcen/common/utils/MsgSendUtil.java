package com.github.jyoghurt.msgcen.common.utils;

import com.github.jyoghurt.msgcen.exception.MsgException;
import com.github.jyoghurt.msgcen.service.MsgSendService;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * user:zjl
 * date: 2016/12/1.
 */
public class MsgSendUtil {

    private static MsgSendService msgSendService;

    private static MsgSendService getMsgSendService() {
        if (null == msgSendService) {
            msgSendService = (MsgSendService) SpringContextUtils.getBean("msgSendService");
        }
        return msgSendService;
    }

    /**
     * @param target      目标实体
     * @param triggerCode 模板编号
     * @param param       发送参数
     * @throws MsgException
     */
    public static void send(Object target, String triggerCode, JSONObject param) throws MsgException {
        if (target instanceof ArrayList<?>) {
            List<?> targetList = (ArrayList<?>) target;
            getMsgSendService().sendToTargetsByTriggerCode(targetList, triggerCode, param);
            return;
        }
        getMsgSendService().sendByTriggerCode(target, triggerCode, param);
    }
}
