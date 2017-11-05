package com.github.jyoghurt.drools.service.knowledge.service.impl;



import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.drools.service.knowledge.exception.DroolsRuleErrorException;
import com.github.jyoghurt.drools.service.knowledge.service.KnowledgeService;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.*;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * user:zjl
 * date: 2016/12/29.
 */
@Service("knowledgeService")
public class KnowledgeServiceImpl implements KnowledgeService {
    /**
     * @param drlPath 规则文件路径
     * @return 根据规则文件路径
     */
    @Override
    public KnowledgeBase getKnowledgeBase(String drlPath) throws DroolsRuleErrorException {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();//创建规则构建器
        File file = new File(drlPath);
        int conter = 0;
        do {
            if (!file.exists()) {
                if (conter == 2) {
                    throw new BaseErrorException("无法找到规则文件,文件路径:{0}", drlPath);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                conter++;
            }
            break;
        } while (conter < 3);
        Resource r = ResourceFactory.newFileResource(file);
        knowledgeBuilder.add(r, ResourceType.DRL);//加载规则文件，并增加到构建器
        KnowledgeBuilderErrors errors = knowledgeBuilder.getErrors();
        if (errors.size() > 0) {//编译规则过程中发现规则是否有错误
            String errorMessage = "";
            for (KnowledgeBuilderError error : errors) {
                errorMessage += error;
            }
            throw new DroolsRuleErrorException();
        }
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();//创建规则构建库
        knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());//构建器加载的资源文件包放入构建库
        return knowledgeBase;
    }

    /**
     * @param drlPath 规则文件路径
     * @return 当前规则session
     * @throws DroolsRuleErrorException
     */
    @Override
    public StatefulKnowledgeSession getDroolsSession(String drlPath) throws DroolsRuleErrorException {
        KnowledgeBase knowledgeBase = getKnowledgeBase(drlPath);
        return knowledgeBase.newStatefulKnowledgeSession();
    }
}
