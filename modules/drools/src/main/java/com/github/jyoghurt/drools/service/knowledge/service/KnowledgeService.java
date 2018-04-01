package com.github.jyoghurt.drools.service.knowledge.service;


import com.github.jyoghurt.drools.service.knowledge.exception.DroolsRuleErrorException;
import org.drools.KnowledgeBase;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * user:zjl
 * date: 2016/12/29.
 */
public interface KnowledgeService {
    /**
     * 获得规则引擎基本接口
     * 根据规则文件路径
     *
     * @param drlPath 规则文件路径
     * @return 规则引擎基本接口
     */
    KnowledgeBase getKnowledgeBase(String drlPath) throws DroolsRuleErrorException;

    /**
     * 获得规则引擎 当前规则session
     * 根据规则文件路径
     *
     * @param drlPath 规则文件路径
     * @return 当前规则session
     */
    StatefulKnowledgeSession getDroolsSession(String drlPath) throws DroolsRuleErrorException;
}
