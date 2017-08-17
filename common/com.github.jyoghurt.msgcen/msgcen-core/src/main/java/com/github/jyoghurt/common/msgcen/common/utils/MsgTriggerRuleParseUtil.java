package com.github.jyoghurt.common.msgcen.common.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * user:zjl
 * date: 2016/11/16.
 */
public class MsgTriggerRuleParseUtil {
    /**
     * 解析规则  并返回执行结果
     *
     * @param rule 触发器规则
     * @return 执行规则后的返回对象
     * @throws ScriptException
     */
    public static Object parseTriggerRule(String rule) throws ScriptException {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine engine = engineManager.getEngineByName("JavaScript");
        engine.eval("function run(param){ " + rule + "}");
        return engine.eval("run();");
    }
}
