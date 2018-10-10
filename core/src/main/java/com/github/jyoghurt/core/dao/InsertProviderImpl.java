package com.github.jyoghurt.core.dao;

import com.github.jyoghurt.core.enums.LogSystemType;
import com.github.jyoghurt.core.exception.DaoException;
import com.github.jyoghurt.core.exception.UtilException;
import com.github.jyoghurt.core.utils.JPAUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.INSERT_INTO;
import static org.apache.ibatis.jdbc.SqlBuilder.VALUES;

public class InsertProviderImpl extends BaseMapperProvider {

    /**
     * 保存sql
     *
     * @param param 参数
     * @return sql
     * @{@inheritDoc}
     */
    public String save(Map<String, Object> param) {
        BEGIN();
        entityClass = param.get(BaseMapper.ENTITY).getClass();
        String tableName = getTableName(entityClass);
        if (param.containsKey(BaseMapper.TABLE_NAME) && param.get(BaseMapper.TABLE_NAME) != null) {
            tableName = param.get(BaseMapper.TABLE_NAME).toString().trim();
        }
        INSERT_INTO(tableName);
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
            Object value = JPAUtils.getValue(param.get(BaseMapper.ENTITY), field.getName());
            if (null == value) {
                continue;
            }

            VALUES(field.getName(), StringUtils.join("#{", BaseMapper.ENTITY, ".", field.getName(), "}"));
        }
        if (null == idField) {
            throw new DaoException(StringUtils.join(entityClass.getName(), "实体未配置@Id "));
        }

        setId(param, idField);
        return sql();
    }




    public String saveForSelective(Map<String, Object> param) {
        final Object entity = param.get(BaseMapper.ENTITY);
        BEGIN();
        entityClass = param.get(BaseMapper.ENTITY).getClass();
        INSERT_INTO(getTableName(entityClass));
        Field idField = null;
        for (Field field : JPAUtils.getAllFields(entityClass)) {
            field.setAccessible(true);
            //处理主键
            if (null != field.getAnnotation(Id.class) || null != field.getAnnotation(Transient.class)) {
                idField = field;
                continue;
            }
            try {
                if (field.get(entity) != null) {
                    VALUES(field.getName(), StringUtils.join("#{", BaseMapper.ENTITY, ".", field.getName(), "}"));
                }
            } catch (IllegalAccessException e) {
                throw new UtilException(e);
            }
        }
        if (null == idField) {
            throw new DaoException(StringUtils.join(entityClass.getName(), "实体未配置@Id "));
        }
        setId(param, idField);
        return sql();
    }


    private void setId(Map<String, Object> param, Field idField) {
        if (!idField.getType().isAssignableFrom(String.class)) {
            return;
        }
        try {
            if (StringUtils.isNotEmpty((String) idField.get(param.get(BaseMapper.ENTITY)))) {
                VALUES(idField.getName(), StringUtils.join("#{", BaseMapper.ENTITY, ".", idField.getName(), "}"));
                return;
            }

            String id = UUID.randomUUID().toString().replace("-", "");
            VALUES(idField.getName(), StringUtils.join("'", id, "'"));
            idField.set(param.get(BaseMapper.ENTITY), id);
        } catch (IllegalAccessException e) {
            throw new UtilException(e);
        }
    }
}
