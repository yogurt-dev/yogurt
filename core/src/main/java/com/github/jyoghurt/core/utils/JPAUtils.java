package com.github.jyoghurt.core.utils;

import com.github.jyoghurt.core.exception.UtilException;
import com.github.jyoghurt.core.utils.beanUtils.AnnotationBinder;
import com.github.jyoghurt.core.utils.beanUtils.AnnotationReader;
import com.github.jyoghurt.core.utils.beanUtils.EntityBinder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by jtwu on 2015/4/23.
 * jpa公共处理类
 */
public class JPAUtils {
    protected static Logger logger = LoggerFactory.getLogger(JPAUtils.class);

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
     * @throws UtilException {@inheritDoc}
     */
    public static Field getIdField(Class<?> entityClass) throws UtilException {
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
     * @throws UtilException
     */
    public static Object gtIdValue(Object po) throws UtilException {
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
     * @throws UtilException {@inheritDoc}
     */
    public static Object getValue(Object po, String fieldName) throws UtilException {
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
     * @throws NoSuchFieldException
     */
    public static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        if (clazz == Object.class) {
            throw new NoSuchFieldException();
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return getField(clazz.getSuperclass(), fieldName);

    }

    /**
     * 设置属性值
     *
     * @param source    对象
     * @param fieldName 属性名
     * @param value     属性值
     * @throws UtilException {@inheritDoc}
     */
    public static void setValue(Object source, String fieldName, Object value) throws UtilException {
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
     * 基于注解初始化entity一些默认值
     *
     * @param entity 业务实体
     * @throws UtilException {@inheritDoc}
     */
    public static void interpretField(Object entity) throws UtilException {
        interpretField(entity, new ArrayList<>());
    }

    /**
     * 基于注解初始化entity一些默认值
     *
     * @param entity 业务实体
     * @throws UtilException
     */

    private static void interpretField(Object entity, List<Object> list) throws UtilException {
        try {
            if (entity == null) {
                return;
            }
            if (list.contains(entity)) {
                return;
            }
            //集合类型循环处理
            if (entity instanceof Collection) {
                for (Iterator<?> it = ((Collection<?>) entity).iterator(); it.hasNext(); ) {
                    interpretField(it.next(), list);
                }
                return;
            }
            list.add(entity);

            EntityBinder entityBinder = AnnotationReader.readEntity(entity.getClass());

            //解析注解
            for (AnnotationBinder annotationBinder : entityBinder.getAnnotationBinders()) {
                for (Field field : annotationBinder.getFields()) {
                    annotationBinder.getResolver().resolve(entity, field);
                }
            }
        } catch (Exception e) {
            throw new UtilException(e);
        }
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


}
