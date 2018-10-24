package com.github.jyoghurt.core.utils;

import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.exception.UtilException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class BeanUtils {
    /**
     * 反射调用get方法
     *
     * @param bean 对象
     * @param name 属性名
     * @return 返回属性值
     * @ {@inheritDoc}
     */
    public static Object getProperty(final Object bean, final String name) {

        if (null == bean || StringUtils.isEmpty(name)) {
            throw new UtilException("parameter is null");
        }
        try {
            Field field = getField(bean.getClass(), name);
            field.setAccessible(true);  //设置私有属性范围
            return field.get(bean);
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


}
