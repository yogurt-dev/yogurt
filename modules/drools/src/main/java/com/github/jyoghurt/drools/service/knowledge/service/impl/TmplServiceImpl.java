package com.github.jyoghurt.drools.service.knowledge.service.impl;

import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.drools.service.knowledge.service.TmplService;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

/**
 * user:zjl
 * date: 2016/12/29.
 */
@Service("tmplService")
public class TmplServiceImpl implements TmplService {
    /**
     * @param moduleName 模块名称
     * @param vmName     模板名称
     * @return 模板对象
     */
    @Override
    public Template generateTemplate(String moduleName, String vmName) {
        // 项目跟路径路径，此处修改为你的项目路径
        String templateBasePath = TmplServiceImpl.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        Properties properties = new Properties();
        properties.setProperty(Velocity.RESOURCE_LOADER, "jar");
        properties.setProperty("jar.resource.loader.class", "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
        properties.setProperty("jar.resource.loader.path", "jar:file:" + templateBasePath);
        properties.setProperty("file.resource.loader.modificationCheckInterval", "30");
        properties.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
        properties.setProperty("runtime.log.logsystem.log4j.logger", "org.apache.velocity");
        properties.setProperty("directive.set.null.allowed", "true");
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init(properties);
        String path = "tmpl/" + moduleName + "/" + vmName;
        return velocityEngine.getTemplate(path, "UTF-8");
    }

    /**
     * @param velocityContext 模板内容上下文
     * @param template        模板对象
     * @param filePath        生成位置
     */
    @Override
    public void buildTmpl(VelocityContext velocityContext, Template template, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            new File(file.getParent()).mkdirs();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            template.merge(velocityContext, writer);
            writer.flush();
            writer.close();
            fos.close();
        } catch (Exception e) {
            throw new BaseErrorException("生成模板失败", e);
        }
    }
}
