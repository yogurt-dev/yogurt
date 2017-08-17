package com.github.jyoghurt.common.msgcen.adapter;

import com.alibaba.fastjson.JSON;
import com.github.jyoghurt.common.msgcen.adapter.target.MsgTarget;
import com.github.jyoghurt.common.msgcen.common.utils.MsgRegularUtil;
import com.github.jyoghurt.common.msgcen.common.utils.MsgTmplRuleParseUtil;
import com.github.jyoghurt.common.msgcen.domain.MsgTmplT;
import com.github.jyoghurt.common.msgcen.exception.sms.SMSSendTooManyPhonesException;
import com.github.jyoghurt.common.msgcen.exception.sms.SMSTmplSendTooManyTimesException;
import com.github.jyoghurt.common.msgcen.exception.sms.SMSVerificationCodeOftenException;
import com.github.jyoghurt.common.msgcen.factory.MsgAdapter;
import com.github.jyoghurt.sms.SmsUtils;
import com.github.jyoghurt.sms.aliyun.AliyunSmsUtils;
import com.github.jyoghurt.sw.handler.SwitchHandler;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * user:zjl
 * date: 2016/11/17.
 */
public class SMSAdapter extends SmsUtils implements MsgAdapter, MsgTarget {
    /**
     * @param to      发送目标集合
     * @param msgTmpl 消息模板
     * @param param   发送参数
     */
    @Override
    public void send(List<String> to, MsgTmplT msgTmpl, JSONObject param) throws SMSTmplSendTooManyTimesException, SMSVerificationCodeOftenException, SMSSendTooManyPhonesException {
        //解析消息模板参数
        JSONObject tmplParam = MsgTmplRuleParseUtil.parseTmplRule(msgTmpl, param);
        //解析模板内容
        List<String> values = MsgRegularUtil.getSmsDoubleContent(msgTmpl.getTmplContent(), param, tmplParam);
        String targets = "";
        if (to.size() == 0) {
            return;
        }
        if (to.size() > 100) {
            throw new SMSSendTooManyPhonesException();
        }
        for (String userPhone : to) {
            targets += userPhone + ",";
        }
        try {
            //发送email
            if (SwitchHandler.switchIsOpenBySwitchGroupKey("msgcen")) {
//                SmsUtils.send(targets, msgTmpl.getTmplCode(), values.size() == 0 ? null : values.toArray(new String[values.size()]));
                AliyunSmsUtils.send(targets, msgTmpl.getSignName(), msgTmpl.getTmplCode(), values.size() == 0 ? null : values.toArray(new String[values.size()]));
            }
            recordMsg(targets, msgTmpl, JSON.toJSONString(param), null);
        } catch (Exception e) {
            recordMsg(targets, msgTmpl, JSON.toJSONString(param), e);
//        } catch (SmsException e) {
//            recordMsg(targets, msgTmpl, JSON.toJSONString(param), e);
        }
    }
}
