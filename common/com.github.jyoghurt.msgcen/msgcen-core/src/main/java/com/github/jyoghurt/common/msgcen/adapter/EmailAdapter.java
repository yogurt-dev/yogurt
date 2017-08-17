package com.github.jyoghurt.common.msgcen.adapter;

import com.github.jyoghurt.common.msgcen.adapter.target.MsgTarget;
import com.github.jyoghurt.common.msgcen.common.constants.EmailConstants;
import com.github.jyoghurt.common.msgcen.common.email.EmailUpLoadSupport;
import com.github.jyoghurt.common.msgcen.common.utils.MsgRegularUtil;
import com.github.jyoghurt.common.msgcen.common.utils.MsgTmplRuleParseUtil;
import com.github.jyoghurt.common.msgcen.domain.MsgTmplT;
import com.github.jyoghurt.common.msgcen.factory.MsgAdapter;
import com.github.jyoghurt.dataDict.service.DataDictUtils;
import com.github.jyoghurt.email.domain.EmailMsg;
import com.github.jyoghurt.emailPlugin.common.util.EmailPluginUtil;
import com.github.jyoghurt.emailPlugin.enums.EmailConfig;
import com.github.jyoghurt.sw.handler.SwitchHandler;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * user:zjl
 * date: 2016/11/17.
 */
@Component
public class EmailAdapter extends EmailPluginUtil implements MsgAdapter, MsgTarget {
    /**
     * @param to      发送目标集合
     * @param msgTmpl 消息模板
     * @param param   发送参数
     */
    @Override
    public void send(List<String> to, MsgTmplT msgTmpl, JSONObject param) {
        //适配封装email消息
        EmailMsg emailMsg = buildEmailBusinessMsg(to, msgTmpl, param);
        //封装基类信息
        buildBaseEmailMsg(emailMsg, param);
        //发送email
        if (SwitchHandler.switchIsOpenBySwitchGroupKey("msgcen")) {
            EmailPluginUtil.pushEmailMsg(emailMsg);
        }
        String address = "";
        for (String str : to) {
            address += str + ",";
        }
        recordMsg(address.substring(0, address.length() - 1), msgTmpl, JSONObject.fromObject(param).toString(), null);
    }

    /**
     * 封装email发送业务信息
     *
     * @param to      发送目标集合
     * @param msgTmpl 消息模板
     * @param param   发送参数
     */
    private EmailMsg buildEmailBusinessMsg(List<String> to, MsgTmplT msgTmpl, JSONObject param) {
        EmailMsg emailMsg = new EmailMsg();
        //封装发送人列表
        emailMsg.setTargetAddress(to);
        //解析消息模板参数
        JSONObject tmplParam = MsgTmplRuleParseUtil.parseTmplRule(msgTmpl, param);
        //封装消息发送内容
        emailMsg.setEmailContent(
                //解析模板内容，将参数替换至模板
                MsgRegularUtil.replaceEmailDoubleContent(msgTmpl.getTmplContent(), param, tmplParam)
        );
        //封装附件
        if (null != param.get(EmailConstants.UPLOAD_FILES)) {
            String serviceName = param.get(EmailConstants.UPLOAD_FILES).toString();
            EmailUpLoadSupport emailUpLoadSupport = (EmailUpLoadSupport) SpringContextUtils.getBean(serviceName);
            List<File> uploadFiles = emailUpLoadSupport.upload();
            emailMsg.setFileList(uploadFiles);
        }
        emailMsg.setSubject(null == param.get("subject") ? msgTmpl.getTmplSubject() : param.get("subject").toString());
        return emailMsg;
    }

    /**
     * 封装email基类信息
     *
     * @param emailMsg email发送对象
     * @param param    发送参数
     * @return 封装后的email发送对象
     */
    private EmailMsg buildBaseEmailMsg(EmailMsg emailMsg, JSONObject param) {
        if (null == emailMsg) {
            emailMsg = new EmailMsg();
        }
        emailMsg.setHost(null == param.get(EmailConfig.EMAILHOST.name())
                ? DataDictUtils.getDataDictValue(EmailConfig.EMAILHOST).getDictValueName()
                : param.get(EmailConfig.EMAILHOST.name()).toString());
        emailMsg.setUserName(null == param.get(EmailConfig.SENDERACCOUNT.name())
                ? DataDictUtils.getDataDictValue(EmailConfig.SENDERACCOUNT).getDictValueName()
                : param.get(EmailConfig.SENDERACCOUNT.name()).toString());
        emailMsg.setPassWord(null == param.get(EmailConfig.SENDERPASSWORD.name())
                ? DataDictUtils.getDataDictValue(EmailConfig.SENDERPASSWORD).getDictValueName()
                : param.get(EmailConfig.SENDERPASSWORD.name()).toString());
        emailMsg.setShowName(null == param.get(EmailConfig.SHOWNAME.name())
                ? DataDictUtils.getDataDictValue(EmailConfig.SHOWNAME).getDictValueName()
                : param.get(EmailConfig.SHOWNAME.name()).toString());
        emailMsg.setHost(DataDictUtils.getDataDictValue(EmailConfig.EMAILHOST).getDictValueName());
        return emailMsg;
    }

}
