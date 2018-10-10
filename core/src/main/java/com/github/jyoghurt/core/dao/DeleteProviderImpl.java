package com.github.jyoghurt.core.dao;

import com.github.jyoghurt.core.constant.Constant;
import com.github.jyoghurt.core.utils.JPAUtils;

import java.lang.reflect.Field;
import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class DeleteProviderImpl extends BaseMapperProvider{

    /**
     * 逻辑删除标识
     */
    public static final String DELETE_FLAG = "deleteFlag";

    public String logicDelete(Map<String, Object> param) {
        beginWithClass(param);
        UPDATE(getTableName(entityClass));
        param.put(DELETE_FLAG, true);
        SET(getEqualsValue(DELETE_FLAG, DELETE_FLAG));
        Field idField = JPAUtils.getIdField(entityClass);
        WHERE(getEqualsValue(idField.getName(), BaseMapper.ID));

        return sql();
    }


    public String delete(Map<String, Object> param) {
        beginWithClass(param);
        DELETE_FROM(getTableName(entityClass));
        WHERE(getEqualsValue(JPAUtils.getIdField(entityClass).getName(), BaseMapper.ID));

        return sql();
    }




}
