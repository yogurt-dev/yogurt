package com.github.jyoghurt.activiti.business.utils;

import com.github.jyoghurt.activiti.business.enums.CompontentShowType;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * user:DELL
 * date:2017/5/18.
 */
class JsUtil {
    private static Logger logger = LoggerFactory.getLogger(JsUtil.class);

    /**
     * 解析规则  并返回执行结果
     *
     * @param rule 规则
     * @return 执行规则后的返回对象
     */
    static Boolean executeRule(JSONObject user, JSONObject vars, CompontentShowType showType, String rule) {
        try {
            ScriptEngineManager engineManager = new ScriptEngineManager();
            ScriptEngine engine = engineManager.getEngineByName("JavaScript");
            engine.eval("function run(securityUserT,vars){" +
                    "var showType='" + showType.name() +"'; "+
                    "function include(attr,target,str){" +
                    "for(var i=0;i<attr.length;i++){ if(!attr[i][target]){  continue;}if(attr[i][target]==str){return true} } return false}" + rule + "}");
            Object o = engine.eval("run(" + user + "," + vars +" );");
            if (null == o) {
                return false;
            }
            return (Boolean) o;
        } catch (Exception e) {
            logger.error("流程引擎解析按钮规则异常", e);
            return false;
        }
    }
}
