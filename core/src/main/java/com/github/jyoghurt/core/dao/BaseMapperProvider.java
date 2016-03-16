package com.github.jyoghurt.core.dao;


import com.github.jyoghurt.core.domain.BaseEntity;
import com.github.jyoghurt.core.enums.LogSystemType;
import com.github.jyoghurt.core.exception.DaoException;
import com.github.jyoghurt.core.exception.UtilException;
import com.github.jyoghurt.core.handle.CustomWhereHandle;
import com.github.jyoghurt.core.handle.OperatorHandle;
import com.github.jyoghurt.core.handle.SQLJoinHandle;
import com.github.jyoghurt.core.utils.JPAUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import static com.github.jyoghurt.core.mybatis.SqlBuilder.*;

/**
 * Created by jtwu on 2015/4/21.
 */
@SuppressWarnings("unchecked")
public class BaseMapperProvider {
    private Class<?> entityClass;
    protected String limit;

    /**
     * 查询所有数据sql
     *
     * @param param 参数
     * @return sql
     * @throws DaoException {@inheritDoc}
     */
    public String findAll(Map<String, Object> param) throws DaoException {
        beginWithClass(param);
        if (((Map<String, Object>) param.get(BaseMapper.DATA)).containsKey("distinct")) {
            SELECT_DISTINCT("t.*");
        } else {
            SELECT("t.*");
        }
        FROM(getTableNameWithAlias(entityClass));
        createAllWhere((Map<String, Object>) param.get(BaseMapper.DATA), false);
        return StringUtils.join(SQL(), limit);
    }

    protected void createAllWhere(Map<String, Object> param, boolean usePage) throws DaoException {
        createAllWhere(param, usePage, false);
    }

