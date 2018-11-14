package com.github.yogurt.cg;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * @author jtwu
 */
@Data
@Accessors(chain = true)
public class ClassDefinition {
	/**
	 * 类名
	 */
	private String className;
	/**
	 * 包名
	 */
	private String packageName;
	/**
	 * 注释
	 */
	private String comment;
	/**
	 * 字段列表
	 */
	private List<FieldDefinition> fieldDefinitions;

	/**
	 * 主键
	 */
	private List<FieldDefinition> priKeys;
}
