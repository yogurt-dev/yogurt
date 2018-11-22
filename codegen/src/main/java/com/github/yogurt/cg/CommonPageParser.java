package com.github.yogurt.cg;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.jooq.tools.JooqLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * @author jtwu
 */
public class CommonPageParser {

	private static JooqLogger logger =  JooqLogger.getLogger(CommonPageParser.class);
	private final static String CONTENT_ENCODING = "UTF-8";
	private static Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);

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

	static void writerPage(Map map, String templateName, String fileDirPath, String targetFile) throws Exception {
		File file = new File(fileDirPath + targetFile);
		if (!file.exists()) {
			new File(file.getParent()).mkdirs();

		}
		Template temp = cfg.getTemplate(templateName);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, CONTENT_ENCODING));
		temp.process(map, writer);
		writer.flush();
		writer.close();
		fos.close();

		logger.info("Generating " + StringUtils.substringBefore(templateName,"."),StringUtils.substringAfterLast(targetFile,File.separator));
	}

}
