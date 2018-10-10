package com.github.jyoghurt.core.dao;

import com.github.jyoghurt.core.exception.DaoException;
import com.github.jyoghurt.core.handle.OperatorHandle;
import com.github.jyoghurt.core.utils.JPAUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.*;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class SelectProviderImpl extends BaseMapperProvider {

    /**
     * 查询所有数据sql
     *
     * @param param 参数
     * @return sql
     * @{@inheritDoc}
     */
    public String findAll(Map<String, Object> param) {
        beginWithClass(param);
        //处理查询列问题 add by limiao 20170508
        Map<String, Object> map = ((Map<String, Object>) param.get(BaseMapper.DATA));
        String selectColumnSql = createSelectColumnSql(map);
        SELECT(selectColumnSql);
        FROM(getTableNameWithAlias(entityClass));
        createAllWhere(map, false);
        return StringUtils.join(sql(), limit);
    }


    public String pageData(Map<String, Object> param) {
        beginWithClass(param);
        //处理查询列问题 add by limiao 20170508
        Map<String, Object> map = ((Map<String, Object>) param.get(BaseMapper.DATA));
        String selectColumnSql = createSelectColumnSql(map);
        if (MapUtils.isNotEmpty(map) && map.containsKey("distinct")) {
            SELECT_DISTINCT(selectColumnSql);
        } else {
            SELECT(selectColumnSql);
        }
        FROM(getTableNameWithAlias(entityClass));
        createAllWhere(map, true);
        return StringUtils.join(sql(), limit);
    }

    public String pageTotalRecord(Map<String, Object> param) {
        beginWithClass(param);
        SELECT(createSelectCountColumnSql(param));
        FROM(getTableNameWithAlias(entityClass));
        createAllWhere((Map<String, Object>) param.get(BaseMapper.DATA), false, true);
        return sql();
    }


    /**
     * 查询所有数据sql
     *
     * @param param 参数
     * @return sql
     */
    public String selectById(Map<String, Object> param) {
        beginWithClass(param);
        SELECT("*");
        FROM(getTableName(entityClass));
        WHERE(getEqualsValue(JPAUtils.getIdField(entityClass).getName(), BaseMapper.ID));
        return sql();
    }

    //处理查询列问题 add by limiao 20170508
    private String createSelectColumnSql(Map<String, Object> map) {
        String selectColumnSql = " t.* ";
        if (MapUtils.isNotEmpty(map) && map.containsKey("selectColumnSql") && map.get("selectColumnSql") != null) {
            selectColumnSql = " " + map.get("selectColumnSql").toString() + " ";
        }
        return selectColumnSql;
    }

    private String createSelectCountColumnSql(Map<String, Object> param) {
        Map<String, Object> map = ((Map<String, Object>) param.get(BaseMapper.DATA));
        String selectColumnSql = "count(distinct t." + JPAUtils.getIdField((Class<?>) param.get(BaseMapper.ENTITY_CLASS)).getName() + ")";
        if (map.containsKey("selectColumnSql") && map.get("selectColumnSql") != null) {
            selectColumnSql = " count(distinct " + map.get("selectColumnSql").toString() + ") ";
        }
        return selectColumnSql;
    }

    protected void createAllWhere(Map<String, Object> param, boolean usePage) {
        createAllWhere(param, usePage, false);
    }

    private void createAllWhere(Map<String, Object> param, boolean usePage, boolean isCount) {
        if (MapUtils.isEmpty(param)) {
            return;
        }
        try {
            Map<String, OperatorHandle> operatorMap = param.containsKey("operatorHandles") ?
                    (Map) param.get("operatorHandles") : new HashMap();
            createFieldsWhereSql(operatorMap, param);
            parseQueryHandle(param, usePage, isCount);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }


    private void createFieldsWhereSql(Map<String, OperatorHandle> operatorMap, Map<String, Object> param) {
        createFieldsWhereSql(operatorMap, param, entityClass);
    }

    private void createFieldsWhereSql(Map<String, OperatorHandle> operatorMap, Map<String, Object> param,
                                      Class clazz) {
//        处理非本类字段
        for (String key : operatorMap.keySet()) {
            if (key.contains(".")) {
                createColumnWhereSql(operatorMap, StringUtils.EMPTY, key);
            }
        }
//        处理本类字段
        for (Field field : JPAUtils.getAllFields(clazz)) {
            createFieldWhereSql(operatorMap, field, param);
        }
    }

    private boolean createFieldWhereSql(Map<String, OperatorHandle> operatorMap, Field field, Map<String, Object> param) {
        //验证是否需要拼装该属性
        if (!validateField2WhereSql(operatorMap, field, param)) {
            return false;
        }

        //封装类型递归处理
        if (null != field.getType().getAnnotation(Table.class)) {
            return false;
        }
        if (!operatorMap.containsKey(field.getName())) {
            WHERE(StringUtils.join("t.", getEqualsValue(field.getName(),
                    StringUtils.join(BaseMapper.DATA + ".", field.getName()))));
            return true;
        }
        createColumnWhereSql(operatorMap, "t.", field.getName());
        return true;
    }

    private void createColumnWhereSql(Map<String, OperatorHandle> operatorMap, String tableAlias, String fieldName) {
        /*
         * 将参数变量的"."改成"_"，eg table.id改成table_id,
         * 不改mybatis会将解释成table对象的id属性，而非table.id变量
         */

        for (String key : operatorMap.keySet()) {
            if (key.contains(".")) {
                operatorMap.put(key.replace(".", "_"), operatorMap.get(key));
            }
        }

//        String value = getColumnValues(operatorMap, fieldName);
        String value = ArrayUtils.isEmpty((operatorMap.get(fieldName)).getValues())
                ? "#{" + StringUtils.join(BaseMapper.DATA + "." + fieldName) + "}"
                : "#{" + BaseMapper.DATA + ".operatorHandles." + fieldName.replace(".", "_") + ".values[0]}";

        switch ((operatorMap.get(fieldName)).getOperator()) {
            case EQUAL: {
                WHERE(StringUtils.join(tableAlias, fieldName, " = ", value));
                break;
            }
            case LIKE: {
                WHERE(StringUtils.join(tableAlias, fieldName, " like CONCAT('%',", value, ", '%')"));
                break;
            }
            case LESS_THEN: {
                WHERE(StringUtils.join(tableAlias, fieldName, " < ", value));
                break;
            }
            case MORE_THEN: {
                WHERE(StringUtils.join(tableAlias, fieldName, " > ", value));
                break;
            }
            case LESS_EQUAL: {
                WHERE(StringUtils.join(tableAlias, fieldName, " <= ", value));
                break;
            }
            case MORE_EQUAL: {
                WHERE(StringUtils.join(tableAlias, fieldName, " >= ", value));
                break;
            }
            case NOT_IN: {
                WHERE(StringUtils.join(tableAlias, fieldName, " not in ", getInValuesSql(operatorMap, fieldName)));
                break;
            }
            case IN: {
                WHERE(StringUtils.join(tableAlias, fieldName, " in ", getInValuesSql(operatorMap, fieldName)));
                break;
            }
            case FIND_IN_SET: {
                WHERE("find_in_set(" + value + ",t." + fieldName + ")");
                break;
            }
            /* add by limiao 20160203 ,新增NOT_EQUAL, NOT_LIKE,IS_NULL ,IS_NOT_NULL   */
            case NOT_EQUAL: {
                WHERE(StringUtils.join(tableAlias, fieldName, " != ", value));
                break;
            }
            case NOT_LIKE: {
                WHERE(StringUtils.join(tableAlias, fieldName, " not like CONCAT('%'," + value + ", '%')"));
                break;
            }
            case IS_NULL: {
                WHERE(StringUtils.join(tableAlias, fieldName, " is null "));
                break;
            }
            case IS_NOT_NULL: {
                WHERE(StringUtils.join(tableAlias, fieldName, " is not null "));
                break;
            }
        }
    }

    /**
     * 验证该字段是否有需要拼装到sql中
     *
     * @param operatorMap 扩展运算集合
     * @param field       字段
     * @param param       数据集合
     * @return boolean值, 是否需要验证
     * @
     */
    private boolean validateField2WhereSql(Map<String, OperatorHandle> operatorMap, Field field, Map<String, Object>
            param) {
        if (null != field.getAnnotation(Transient.class) || field.getType().isAssignableFrom(Class.class)) {
            return false;
        }
        return param.containsKey(field.getName()) || operatorMap.containsKey(field.getName());
    }

    private String getInValuesSql(Map<String, OperatorHandle> operatorMap, String fieldName) {
        fieldName = StringUtils.replace(fieldName,".","_");
        List inValues = new ArrayList<>();
        for (int i = 0; i < operatorMap.get(fieldName).getValues().length; i++) {
            inValues.add(" #{" + BaseMapper.DATA + ".operatorHandles." + fieldName + ".values[" + i + "]}");
        }
        //add by limiao 20160811 处理拼in的时候，数组为空，不想查询的问题
        return inValues.isEmpty() ? " ( null ) " : StringUtils.join(" (", StringUtils.join(inValues, ","), ")");
    }


    /*
     * 处理查询扩展类
     */
    private void parseQueryHandle(Map<String, Object> param, Boolean usePage, Boolean isCount) {
        if (param.containsKey("orderBy") && !isCount) {
            LinkedHashMap<String, String> orderByMap = (LinkedHashMap<String, String>) param.get("orderBy");
            for (String orderBy : orderByMap.keySet()) {
                if (orderBy.contains(".")) {
                    ORDER_BY(orderBy + " " + orderByMap.get(orderBy));
                    continue;
                }
                ORDER_BY(StringUtils.join("t.", orderBy, " ", orderByMap.get(orderBy)));
            }
        }
        if (param.containsKey("whereSqls")) {
            List<String> whereSqls = (List<String>) param.get("whereSqls");
            for (String whereSql : whereSqls) {
                WHERE(whereSql);
            }
        }
        createLimit(param, usePage);
        if (param.containsKey("groupBy")) {
            GROUP_BY((String) param.get("groupBy"));
        }
    }

    /**
     * 创建limit
     *
     * @param param   参数
     * @param usePage 是否使用分页
     */
    private void createLimit(Map<String, Object> param, Boolean usePage) {
        if (param.containsKey("page") && usePage) {
            limit = StringUtils.join(" limit ", ((Integer) param.get("page") - 1) * (Integer) param.get("rows"), " , ",
                    param.get("rows").toString());
        }
    }


}
