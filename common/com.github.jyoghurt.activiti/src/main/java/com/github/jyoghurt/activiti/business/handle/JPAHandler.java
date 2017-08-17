package com.github.jyoghurt.activiti.business.handle;

import com.github.jyoghurt.activiti.business.annotations.MainFormId;
import com.github.jyoghurt.activiti.business.flowEntity.FlowEntity;
import com.github.jyoghurt.core.exception.UtilException;

import javax.persistence.Table;
import java.lang.reflect.Field;

/**
 * user:dell
 * date: 2016/9/19.
 */
public class JPAHandler {

    public static String findTableName(String className) throws UtilException {
        try {
            return findTableName(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new UtilException("cant find class by name:" + className);
        }
    }

    /**
     * 根据业务实体类 返回映射表的名称
     *
     * @param entityClass 业务实体类
     * @return 映射表名
     */
    public static String findTableName(Class<?> entityClass) {
        return entityClass.getAnnotation(Table.class).name();
    }

    /**
     * 获取主表ID名称
     *
     * @param flowEntity 流程基类
     * @return 主表Id名称
     */
    public static String getMainFormIdName(FlowEntity flowEntity) {
        Class entity = flowEntity.getClass();
        Field[] fields = entity.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            //获取字段中包含MainFormId的注解
            MainFormId mainFormId = f.getAnnotation(MainFormId.class);
            if (mainFormId != null) {
                return (String) f.getName();
            }
        }
        return null;
    }
}
