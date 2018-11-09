package com.github.yogurt.cg;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: jtwu
 * @Date: 2018/11/9 9:25
 *
 * 枚举描述类
 */
@Data
@Accessors(chain = true)
public class EnumFieldDefinition {
	private String name;
	private String annotation;
}
