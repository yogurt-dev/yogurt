package com.github.yogurt.core.dao.impl;

import com.github.yogurt.core.dao.BaseDAO;
import com.github.yogurt.core.exception.BaseAccidentException;
import com.github.yogurt.core.exception.BaseErrorException;
import com.github.yogurt.core.exception.DaoException;
import com.github.yogurt.core.po.BasePO;
import com.github.yogurt.core.utils.QueryDSLUtils;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @Author: jtwu
 * @Date: 2019/6/26 10:20
 */

public  class BaseDAOImpl<T extends BasePO> extends SimpleJpaRepository<T, Long> implements BaseDAO<T> {

	private final EntityManager entityManager;
	private JPAQueryFactory queryFactory;

	public BaseDAOImpl(JpaEntityInformation<T, ?> entityInformation
			, EntityManager em) {
		super(entityInformation, em);
		this.entityManager = em;
		queryFactory = new JPAQueryFactory(entityManager);
	}

	@Override
	public JPAQueryFactory getQueryFactory() {
		return queryFactory;
	}

	@Override
	public List<T> findAll(T po,EntityPathBase<T>  entityPathBase) {
		return queryFactory
				.selectFrom(entityPathBase)
				.where(QueryDSLUtils.getPredicatesByNotNullFileds(po, entityPathBase))
				.fetch();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<T> findAll(T po, Pageable pageable, EntityPathBase<T> entityPathBase) {
		return BasePageHandle.fetch(
				queryFactory
				.selectFrom(entityPathBase)
				.where(QueryDSLUtils.getPredicatesByNotNullFileds(po, entityPathBase))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize()),pageable);

	}
}
