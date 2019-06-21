package com.github.yogurt.core.service;


import com.github.yogurt.core.exception.ServiceException;
import com.github.yogurt.core.po.BasePO;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;

/**
 * service层基类，提供单表的crud接口
 *
 * @param <T> PO类型
 * @author jtwu
 */
public interface BaseService<T extends BasePO> extends PagingAndSortingRepository<T, Long>  {


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
	 * @param po 持久化对象
	 */
	void setCreator(BasePO po);

	/**
	 * 设置修改人
	 * @param po 持久化对象
	 */
	void setModifier(BasePO po);
}
