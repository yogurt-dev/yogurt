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
public interface BaseDeleteService<T extends BasePO> extends BaseService{
	/**
	 * 根据id删除
	 * @param id 主键
	 */
	void deleteById(Long id);

	/**
	 * 删除实体
	 * @param po 业务实体
	 */
	void delete(T po);

	/**
	 * 批量删除
	 * @param pos
	 */
	void deleteAll(Iterable<? extends T> pos);
}
