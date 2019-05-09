package com.github.yogurt.core.dao.impl;

import com.github.yogurt.core.dao.BaseDAO;
import com.github.yogurt.core.po.BasePO;
import com.github.yogurt.core.utils.JpaUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author jtwu
 */
public abstract class BaseDAOImpl<T extends BasePO, R extends UpdatableRecord<R>> extends JOOQHandle implements BaseDAO<T> {
	/**
	 * 获取PO类型
	 *
	 * @return PO类型
	 */
	@SuppressWarnings("unchecked")
	private Class<T> getPoClass() {
		ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
		return (Class<T>) pt.getActualTypeArguments()[0];
	}

	/**
	 * 获取JOOQ对应的Table
	 *
	 * @return org.jooq.Table
	 */
	@SuppressWarnings("unchecked")
	public abstract Table<R> getTable();

	private Table<R> getAliasTable() {
		return getTable().as(ALIAS);
	}

	@Override
	@SuppressWarnings({"unchecked"})
	public void save(T po) {
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
		R r = dsl.insertInto(getTable()).columns(fieldList.toArray(new Field[0]))
				.values(valueList).returning(getTable().getPrimaryKey().getFields()).fetchOne();
		for (TableField field : getTable().getPrimaryKey().getFields()) {
			JpaUtils.setValue(po, field.getName(), r.get(field));
		}
		System.out.println();
	}

	@Override
	public void update(T po) {
		Map<String, Object> valueMap = JpaUtils.getColumnNameValueMap(po);
		UpdateQuery updateQuery = dsl.updateQuery(getAliasTable());
		addValue(valueMap, updateQuery);
		updateQuery.execute();
	}

	@Override
	public void updateForSelective(T po) {
		Map<String, Object> valueMap = JpaUtils.getColumnNameValueMap(po);
		UpdateQuery updateQuery = dsl.updateQuery(getAliasTable());
		addValue(valueMap, updateQuery);
		updateQuery.execute();
	}

	@Override
	public void updateForSelective(T po, Condition condition) {
		Map<String, Object> valueMap = JpaUtils.getColumnNameValueMap(po);
		UpdateQuery updateQuery = dsl.updateQuery(getTable());

		for (Field field : getTable().fields()) {
			if (!valueMap.containsKey(field.getName())) {
				continue;
			}
			if (null == valueMap.get(field.getName())) {
				continue;
			}
			updateQuery.addValue(field, valueMap.get(field.getName()));
		}
		updateQuery.addConditions(condition);
		updateQuery.execute();
	}

	@SuppressWarnings("unchecked")
	private void addValue(Map<String, Object> valueMap, UpdateQuery updateQuery) {
//		getAliasTable().getPrimaryKey().getFields()本期望获取别名.列名，实际表名.列名
		List<String> primaryKeys = getAliasTable().getPrimaryKey().getFields().stream().map(Field::getName).collect(Collectors.toList());
		for (Field field : getAliasTable().fields()) {
			if (!valueMap.containsKey(field.getName())) {
				continue;
			}
			if (null == valueMap.get(field.getName())) {
				continue;
			}
			if (primaryKeys.contains(field.getName())) {
				updateQuery.addConditions(field.eq(valueMap.get(field.getName())));
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
//		单主键
		if (id instanceof Number) {
			TableField tableField = getAliasTable().getPrimaryKey().getFields().get(0);
			return dsl.selectFrom(getAliasTable()).where(getAliasTable().field(tableField).eq(id)).fetchOneInto(getPoClass());
		}
//		联合主键

		List<Condition> list = new ArrayList<>();
		for (TableField field : getAliasTable().getPrimaryKey().getFields()) {
			list.add(getAliasTable().field(field).eq(JpaUtils.getValue(id, field.getName())));
		}
		return dsl.selectFrom(getAliasTable()).where(list.toArray(new Condition[0])).fetchOneInto(getPoClass());
	}

	@Override
	public List<T> findAll() {
		return dsl.selectFrom(getAliasTable()).fetchInto(getPoClass());
	}

	@Override
	public List<T> find(T po) {
		Map<String, Object> map = JpaUtils.getColumnNameValueMap(po);
		List<String> columns = new ArrayList<>();
		List<Object> values = new ArrayList<>();
		createPoCondition(map, columns, values);
		if (columns.isEmpty()) {
			return findAll();
		}
		return dsl.selectFrom(getAliasTable()).where(StringUtils.join(columns, " and "), values.toArray()).fetchInto(getPoClass());
	}

	@Override
	public List<T> find(Condition condition, SortField... orderFields) {
		SelectQuery<R> rs = dsl.selectQuery(getAliasTable());
		rs.addConditions(condition);
		rs.addOrderBy(orderFields);
		return rs.fetchInto(getPoClass());
	}

	private void createPoCondition(Map<String, Object> map, List<String> columns, List<Object> values) {
		for (String columnName : map.keySet()) {
			Object property = map.get(columnName);
			if (null == property) {
				continue;
			}
			columns.add(StringUtils.join(columnName, "=? "));
			values.add(map.get(columnName));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<T> list(T po, Pageable pageable) {
		return new BasePageHandle<T>(dsl, pageable, getPoClass()) {
			@Override
			public TableField[] fields() {
				List<TableField> list = new ArrayList<>();
				for (Field field : getAliasTable().fields()) {
					TableField tableField = (TableField) field;

					list.add(tableField);
				}
				return list.toArray(new TableField[0]);
			}

			@Override
			public SelectConditionStep<? extends Record> beginWithFormSql(SelectSelectStep selectColumns) {
				Map<String, Object> map = JpaUtils.getColumnNameValueMap(po);
				List<String> columns = new ArrayList<>();
				List<Object> values = new ArrayList<>();
				createPoCondition(map, columns, values);
				if (columns.isEmpty()) {
					return selectColumns.from(getAliasTable()).where();
				}
				return selectColumns.from(getAliasTable()).where(StringUtils.join(columns, " and "), values.toArray());
			}
		}.fetch();
	}

	@Override
	public void batchSave(List<T> poList) {
//		dsl.batchInsert((TableRecord<?>[]) poList.stream().map(this::getRecord).toArray()).execute();
//		todo 需要调整
		List list = poList.stream().map(this::getRecord).collect(Collectors.toList());
		TableRecord<?>[] array = new TableRecord[list.size()];
		list.toArray(array);
		dsl.batchInsert(array).execute();
	}

	@Override
	public void batchUpdate(List<T> poList) {
		dsl.batchUpdate((UpdatableRecord<?>[]) poList.stream().map(this::getRecord).toArray()).execute();

	}


}