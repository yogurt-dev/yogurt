package com.github.yogurt.core.dao;


import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;


/**
 * @author jtwu
 */
@NoRepositoryBean
public interface BaseDAO<T> extends JpaRepository<T, Long> {


	/**
	 * 根据非空字段查询
	 *
	 * @param po             业务实体
	 * @param entityPathBase QueryDSL
	 * @return 业务实体列表
	 */
	List<T> findAll(T po, EntityPathBase<T> entityPathBase);

	/**
	 * 根据非空字段查询（分页）
	 *
	 * @param po             业务实体
	 * @param pageable       分页信息
	 * @param entityPathBase QueryDSL
	 * @return 业务实体列表
	 */
	Page<T> findAll(T po, Pageable pageable, EntityPathBase<T> entityPathBase);

	/**
	 * 获取JPAQueryFactory
	 */
	JPAQueryFactory getQueryFactory();
}
