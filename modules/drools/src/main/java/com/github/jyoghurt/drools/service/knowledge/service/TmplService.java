package com.github.jyoghurt.drools.service.knowledge.service;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

/**
 * user:zjl
 * date: 2016/12/29.
 */
public interface TmplService {
    /**
     * 根据模板名称 获得模板对象
     *
     * @param moduleName 模块名称
     * @param vmName     模板名称
     * @return 模板对象
     */
    Template generateTemplate(String moduleName, String vmName);

    /**
     * 生成模板
     *
     * @param velocityContext 模板内容上下文
     * @param template        模板对象
     * @param filePath        生成位置
     */
    void buildTmpl(VelocityContext velocityContext, Template template, String filePath);
}
