package com.github.yogurt.core.service;


import com.github.yogurt.core.exception.ServiceException;
import com.github.yogurt.core.po.BasePO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * service层基类，提供单表的crud接口
 *
 * @param <T> PO类型
 * @author jtwu
 */
public interface BaseService<T extends BasePO> {
	/**
	 * 保存实体
	 *
	 * @param po 待保存的实体
	 */
	void save(T po);

	/**
	 * 更新实体
	 *
	 * @param po 持久化对象
	 */
	void update(T po);

	/**
	 * 批量更新
	 *
	 * @param pos 持久化对象列表
	 */
	void saveAll(Iterable<T> pos);

	/**
	 * 根据ID获取实体
	 *
	 * @param id 主键
	 * @return 持久化对象
	 */
	Optional<T> findById(Long id);

	/**
	 * 查询所有记录
	 *
	 * @return 持久化对象集合
	 */
	List<T> findAll(T po);

	/**
	 * 查询所有记录
	 *
	 * @param pageable 分页
	 * @return 持久化对象集合
	 */
	Page<T> findAll(T po, Pageable pageable);

	/**
	 * 根据id列表查询
	 *
	 * @param ids id列表
	 * @return 实体集合
	 */
	Iterable<T> findAllById(Iterable<Long> ids);

	/**
	 * 查询总数
	 *
	 * @return
	 */
	long count();


	/**
	 * 更新非空字段
	 *
	 * @param po 持久化对象
	 */
	void updateForSelective(T po);


	/**
	 * 逻辑删除
	 *
	 * @param id 主键
	 * @throws ServiceException 业务异常
	 */
	<F extends Serializable> void logicDelete(F id) throws ServiceException;

	/**
	 * 设置创建人
	 *
	 * @param po 持久化对象
	 */
	void setCreator(BasePO po);

	/**
	 * 设置修改人
	 *
	 * @param po 持久化对象
	 */
	void setModifier(BasePO po);
}
