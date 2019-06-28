package com.github.yogurt.core.service.impl;

import com.github.yogurt.core.dao.BaseDAO;
import com.github.yogurt.core.po.BasePO;
import com.github.yogurt.core.service.BaseDeleteService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: jtwu
 * @Date: 2019/6/27 11:28
 */
public abstract class BaseDeleteServiceImpl<T extends BasePO>  extends BaseServiceImpl  implements BaseDeleteService<T> {

	@Override
	public void deleteById(Long id) {
		baseDAO.deleteById(id);
	}

	@Override
	public void delete(T po) {
		baseDAO.delete(po);
	}

	@Override
	public void deleteAll(Iterable pos) {
		baseDAO.deleteAll(pos);
	}
}
