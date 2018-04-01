package com.github.jyoghurt.msgcen.common.utils;

import com.github.jyoghurt.msgcen.domain.MsgTmplT;
import com.github.jyoghurt.core.exception.BaseErrorException;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;

/**
 * user:zjl
 * date: 2016/12/13.
 */
public class MsgTmplRuleParseUtil {

    /**
     * 解析规则  并返回执行结果
     *
     * @param msgTmpl 消息模板
     * @return 执行规则封装后的消息模板
     */
    public static JSONObject parseTmplRule(MsgTmplT msgTmpl, JSONObject param) {
        try {
            if (StringUtils.isEmpty(msgTmpl.getTmplParamRule())) {
                return new JSONObject();
            }
            String paramRule = MsgRegularUtil.replaceDoubleContent(msgTmpl.getTmplParamRule(), param);
            HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String callSourceEnumStr = httpServletRequest.getHeader("CallSource");
            ScriptEngineManager engineManager = new ScriptEngineManager();
            ScriptEngine engine = engineManager.getEngineByName("JavaScript");
            engine.eval("function run(callSource){ param={}; " +
                    "var CallSourceEnum = {\n" +
                    "EMPLOYEE_MANAGER: 'EMPLOYEE_MANAGER',\n" +
                    "MEMBER_PORTAL: 'MEMBER_PORTAL',\n" +
                    "MEMBER_WECHAT: 'MEMBER_WECHAT'" +
                    "};"
                    + paramRule + " return param;}");
            return JSONObject.fromObject(engine.eval("run('" + callSourceEnumStr + "');"));
        } catch (Exception e) {
            throw new BaseErrorException("模板参数规则解析失败，模板编号：{}", msgTmpl.getTmplCode());
        }
    }
}
