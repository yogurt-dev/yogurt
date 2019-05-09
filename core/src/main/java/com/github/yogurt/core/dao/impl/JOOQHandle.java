package com.github.yogurt.core.dao.impl;

import org.jooq.DSLContext;
import org.jooq.conf.RenderNameStyle;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @Author: jtwu
 * @Date: 2019/5/9 19:04
 */
public class JOOQHandle {

	protected static final String ALIAS = "t";

	@Autowired
	protected DSLContext dsl;

	@PostConstruct
	private void init() {
//		去掉sql中的单引号
		dsl.settings().withRenderNameStyle(RenderNameStyle.AS_IS);
	}
}
