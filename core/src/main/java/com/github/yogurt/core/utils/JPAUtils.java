package com.github.yogurt.core.utils;

import com.github.yogurt.core.exception.BaseErrorException;
import com.github.yogurt.core.exception.UtilException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by jtwu on 2015/4/23.
 * jpa公共处理类
 */
public class JPAUtils {
    private static Logger logger = LoggerFactory.getLogger(JPAUtils.class);

    /**
     * 获取所有属性，包括父类属性
     *
     * @param entityClass 业务实体类
     * @return 属性列表
     */
    public static List<Field> getAllFields(Class<?> entityClass) {
        Field[] fields = entityClass.getDeclaredFields();//获得属性
        return getAllFields(entityClass.getSuperclass(), new ArrayList<>(Arrays.asList(fields)));
    }

    private static List<Field> getAllFields(Class<?> entityClass, List<Field> fields) {
        if (Object.class.equals(entityClass)) {
            //排除Transient
            Iterator<Field> it = fields.iterator();
            while (it.hasNext()) {
                Field next = it.next();
                if ("serialVersionUID".equals(next.getName())) {
                    it.remove();
                    continue;
                }
                if (null != next.getAnnotation(Transient.class)) {
                    it.remove();
                }
            }

            return fields;
        }
        Collections.addAll(fields, entityClass.getDeclaredFields());
        return getAllFields(entityClass.getSuperclass(), fields);
    }


    /**
     * 获取主键属性
     *
     * @param entityClass 业务实体类
     * @return 主键属性
     * @ {@inheritDoc}
     */
    public static Field getIdField(Class<?> entityClass) {
        for (Class<?> c = entityClass; c != Object.class; c = c.getSuperclass()) {
            Field[] fields = c.getDeclaredFields();//获得属性
            for (Field field : fields) {
                if (null != field.getAnnotation(Id.class)) {
                    return field;
                }
            }
        }
        throw new UtilException("Id field is not found in[" + entityClass + "]" + entityClass.getName());
    }

    /**
     * 获取主键值
     *
     * @param po 获取po的主键值
     * @return 主键值
     * @
     */
    public static Object gtIdValue(Object po) {
        if (null == po) {
            return null;
        }
        return getValue(po, getIdField(po.getClass()));
    }

    /**
     * 反射调用get方法
     *
     * @param po        对象
     * @param fieldName 属性名
     * @return 返回属性值
     * @ {@inheritDoc}
     */
    public static Object getValue(Object po, String fieldName) {
        if (null == po || StringUtils.isEmpty(fieldName)) {
            throw new UtilException("parameter is null");
        }
        try {
            Field field = getField(po.getClass(), fieldName);
            field.setAccessible(true);  //设置私有属性范围
            return field.get(po);
        } catch (Exception e) {
            throw new UtilException(e);
        }
    }


    /**
     * 获取类及父类的属性
     *
     * @param clazz     类
     * @param fieldName 字段
     * @return 字段
     */
    public static Field getField(Class clazz, String fieldName) {
        if (clazz == Object.class) {
            throw new BaseErrorException("class does not have this field :" + fieldName);
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return getField(clazz.getSuperclass(), fieldName);

    }


    /**
     * 已知对象的field时获取对象的值 add by limiao 20160215
     *
     * @param source 具体对象
     * @param field  字段
     * @return Object 值
     */
    public static Object getValue(Object source, Field field) {
        try {
            field.setAccessible(true);
            return field.get(source);
        } catch (Exception e) {
            logger.error("JPAUtils getValue 异常！", e);
            return null;
        }
    }

    /**
     * 设置属性值
     *
     * @param source    对象
     * @param fieldName 属性名
     * @param value     属性值
     * @ {@inheritDoc}
     */
    public static void setValue(Object source, String fieldName, Object value) {
        if (null == source || StringUtils.isEmpty(fieldName)) {
            throw new UtilException("parameter is null");
        }
        try {
            Field field = getField(source.getClass(), fieldName);
            field.setAccessible(true);  //设置私有属性范围
            field.set(source, value);
        } catch (Exception e) {
            throw new UtilException(e);
        }

    }


    /**
     * 判断一个field是否是string类型
     * add by limiao 20160216
     *
     * @param field field对象
     * @return boolean
     */
    public static boolean fieldIsStringType(Field field) {
        return field.getType().equals(String.class);
    }

    /**
     * 判断一个field是否是date类型
     * add by limiao 20160216
     *
     * @param field field对象
     * @return boolean
     */
    public static boolean fieldIsDateType(Field field) {
        return field.getType().equals(Date.class);
    }

    /**
     * 返回一个map,key是id，value是propertyName的值
     *
     * @param propertyName     属性名
     * @param entityCollection 对象集合
     * @param <T>              泛型
     * @return idPropertyMap
     */
    public static <T> Map<String, Object> getIdPropertyMap(String propertyName, Collection<T> entityCollection) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (CollectionUtils.isNotEmpty(entityCollection)) {
            for (T entity : entityCollection) {
                try {
                    Field idField = getIdField(entity.getClass());
                    idField.setAccessible(true);
                    Object propertyObj = getValue(entity, entity.getClass().getDeclaredField(propertyName));
                    result.put((String) idField.get(entity), propertyObj);
                } catch (Exception e) {
                    logger.error("JPAUtils getValue 异常！", e);
                    return null;
                }
            }
        }
        return result;
    }

    /**
     * 返回一个map,key是id,value是对象本身
     *
     * @param entityCollection 对象集合
     * @param <T>              泛型
     * @return id对象Map
     */
    public static <T> Map<String, T> getIdEntityMap(Collection<T> entityCollection) {
        Map<String, T> result = new LinkedHashMap<>();
        if (CollectionUtils.isNotEmpty(entityCollection)) {
            for (T entity : entityCollection) {
                try {
                    Field idField = getIdField(entity.getClass());
                    result.put((String) getValue(entity, idField), entity);
                } catch (UtilException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 接收一个po，获取一个以的数据库列名为key，以属性值为value的map
     *
     * @param po po
     */
    public static Map<String, Object> getColumnNameValueMap(Object po) {
        Assert.notNull(po, "po is required");
        Map<String, Object> map = new HashMap<>();
        List<Field> list = getAllFields(po.getClass());
        for (Field field : list) {
            Column annotation = field.getAnnotation(Column.class);
            if (annotation == null) {
                continue;
            }
            field.setAccessible(true);
            String name = StringUtils.isEmpty(annotation.name())?field.getName():annotation.name();
            try {
                map.put(name, field.get(po));
            } catch (IllegalAccessException e) {
                logger.error("获取属性值失败", e);
            }
        }
        return map;
    }
}
