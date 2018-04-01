package com.github.jyoghurt.wechatbasic.common.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.Map;

public class FreeMarkerUtil {
    private static FreeMarkerUtil freeMarkerUtil = new FreeMarkerUtil();

    private FreeMarkerUtil() {
    }

    public Configuration getFreemarker_cfg() {
        return freemarker_cfg;
    }

    public void setFreemarker_cfg(Configuration freemarker_cfg) {
        this.freemarker_cfg = freemarker_cfg;
    }

    public Log getLogger() {
        return logger;
    }

    public static FreeMarkerUtil getInstance() {
        return freeMarkerUtil;
    }

    public FreeMarkerUtil getFreeMarkerUtil() {
        return freeMarkerUtil;
    }

    public void setFreeMarkerUtil(FreeMarkerUtil freeMarkerUtil) {
        this.freeMarkerUtil = freeMarkerUtil;
    }

    private final Log logger = LogFactory.getLog(getClass());
    //配置实例
    private Configuration freemarker_cfg = null;


    protected Configuration getFreeMarkerCFG(String mobanurl) {
//        if (null == freemarker_cfg) {
            freemarker_cfg = new Configuration();
            freemarker_cfg.setDefaultEncoding("UTF-8");

            WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
            ServletContext context = webApplicationContext.getServletContext();

            freemarker_cfg.setServletContextForTemplateLoading(context, mobanurl);
//        }

        return freemarker_cfg;
    }

    //参数：根路径，模块文件，封装好的list，生成html后存放的相对路径，生成html后的名字
    public boolean geneHtmlFile(String sRootDir, String mobanurl, String templateFileName, Map propMap, String
            htmlFilePath,
                                String
                                        htmlFileName) {


        try {
            Template t = getFreeMarkerCFG(mobanurl).getTemplate(templateFileName);
            t.setEncoding("UTF-8");

            creatDirs(sRootDir, htmlFilePath);

            File afile = new File(sRootDir + "/" + htmlFilePath + "/" + htmlFileName);

            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(afile), "UTF-8"));

            t.process(propMap, out);
            out.flush();
        } catch (TemplateException e) {
            logger.error("Error while processing FreeMarker template " + templateFileName, e);
            System.out.println(e.getMessage());
            return false;
        } catch (IOException e) {
            logger.error("Error while generate Static Html File " + htmlFileName, e);
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public static boolean creatDirs(String aParentDir, String aSubDir) {
        File aFile = new File(aParentDir);
        if (aFile.exists()) {
            File aSubFile = new File(aParentDir + aSubDir);
            if (!aSubFile.exists()) {
                return aSubFile.mkdirs();
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