    protected void createAllWhere(Map<String, Object> param, boolean usePage, boolean isCount) throws DaoException {
        if (MapUtils.isEmpty(param)) {
            return;
        }
        try {
            Map<String, OperatorHandle> operatorMap = param.containsKey("operatorHandles") ?
                    (Map) param.get("operatorHandles") : new HashMap();
            if (CollectionUtils.isNotEmpty((LinkedList) param.get("customList"))) {
                boolean operatorTag = false;
                for (CustomWhereHandle customWhereHandle : (LinkedList<CustomWhereHandle>) param.get("customList")) {
                    switch (customWhereHandle.getType()) {
                        case FIELD: {
                            operatorTag = createFieldWhereSql(operatorMap, entityClass.getDeclaredField(customWhereHandle.getSql()),
                                    param);
                            break;
                        }
                        case OR: {
                            if (operatorTag) {
                                OR();
                                operatorTag = false;
                            }
                            break;
                        }
                        case AND: {
                            if (operatorTag) {
                                AND();
                                operatorTag = false;
                            }
                            break;
                        }
                        case SQL: {
                            WHERE(customWhereHandle.getSql());
                            operatorTag = true;
                        }
                    }
                }
                parseQueryHandle(param, usePage, isCount);
                return;
            }

            for (Field field : JPAUtils.getAllFields(entityClass)) {
                createFieldWhereSql(operatorMap, field, param);
            }
            parseQueryHandle(param, usePage, isCount);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    private boolean createFieldWhereSql(Map<String, OperatorHandle> operatorMap, Field field, Map<String, Object> param) {
        if (null != field.getAnnotation(Transient.class) || field.getType().isAssignableFrom(Class.class) ||
                (!param.containsKey(field.getName()) && (!operatorMap.containsKey(field.getName()) || ArrayUtils.isEmpty(
                        operatorMap.get(field.getName()).getValues())))) {
            return false;
        }
        if (!operatorMap.containsKey(field.getName())) {
            WHERE(StringUtils.join("t.", getEqualsValue(field.getName(), StringUtils.join(BaseMapper.DATA, ".", field.getName()))));
            return true;
        }
        //用户自定义值优先
        Object value = ArrayUtils.isEmpty((operatorMap.get(field.getName())).getValues())
                ? "#{" + StringUtils.join(BaseMapper.DATA + "." + field.getName()) + "}"
                : "#{" + BaseMapper.DATA + ".operatorHandles." + field.getName() + ".values[0]}";
        switch ((operatorMap.get(field.getName())).getOperator()) {
            case EQUAL: {
                WHERE(StringUtils.join("t.", getEqualsValue(field.getName(), StringUtils.join(BaseMapper.DATA, ".", field.getName()))));
                break;
            }
            case LIKE: {
                WHERE(StringUtils.join("t.", field.getName(), " like CONCAT('%'," + value + ", '%')"));
                break;
            }
            case LESS_THEN: {
                WHERE(StringUtils.join("t.", field.getName(), " < " + value));
                break;
            }
            case MORE_THEN: {
                WHERE(StringUtils.join("t.", field.getName(), " > " + value));
                break;
            }
            case LESS_EQUAL: {
                WHERE(StringUtils.join("t.", field.getName(), " <= " + value));
                break;
            }
            case MORE_EQUAL: {
                WHERE(StringUtils.join("t.", field.getName(), " >= " + value));
                break;
            }
            case IN: {
                String inValue = "";
                for (int i = 0; i < operatorMap.get(field.getName()).getValues().length; i++) {
                    inValue += " #{" + BaseMapper.DATA + ".operatorHandles." + field.getName() + ".values[" + i + "]},";
                }
                WHERE(StringUtils.join("t.", field.getName(), " in (", inValue.substring(0, inValue.length() - 1), ")" +
                        ""));
                break;
            }
            case FIND_IN_SET: {
                WHERE("find_in_set(" + value + ",t." + field.getName() + ")");
                break;
            }
            /* add by limiao 20160203 ,新增NOT_EQUAL, NOT_LIKE,IS_NULL ,IS_NOT_NULL   */
            case NOT_EQUAL: {
                WHERE(StringUtils.join("t.", getNotEqualsValue(field.getName(), StringUtils.join(BaseMapper.DATA, ".", field.getName()))));
                break;
            }
            case NOT_LIKE: {
                WHERE(StringUtils.join("t.", field.getName(), " not like CONCAT('%'," + value + ", '%')"));
                break;
            }
            case IS_NULL: {
                WHERE(StringUtils.join("t.", field.getName(), " is null "));
                break;
            }
            case IS_NOT_NULL: {
                WHERE(StringUtils.join("t.", field.getName(), " is not null "));
                break;
            }
        }
        return true;
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
        if (param.containsKey("sqlJoinHandle")) {
            LinkedList<SQLJoinHandle> sqlJoinHandle = (LinkedList<SQLJoinHandle>) param.get("sqlJoinHandle");
            for (SQLJoinHandle sqlJoinAssistor : sqlJoinHandle) {
                switch (sqlJoinAssistor.getJoinType()) {
                    case JOIN: {
                        JOIN(sqlJoinAssistor.getJoinSql());
                        if (StringUtils.isNotEmpty(sqlJoinAssistor.getSelectColumns()) && !isCount) {
                            SELECT(sqlJoinAssistor.getSelectColumns());
                        }
                        break;
                    }
                    case INNER_JOIN: {
                        INNER_JOIN(sqlJoinAssistor.getJoinSql());
                        if (StringUtils.isNotEmpty(sqlJoinAssistor.getSelectColumns()) && !isCount) {
                            SELECT(sqlJoinAssistor.getSelectColumns());
                        }
                        break;
                    }
                    case LEFT_OUTER_JOIN: {
                        LEFT_OUTER_JOIN(sqlJoinAssistor.getJoinSql());
                        if (StringUtils.isNotEmpty(sqlJoinAssistor.getSelectColumns()) && !isCount) {
                            SELECT(sqlJoinAssistor.getSelectColumns());
                        }
                        break;
                    }
                    case RIGHT_OUTER_JOIN: {
                        RIGHT_OUTER_JOIN(sqlJoinAssistor.getJoinSql());
                        if (StringUtils.isNotEmpty(sqlJoinAssistor.getSelectColumns()) && !isCount) {
                            SELECT(sqlJoinAssistor.getSelectColumns());
                        }
                        break;
                    }
                }
            }
        }
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

    /**
     * 查询所有数据sql
     *
     * @param param 参数
     * @return sql
     * @throws DaoException {@inheritDoc}
     */
    public String selectById(Map<String, Object> param) throws DaoException {
        beginWithClass(param);
        SELECT("*");
        FROM(getTableName(entityClass));
        try {
            WHERE(getEqualsValue(JPAUtils.getIdField(entityClass).getName(), BaseMapper.ID));
        } catch (UtilException e) {
            throw new DaoException(e);
        }
        return SQL();
    }

    /**
     * 获取比较值字符串
     *
     * @param column 列名
     * @param value  值
     * @return 拼接后的字符串
     */
    private String getEqualsValue(String column, String value) {
        return StringUtils.join(column, " = #{", value, "}");
    }

    /**
     * 获取不等于比较值字符串
     * <p>
     * add by limiao 20160203
     *
     * @param column 列名
     * @param value  值
     * @return 拼接后的字符串
     */
    private String getNotEqualsValue(String column, String value) {
        return StringUtils.join(column, " != #{", value, "}");
    }

    /*
     * 生成sql开始
     */
    protected void beginWithClass(Map<String, Object> param) throws DaoException {
        if (null == param.get(BaseMapper.ENTITY_CLASS)) {
            throw new DaoException(StringUtils.join("获取实体类型失败 entityClass 为空"));
        }
        BEGIN();
        entityClass = (Class) param.get(BaseMapper.ENTITY_CLASS);
    }

    /**
     * 保存sql
     *
     * @param param 参数
     * @return sql
     * @throws DaoException {@inheritDoc}
     */
    public String save(Map<String, Object> param) throws Exception {
        entityClass = param.get(BaseMapper.ENTITY).getClass();
        BEGIN();
        INSERT_INTO(getTableName(entityClass));

        Field idField = null;
        for (Field field : JPAUtils.getAllFields(entityClass)) {

            if (!ClassUtils.isPrimitiveOrWrapper(field.getClass()) && !Enum.class.isAssignableFrom(LogSystemType.class)) {
                continue;
            }
            field.setAccessible(true);
            //处理主键
            if (null != field.getAnnotation(Id.class) || null != field.getAnnotation(Transient.class)) {
                idField = field;
                continue;
            }
            //add by limiao 20160309 insert 为null的不拼sql
            try {
                Object value = JPAUtils.getValue(param.get(BaseMapper.ENTITY), field.getName());
                if (null == value) {
                    continue;
                }
            } catch (UtilException e) {
                throw new DaoException(e);
            }

            VALUES(field.getName(), StringUtils.join("#{", BaseMapper.ENTITY, ".", field.getName(), "}"));
        }
        if (null == idField) {
            throw new DaoException(StringUtils.join(entityClass.getName(), "实体未配置@Id "));
        }

        setId(param, idField);
        return SQL();
    }

    /**
     * 保存sql
     *
     * @param param 参数
     * @return sql
     * @throws DaoException {@inheritDoc}
     */
    public String saveBatch(Map<String, Object> param) throws Exception {
        if (null == param.get(BaseMapper.ENTITIES)) {
            throw new DaoException("批量插入的数据为null");
        }
        entityClass = ((List) param.get(BaseMapper.ENTITIES)).get(0).getClass();
        BEGIN();
        BATCH_INSERT_INTO(getTableName(entityClass));

        Field idField = null;
        for (int i = 0; i < ((List) param.get(BaseMapper.ENTITIES)).size(); i++) {
            for (Field field : JPAUtils.getAllFields(entityClass)) {

                if (!ClassUtils.isPrimitiveOrWrapper(field.getClass()) && !Enum.class.isAssignableFrom(LogSystemType.class)) {
                    continue;
                }
                field.setAccessible(true);
                //处理主键
                if (null != field.getAnnotation(Id.class) || null != field.getAnnotation(Transient.class)) {
                    idField = field;
                    continue;
                }
                //add by limiao 20160309 insert 为null的不拼sql
                try {
                    Object value = JPAUtils.getValue(((List) param.get(BaseMapper.ENTITIES)).get(i), field.getName());
                    if (null == value) {
                        continue;
                    }
                } catch (UtilException e) {
                    throw new DaoException(e);
                }
                BATCH_VALUES(field.getName(), StringUtils.join("#{", BaseMapper.ENTITIES, "[", i, "]", ".", field.getName(), "}"));
            }
            if (null == idField) {
                throw new DaoException(StringUtils.join(entityClass.getName(), "实体未配置@Id "));
            }
            setIdBatch((BaseEntity) ((List) param.get(BaseMapper.ENTITIES)).get(i), i, idField);
            BATCH_SEGMENTATION();
        }
        return SQL();
    }

    public String saveForSelective(Map<String, Object> param) throws Exception {
        final Object entity = param.get(BaseMapper.ENTITY);
        entityClass = entity.getClass();
        BEGIN();
        INSERT_INTO(getTableName(entityClass));

        Field idField = null;
        for (Field field : JPAUtils.getAllFields(entityClass)) {
            field.setAccessible(true);
            //处理主键
            if (null != field.getAnnotation(Id.class) || null != field.getAnnotation(Transient.class)) {
                idField = field;
                continue;
            }
            if (field.get(entity) != null) {
                VALUES(field.getName(), StringUtils.join("#{", BaseMapper.ENTITY, ".", field.getName(), "}"));
            }
        }
        if (null == idField) {
            throw new DaoException(StringUtils.join(entityClass.getName(), "实体未配置@Id "));
        }
        setId(param, idField);
        return SQL();
    }


    private void setId(Map<String, Object> param, Field idField) throws IllegalAccessException {
        if (!idField.getType().isAssignableFrom(String.class)) {
            return;
        }
        if (StringUtils.isNotEmpty((String) idField.get(param.get(BaseMapper.ENTITY)))) {
            VALUES(idField.getName(), StringUtils.join("#{", BaseMapper.ENTITY, ".", idField.getName(), "}"));
            return;
        }
        String id = UUID.randomUUID().toString();
        VALUES(idField.getName(), StringUtils.join("'", id, "'"));
        idField.set(param.get(BaseMapper.ENTITY), id);
    }

    private void setIdBatch(BaseEntity baseEntity, int i, Field idField) throws IllegalAccessException {
        if (!idField.getType().isAssignableFrom(String.class)) {
            return;
        }
        if (StringUtils.isNotEmpty((String) idField.get(baseEntity))) {
            BATCH_VALUES(idField.getName(), StringUtils.join("#{", BaseMapper.ENTITIES, "[", i, "]", ".", idField.getName(), "}"));
            return;
        }
        String id = UUID.randomUUID().toString();
        BATCH_VALUES(idField.getName(), StringUtils.join("'", id, "'"));
        idField.set(baseEntity, id);
    }

    public String update(Map<String, Object> param) throws DaoException {
        entityClass = param.get(BaseMapper.ENTITY).getClass();
        BEGIN();
        UPDATE(getTableName(entityClass));
        Field idField = null;
        for (Field field : JPAUtils.getAllFields(entityClass)) {
            field.setAccessible(true);
            //处理主键
            if (null == field.getAnnotation(Id.class)) {
                SET(StringUtils.join(getEqualsValue(field.getName(), StringUtils.join(BaseMapper.ENTITY, ".", field.getName()))));
                continue;
            }
            idField = field;
            WHERE(getEqualsValue(field.getName(), StringUtils.join(BaseMapper.ENTITY, ".", field.getName())));
        }
        if (null == idField) {
            throw new DaoException(StringUtils.join(entityClass.getName(), "实体未配置@Id "));
        }


        return SQL();

    }

    /**
     * 获取表名
     *
     * @param entityClass 实体类
     * @return sql
     * @throws DaoException {@inheritDoc}
     */
    protected String getTableName(Class entityClass) throws DaoException {
        Annotation table = entityClass.getAnnotation(Table.class);
        if (null == table) {
            throw new DaoException(StringUtils.join("实体未配置Table注解 entityClass =", entityClass.getName()));
        }
        String tableName = ((Table) table).name();
        if (StringUtils.isEmpty(tableName)) {
            throw new DaoException(StringUtils.join("实体的Table注解未配置name属性 entityClass =", entityClass.getName()));
        }
        return tableName;
    }

    protected String getTableNameWithAlias(Class entityClass) throws DaoException {
        return StringUtils.join(getTableName(entityClass), " t");
    }

    public String pageData(Map<String, Object> param) throws DaoException {
        beginWithClass(param);
        if (((Map<String, Object>) param.get(BaseMapper.DATA)).containsKey("distinct")) {
            SELECT_DISTINCT("t.*");
        } else {
            SELECT("t.*");
        }
        FROM(getTableNameWithAlias(entityClass));
        createAllWhere((Map<String, Object>) param.get(BaseMapper.DATA), true);
        return StringUtils.join(SQL(), limit);
    }

    public String pageTotalRecord(Map<String, Object> param) throws DaoException {
        beginWithClass(param);
        try {
            SELECT("count(distinct t." + JPAUtils.getIdField((Class<?>) param.get(BaseMapper.ENTITY_CLASS)).getName() + ")");
        } catch (UtilException e) {
            throw new DaoException("Query paging failure !");
        }
        FROM(getTableNameWithAlias(entityClass));
        createAllWhere((Map<String, Object>) param.get(BaseMapper.DATA), false, true);
        return SQL();
    }

    public String findListBySql(Map<String, Object> param) throws DaoException {
        createLimit((Map<String, Object>) param.get(BaseMapper.DATA), true);
        return StringUtils.join(param.get(BaseMapper.CUSTOM_SQL), limit);
    }

    public String findListTotalRecordBySql(Map<String, Object> param) throws DaoException {
        return StringUtils.join("select count(*) from (", param.get(BaseMapper.CUSTOM_SQL), ") as countTable");
    }

    public String findUniqueObjectBySql(Map<String, Object> param) throws DaoException {
        return StringUtils.join(param.get(BaseMapper.CUSTOM_SQL), " limit 1");
    }

    public String delete(Map<String, Object> param) throws DaoException {
        beginWithClass(param);
        DELETE_FROM(getTableName(entityClass));
        try {
            WHERE(getEqualsValue(JPAUtils.getIdField(entityClass).getName(), BaseMapper.ID));
        } catch (UtilException e) {
            throw new DaoException(e);
        }
        return SQL();
    }

    public String logicDelete(Map<String, Object> param) throws DaoException {
        entityClass = (Class<?>) param.get(BaseMapper.ENTITY_CLASS);
        BEGIN();
        UPDATE(getTableName(entityClass));
        param.put(BaseEntity.DELETE_FLAG, true);
        SET(getEqualsValue(BaseEntity.DELETE_FLAG, BaseEntity.DELETE_FLAG));
        try {
            Field idField = JPAUtils.getIdField(entityClass);
            WHERE(getEqualsValue(idField.getName(), BaseMapper.ID));
        } catch (UtilException e) {
            throw new DaoException(e);
        }
        return SQL();
    }

    public String logicDeleteByCondition(Map<String, Object> param) throws DaoException {
        beginWithClass(param);
        createAllWhere((Map<String, Object>) param.get(BaseMapper.DATA), false);
        UPDATE(StringUtils.join(getTableName(entityClass), " t"));
        param.put(BaseEntity.DELETE_FLAG, true);
        SET(getEqualsValue(BaseEntity.DELETE_FLAG, BaseEntity.DELETE_FLAG));
        return SQL();
    }

    public String updateForSelective(Map<String, Object> param) throws DaoException {
        entityClass = param.get(BaseMapper.ENTITY).getClass();
        BEGIN();
        UPDATE(getTableName(entityClass));
        Field idField = null;
        for (Field field : JPAUtils.getAllFields(entityClass)) {
            field.setAccessible(true);
            //处理主键
            if (null != field.getAnnotation(Id.class)) {
                idField = field;
                continue;
            }
            try {
                Object value = JPAUtils.getValue(param.get(BaseMapper.ENTITY), field.getName());
                if (null == value) {
                    continue;
                }
                //空字符串对应修改时，情况输入框
//                if(StringUtils.EMPTY.equals(value)){
//                    continue;
//                }
            } catch (UtilException e) {
                throw new DaoException(e);
            }
            SET(StringUtils.join(getEqualsValue(field.getName(), StringUtils.join(BaseMapper.ENTITY, ".", field.getName()))));
        }
        if (null == idField) {
            throw new DaoException(StringUtils.join(entityClass.getName(), "实体未配置@Id "));
        }
        WHERE(getEqualsValue(idField.getName(), StringUtils.join(BaseMapper.ENTITY, ".", idField.getName())));
        return SQL();
    }

    public String deleteByCondition(Map<String, Object> param) throws DaoException {
        beginWithClass(param);
        createAllWhere((Map<String, Object>) param.get(BaseMapper.DATA), false);
        DELETE_FROM(StringUtils.join(getTableName(entityClass), " t"));
        return SQL().replaceFirst("DELETE FROM", "DELETE t FROM");
    }


    public String updateByCondition(Map<String, Object> param) throws DaoException {
        beginWithClass(param);
        UPDATE(getTableNameWithAlias(entityClass));
        SET("name = #{name}");
        createAllWhere((Map<String, Object>) param.get(BaseMapper.DATA), false);
        return StringUtils.join(SQL(), limit);
    }


}
