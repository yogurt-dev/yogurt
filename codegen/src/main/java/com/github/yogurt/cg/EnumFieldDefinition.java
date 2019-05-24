package com.github.yogurt.cg;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: jtwu
 * @Date: 2018/11/9 9:25
 *
 * 枚举描述类
 */
@Data
@Accessors(chain = true)
public class EnumFieldDefinition implements Serializable {
	private static final long serialVersionUID = 2405172041950251807L;
	private String name;
	private String annotation;
}
