package com.github.jyoghurt.generator.java;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;


public class CommonPageParser {

	private static VelocityEngine ve;// = VelocityEngineUtil.getVelocityEngine();

	private final static String CONTENT_ENCODING = "UTF-8";

	private static final Log log = LogFactory.getLog(CommonPageParser.class);

	private static boolean isReplace = true; // 是否可以替换文件 true =可以替换，false =不可以替换


	public static String getRootPath() {
		String rootPath = "";
		try {
			File file = new File(CommonPageParser.class.getResource("/").getFile());
			rootPath = file.getParent();
			rootPath = java.net.URLDecoder.decode(rootPath.substring(0, rootPath.indexOf("target") - 1), "utf-8");
			return rootPath;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rootPath;
	}

	public static void main(String[] args) {
		System.out.println(getRootPath());
	}

	static {
		try {
			// 获取文件模板根路径
			String templateBasePath = CommonPageParser.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			Properties properties = new Properties();
			if (templateBasePath.endsWith(".jar")) {
				properties.setProperty(Velocity.RESOURCE_LOADER, "jar");
				properties.setProperty("jar.resource.loader.class", "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
				properties.setProperty("jar.resource.loader.path", "jar:file:" + templateBasePath);
			} else {
				properties.setProperty(Velocity.RESOURCE_LOADER, "file");
				properties.setProperty("file.resource.loader.description", "Velocity File Resource Loader");
				properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, templateBasePath);
				properties.setProperty(Velocity.FILE_RESOURCE_LOADER_CACHE, "true");
			}
			properties.setProperty("file.resource.loader.modificationCheckInterval", "30");
			properties.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS,
					"org.apache.velocity.runtime.log.Log4JLogChute");
			properties.setProperty("runtime.log.logsystem.log4j.logger", "org.apache.velocity");
			properties.setProperty("directive.set.null.allowed", "true");
			VelocityEngine velocityEngine = new VelocityEngine();
			velocityEngine.init(properties);
			ve = velocityEngine;
		} catch (Exception e) {
			log.error(e);
		}
	}

	public static void WriterPage(VelocityContext context, String templateName, String fileDirPath, String targetFile) {
		try {
			File file = new File(fileDirPath + targetFile);
			if (!file.exists()) {
				new File(file.getParent()).mkdirs();
			} else {
				if (isReplace) {
					System.out.println("替换文件" + file.getAbsolutePath());
				} else {
					System.out.println("页面生成失败" + file.getAbsolutePath() + "文件已存在");
					return;
				}
			}

			Template template = ve.getTemplate(templateName, CONTENT_ENCODING);
			FileOutputStream fos = new FileOutputStream(file);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, CONTENT_ENCODING));
			template.merge(context, writer);
			writer.flush();
			writer.close();
			fos.close();
			System.out.println("页面生成成功" + file.getAbsolutePath());
		} catch (Exception e) {
			log.error("", e);
		}
	}

}
