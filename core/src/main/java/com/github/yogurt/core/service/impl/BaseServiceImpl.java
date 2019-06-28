package com.github.yogurt.core.service.impl;

import com.github.yogurt.core.Configuration;
import com.github.yogurt.core.dao.BaseDAO;
import com.github.yogurt.core.exception.ServiceException;
import com.github.yogurt.core.po.BasePO;
import com.github.yogurt.core.service.BaseService;
import com.github.yogurt.core.utils.JpaUtils;
import com.querydsl.core.types.dsl.EntityPathBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author jtwu
 */
@Slf4j
public abstract class BaseServiceImpl<T extends BasePO> implements BaseService<T> {

	@Autowired
	private Configuration configuration;

	@Autowired
	protected BaseDAO<T> baseDAO;

	@Override
	public void save(T po) {
		setCreator(po);
		baseDAO.save(po);
	}

	@Override
	public void update(T po) {
		setModifier(po);
		//todo baseDAO提供update方法
		baseDAO.save(po);
	}

	@Override
	public void saveAll(Iterable<T> pos) {
		pos.forEach(this::setCreator);
		baseDAO.saveAll(pos);
	}

	@Override
	public Optional<T> findById(Long id) {
		return baseDAO.findById(id);
	}

	@Override
	public List<T> findAll(T po) {
		return baseDAO.findAll(po, getEntityPathBase());
	}

	@Override
	public Page<T> findAll(T po, Pageable pageable) {
		return baseDAO.findAll(po,pageable,getEntityPathBase());
	}

	@Override
	public Iterable<T> findAllById(Iterable<Long> ids) {
		return baseDAO.findAllById(ids);
	}

	@Override
	public long count() {
		return baseDAO.count();
	}

	@Override
	public void updateForSelective(T po) {
		setModifier(po);
		Optional<T> optional = findById(po.getId());
		optional.ifPresent(t -> {
			BeanUtils.copyProperties(po, t, JpaUtils.getNullProperties(po));
			this.save(t);
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <F extends Serializable> void logicDelete(F id) {
		try {
//			单主键
			T t = getInstance(this.getClass());
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

	private T getInstance(Class sourceClass) {
		Type type = sourceClass.getGenericSuperclass();

		if (!(type instanceof ParameterizedType)) {
			return getInstance(sourceClass.getSuperclass());
		}
		ParameterizedType pt = (ParameterizedType) type;
		Class<T> clazz = (Class<T>) pt.getActualTypeArguments()[0];
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
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
				Object sessionAttr = getSessionAttr(configuration.getUserId());
				po.setCreatorId(sessionAttr == null ? 0L : (Long) sessionAttr);
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

	/**
	 * 获取QueryDSL对应类
	 */
	protected abstract EntityPathBase<T> getEntityPathBase();
}
