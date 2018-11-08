package com.github.yogurt.core.service;


import com.github.yogurt.core.exception.ServiceException;
import com.github.yogurt.core.po.BasePO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

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
	 * @throws ServiceException 业务异常
	 */
	void save(T po) throws ServiceException;

	/**
	 * 更新实体
	 *
	 * @param po 持久化对象
	 */
	void update(T po);

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
	void logicDelete(Long id) throws ServiceException;

	/**
	 * 根据ID获取实体
	 *
	 * @param id 主键
	 * @return 持久化对象
	 */
	<F extends Serializable> T findById(F id);

	/**
	 * 查询所有记录
	 *
	 * @return 持久化对象集合
	 */
	List<T> findAll();

	/**
	 * 分页查询
	 *
	 * @param po       持久化对象
	 * @param pageable 分页
	 * @return spring-data的分页对象
	 */
	Page<T> list(T po, Pageable pageable);

	/**
	 * 批量保存
	 *
	 * @param poList 持久化对象列表
	 */
	void batchSave(List<T> poList);

	/**
	 * 批量更新
	 *
	 * @param poList 持久化对象列表
	 */
	void batchUpdate(List<T> poList);


}
