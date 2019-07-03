package com.github.yogurt.cg;

import lombok.Data;

/**
 * @Author: jtwu
 * @Date: 2019/6/19 11:56
 */
@Data
public class Configuration {
	private String jdbcUrl;
	private String jdbcUser;
	private String jdbcPassword;
	private String tableName;
	private String tableSchema;
	private String packageName;
	private String directory;
	private String discriminatorValue;
}
