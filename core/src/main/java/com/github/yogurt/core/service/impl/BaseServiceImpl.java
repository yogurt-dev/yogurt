package com.github.yogurt.core.service.impl;

import com.github.yogurt.core.Configuration;
import com.github.yogurt.core.dao.BaseDAO;
import com.github.yogurt.core.exception.ServiceException;
import com.github.yogurt.core.po.BasePO;
import com.github.yogurt.core.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author jtwu
 */
@Slf4j
public class BaseServiceImpl<T extends BasePO> implements BaseService<T> {

	@Autowired
	private Configuration configuration;

	@Autowired
	protected BaseDAO<T> baseDAO;

	@Override
	public void save(T entity) {
		setCreator(entity);
		baseDAO.save(entity);
	}

	@Override
	public void update(T po) {
		setModifier(po);
		baseDAO.update(po);
	}

	@Override
	public void updateForSelective(T po) {
		setModifier(po);
		baseDAO.updateForSelective(po);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <F extends Serializable> void logicDelete(F id) {
		try {
			ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
			Class<T> clazz = (Class<T>) pt.getActualTypeArguments()[0];
//			单主键
			T t = clazz.newInstance();
			if (id instanceof Number) {
				updateForSelective((T) t.setId(((Number) id).longValue()).setDeleted(true));
				return;
			}
//			联合主键
			BeanUtils.copyProperties(id, t);
			updateForSelective((T) t.setDeleted(true));
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public <F extends Serializable> T findById(F id) {
		return baseDAO.findById(id);
	}

	@Override
	public List<T> findAll() {
		return baseDAO.findAll();
	}

	@Override
	public Page<T> list(T po, Pageable pageable) {
		return baseDAO.list(po, pageable);
	}

	@Override
	public void batchSave(List<T> poList) {
		poList.forEach(this::setCreator);
		baseDAO.batchSave(poList);
	}

	@Override
	public void batchUpdate(List<T> poList) {
		poList.forEach(this::setModifier);
		baseDAO.batchUpdate(poList);
	}

	/**
	 * 获取request
	 *
	 * @return HttpServletRequest
	 */
	private Object getSessionAttr(String attr) {
		return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
				.getRequest().getSession().getAttribute(attr);
	}

	@Override
	public void setCreator(BasePO po) {
		if (po == null) {
			return;
		}
		try {
			if (null == po.getCreatorId()) {
				po.setCreatorId((Long) getSessionAttr(configuration.getUserId()));
			}
		} catch (Exception e) {
			log.debug("setFounder时session获取失败!");
		}
	}

	@Override
	public void setModifier(BasePO po) {
		if (po == null) {
			return;
		}
		if (po.getGmtModified() == null) {
			po.setGmtModified(LocalDateTime.now());
		}
		try {
			if (null == po.getModifierId()) {
				Object sessionAttr = getSessionAttr(configuration.getUserId());
				po.setModifierId(sessionAttr == null ? 0L : (Long) sessionAttr);
			}
		} catch (Exception e) {
			log.debug("update时session获取失败!");
		}
	}

//
//    @Override
//    public void updateForSelective(Object po) {
//
//    }


}
