package com.github.jyoghurt.msgcen.common.utils;

import com.github.jyoghurt.core.exception.BaseErrorException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * user:zjl
 * date: 2016/12/12.
 */
public class ScriptUtil {
    /**
     * 解析js内容  并返回执行结果
     *
     * @return 执行规则后的返回的结果
     */
    public static String executeScript(String fn) {
        try {
            ScriptEngineManager engineManager = new ScriptEngineManager();
            ScriptEngine engine = engineManager.getEngineByName("JavaScript");
            return engine.eval(fn).toString();
        } catch (Exception e) {
            throw new BaseErrorException("执行模板参数中的js失败,fn:{}", fn);
        }
    }
}
