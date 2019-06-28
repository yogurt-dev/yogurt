package com.github.yogurt.core.dao.impl;

import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


/**
 * 分页功能实现
 * @author Administrator
 */
public abstract class BasePageHandle {
	public static Page fetch(JPAQuery jpaQuery,Pageable pageable) {
		return new PageImpl<>(jpaQuery.fetch(), pageable, jpaQuery.fetchCount());
	}
}