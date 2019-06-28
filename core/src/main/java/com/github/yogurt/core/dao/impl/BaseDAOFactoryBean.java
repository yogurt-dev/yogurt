package com.github.yogurt.core.dao.impl;

import com.github.yogurt.core.po.BasePO;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;

/**
 * @Author: jtwu
 * @Date: 2019/6/26 10:30
 */
public class BaseDAOFactoryBean<T extends BasePO,R extends Repository<T, Long>>  extends JpaRepositoryFactoryBean<R , T, Long> {
	/**
	 * Creates a new {@link JpaRepositoryFactoryBean} for the given repository interface.
	 *
	 * @param repositoryInterface must not be {@literal null}.
	 */
	public BaseDAOFactoryBean(Class repositoryInterface) {
		super(repositoryInterface);
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		return new CustomRepositoryFactory(entityManager);
	}
	private static class CustomRepositoryFactory extends JpaRepositoryFactory {

		CustomRepositoryFactory(EntityManager entityManager) {
			super(entityManager);
		}

//		@Override
//		@SuppressWarnings({"unchecked"})
//		protected  SimpleJpaRepository getTargetRepository(
//				RepositoryInformation information, EntityManager entityManager) {// 获得当前自定义类的实现
//			return new BaseDAOImpl(information, entityManager);
//
//		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {// 获得当前自定义类的类型
			return BaseDAOImpl.class;
		}
	}
}
