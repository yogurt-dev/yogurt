package com.github.yogurt.cg;


import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

/**
 * @author jtwu
 */
class CommonPageParser {
	private CommonPageParser() {
		throw new IllegalStateException("Utility class");
	}

	private static Logger logger = LoggerFactory.getLogger(CommonPageParser.class);
	private static final String CONTENT_ENCODING = "UTF-8";
	private static Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
	private static Map<String, Object> context;

	static void writerPage(Map map, String templateName, String fileDirPath, String targetFile) throws IOException, TemplateException {
		File file = new File(fileDirPath + targetFile);
		if (!file.exists()) {
			new File(file.getParent()).mkdirs();
		} else {
			logger.info(fileDirPath + targetFile + "文件已存在!");
			return;
		}
		Template temp = cfg.getTemplate(templateName);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, CONTENT_ENCODING));
		temp.process(map, writer);
		writer.flush();
		writer.close();
		fos.close();

		logger.info("Generating " + StringUtils.substringBefore(templateName, "."), StringUtils.substringAfterLast(targetFile, File.separator));
	}

	public static void init(String basedir, Map<String, Object> context) throws IOException {

// Specify the source where the template files come from. Here I set a
// plain directory for it, but non-file-system sources are possible too:

		cfg.setClassForTemplateLoading(CommonPageParser.class, "/");
		ClassTemplateLoader classTemplateLoader = new ClassTemplateLoader(CommonPageParser.class, "/");

// Set the preferred charset template files are stored in. UTF-8 is
// a good choice in most applications:
		cfg.setDefaultEncoding(CONTENT_ENCODING);

// Sets how errors will appear.
// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(new File(basedir+File.separator+"src/main/resources"));
		TemplateLoader[] templateLoader = {fileTemplateLoader,classTemplateLoader};
		MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(templateLoader);
		cfg.setTemplateLoader(multiTemplateLoader);
		CommonPageParser.context = context;
	}
}
