package com.github.yogurt.cg;

import lombok.Data;

import java.util.Map;

/**
 * @Author: jtwu
 * @Date: 2019/6/28 9:39
 */
@Data
class FileTmplContext {
	private Configuration conf;
	private String poPath;
	private String qPoPath;
	private String voPath;
	private String aoPath;
	private String daoPath;
	private String daoImplPath;
	private String servicePath;
	private String serviceImplPath;
	private String controllerPath;
	private Map<String, Object> context;
	private String fileDirPath;
}
