package com.github.yogurt.core.dao.impl;

import com.github.yogurt.core.dao.BaseDAO;
import com.github.yogurt.core.exception.DaoException;
import com.github.yogurt.core.po.BasePO;
import com.github.yogurt.core.utils.JpaUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.conf.RenderNameStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author jtwu
 */
public abstract class BaseDAOImpl<T extends BasePO, R extends UpdatableRecord<R>> implements BaseDAO<T> {
	private static final String ALIAS = "T";

	@Autowired
	protected DSLContext dsl;

	/**
	 * 获取JOOQ中，主键对应的TableField
	 *
	 * @return 主键对应的TableField
	 */
	public abstract TableField getId();

	/**
	 * 获取PO类型
	 *
	 * @return PO类型
	 */
	public abstract Class<T> getType();

	@PostConstruct
	private void init() {
		dsl.settings().withRenderNameStyle(RenderNameStyle.AS_IS);
	}

	@SuppressWarnings("unchecked")
	private Table<R> getTable() {
		return getId().getTable();
	}

	@Override
	@SuppressWarnings({"unchecked"})
	public void save(T po) throws DaoException {
		List<Object> valueList = new ArrayList<>();
		List<Object> fieldList = new ArrayList<>();
		Map<String, Object> valueMap = JpaUtils.getColumnNameValueMap(po);

		for (Field field : getTable().fields()) {
			if (!valueMap.containsKey(field.getName())) {
				continue;
			}
			if (null == valueMap.get(field.getName())) {
				continue;
			}
			fieldList.add(field);
			valueList.add(valueMap.get(field.getName()));
		}
		Object id = dsl.insertInto(getTable()).columns(fieldList.toArray(new Field[fieldList.size()]))
				.values(valueList).returning(getId()).fetchOne().get(getId());
		try {
			BeanUtils.setProperty(po, getId().getName(), id);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public void update(T po) {
		Map<String, Object> valueMap = JpaUtils.getColumnNameValueMap(po);
		UpdateQuery updateQuery = dsl.updateQuery(getTable());
		addValue(valueMap, updateQuery);
		updateQuery.execute();
	}

	@Override
	public void updateForSelective(T po) {
		Map<String, Object> valueMap = JpaUtils.getColumnNameValueMap(po);
		UpdateQuery updateQuery = dsl.updateQuery(getTable());
		addValue(valueMap, updateQuery);
		updateQuery.execute();
	}

	@SuppressWarnings("unchecked")
	private void addValue(Map<String, Object> valueMap, UpdateQuery updateQuery) {
		for (Field field : getTable().fields()) {
			if (!valueMap.containsKey(field.getName())) {
				continue;
			}
			if (null == valueMap.get(field.getName())) {
				continue;
			}
			if (getId().getName().equals(field.getName())) {
				updateQuery.addConditions(getId().eq(valueMap.get(field.getName())));
				continue;
			}
			updateQuery.addValue(field, valueMap.get(field.getName()));
		}
	}

	private R getRecord(BasePO po) {
		return dsl.newRecord(getTable(), po);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findById(Serializable id) {
		return dsl.selectFrom(getTable().as(ALIAS)).where(getId().equal(id)).fetchOneInto(getType());
	}

	@Override
	public List<T> findAll() {
		return dsl.selectFrom(getTable().as(ALIAS)).fetchInto(getType());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<T> list(T po, Pageable pageable) {
		return new BasePageHandle<T>(dsl, pageable, getType()) {
			@Override
			public TableField[] fields() {
				List<TableField> list = new ArrayList<>();
				for (Field field : getTable().fields()) {
					list.add((TableField) field);
				}
				return list.toArray(new TableField[0]);
			}

			@Override
			public SelectConditionStep<? extends Record> beginWithFormSql(SelectSelectStep selectColumns) {
				Map<String, Object> map = JpaUtils.getColumnNameValueMap(po);
				String sql = " ";
				List values = new ArrayList();
				for (String columnName : map.keySet()) {
					Object property = map.get(columnName);
					if (null == property) {
						continue;
					}
					sql += StringUtils.join(columnName, "=? and ");
					values.add(map.get(columnName));
				}
				if (sql.length() > 1) {
					sql = StringUtils.removeEnd(sql, "and ");
				}
				if (sql.length() == 1) {
					return selectColumns.from(getTable()).where();
				}
				return selectColumns.from(getTable().as(ALIAS)).where(sql, values.toArray());
			}
		}.fetch();
	}

	@Override
	public void batchSave(List<T> poList) {
		dsl.batchInsert((TableRecord<?>[]) poList.stream().map(this::getRecord).toArray());
	}

	@Override
	public void batchUpdate(List<T> poList) {
		dsl.batchUpdate((UpdatableRecord<?>[]) poList.stream().map(this::getRecord).toArray());
	}


}