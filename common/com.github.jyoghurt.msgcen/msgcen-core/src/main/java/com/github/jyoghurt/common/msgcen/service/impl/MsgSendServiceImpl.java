package com.github.jyoghurt.common.msgcen.service.impl;

import com.github.jyoghurt.common.msgcen.common.convert.MsgConvert;
import com.github.jyoghurt.common.msgcen.common.utils.MsgRegularUtil;
import com.github.jyoghurt.common.msgcen.common.utils.MsgTriggerRuleParseUtil;
import com.github.jyoghurt.common.msgcen.domain.MsgTirggerT;
import com.github.jyoghurt.common.msgcen.domain.MsgTmplT;
import com.github.jyoghurt.common.msgcen.exception.MsgException;
import com.github.jyoghurt.common.msgcen.factory.MsgAdapter;
import com.github.jyoghurt.common.msgcen.factory.MsgAdapterFactory;
import com.github.jyoghurt.common.msgcen.service.MsgSendService;
import com.github.jyoghurt.common.msgcen.service.MsgTirggerService;
import com.github.jyoghurt.common.msgcen.service.MsgTmplService;
import com.github.jyoghurt.core.exception.BaseErrorException;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * user:zjl
 * date: 2016/11/17.
 */
@Service("msgSendService")
public class MsgSendServiceImpl implements MsgSendService {
    @Autowired
    private MsgTirggerService msgTirggerService;
    @Autowired
    private MsgTmplService msgTmplService;

    /**
     * @param target      发送对象
     * @param triggerCode 触发器编号
     * @param param       发送参数
     * @throws MsgException
     */
    @Override
    public void sendByTriggerCode(Object target, String triggerCode, JSONObject param) throws MsgException {
        try {
            List targets = new ArrayList<>();
            targets.add(target);
            this.sendToTargetsByTriggerCode(targets, triggerCode, param);
        } catch (Exception e) {
            throw new MsgException(e);
        }
    }

    /**
     * @param targets     发送对象集合
     * @param triggerCode 触发器编号
     * @param param       发送参数
     * @throws MsgException
     */
    @Override
    public void sendToTargetsByTriggerCode(List targets, String triggerCode, JSONObject param) throws MsgException {
        MsgTirggerT tirgger = msgTirggerService.find(triggerCode);
        if (!tirgger.getTirggerStatus()) {
            return;
        }
        Object returnMsg;
        //触发器规则常规参数解析
        String commonRule = MsgRegularUtil.replaceDoubleContentByJson(tirgger.getTirggerRule(), param);
        //发送模板及目标集合
        Map<String, List> sendMap = new HashMap<>();
        //根据发送实体解析规则
        for (Object target : targets) {
            if (null == target) {
                continue;
            }
            try {
                returnMsg = MsgTriggerRuleParseUtil.parseTriggerRule(
                        MsgRegularUtil.replaceDoubleContentByT(commonRule, target)
                );
                if (null == returnMsg) {
                    continue;
                }
                if ("".equals(returnMsg.toString())) {
                    continue;
                }
                MsgConvert.convertMsgToSendMap(sendMap, returnMsg.toString());
            } catch (ScriptException e) {
                throw new BaseErrorException("触发器规则解析失败,触发器code:{0},参数:{1}", triggerCode, param.toString());
            }
        }
        this.send(sendMap, param);
    }

    /**
     * @param sendMap 发送目标集合
     * @param param   发送参数
     * @throws MsgException
     */
    private void send(Map<String, List> sendMap, JSONObject param) throws MsgException {
        for (Map.Entry<String, List> entry : sendMap.entrySet()) {
            System.out.println(entry.getKey() + "--->" + entry.getValue());
            MsgTmplT msgTmpl = msgTmplService.find(entry.getKey());
            if (null == msgTmpl) {
                throw new BaseErrorException("根据模板Code未获得对应模板,模板Code:{0}", entry.getKey());
            }
            if(!msgTmpl.getTmplStatus()){
                return;
            }
            MsgAdapter adapter = MsgAdapterFactory.produce(msgTmpl.getTmplType());
            try {
                adapter.send(entry.getValue(), msgTmpl, param);
            } catch (Exception e) {
                throw new MsgException(e);
            }
        }
    }


}
