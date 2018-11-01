package com.github.yogurt.cg;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Map;

@Slf4j
public class CommonPageParser {


    private final static String CONTENT_ENCODING = "UTF-8";
    private static Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);

    private static boolean isReplace = true; // 是否可以替换文件 true =可以替换，false =不可以替换

    static {


// Specify the source where the template files come from. Here I set a
// plain directory for it, but non-file-system sources are possible too:

        cfg.setClassForTemplateLoading(CommonPageParser.class, "/");

// Set the preferred charset template files are stored in. UTF-8 is
// a good choice in most applications:
        cfg.setDefaultEncoding(CONTENT_ENCODING);

// Sets how errors will appear.
// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
//        cfg.setClassicCompatible(true);

    }

    public static void writerPage(Map map, String templateName, String fileDirPath, String targetFile) throws Exception {
            File file = new File(fileDirPath + targetFile);
            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
            } else {
                if (isReplace) {
                    log.info("替换文件 " + file.getAbsolutePath());
                } else {
                    log.info("生成失败 " + file.getAbsolutePath() + "文件已存在");
                    return;
                }
            }

            Template temp = cfg.getTemplate(templateName);
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, CONTENT_ENCODING));
            temp.process(map,writer);
//            template.merge(context, writer);
            writer.flush();
            writer.close();
            fos.close();
            log.info("生成成功 " + file.getAbsolutePath());

    }

}
