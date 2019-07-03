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
		int pageNumber = pageable.getPageNumber() > 1 ? pageable.getPageNumber() - 1 : 0;
		jpaQuery = (JPAQuery)jpaQuery.limit(pageable.getPageSize()).offset(pageNumber * pageable.getPageSize());
		return new PageImpl<>(jpaQuery.fetch(), pageable, jpaQuery.fetchCount());
	}
}