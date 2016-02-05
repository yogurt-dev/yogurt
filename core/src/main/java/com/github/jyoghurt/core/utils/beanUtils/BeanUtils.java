
package com.github.jyoghurt.core.utils.beanUtils;


import com.github.jyoghurt.core.exception.ServiceException;
import com.github.jyoghurt.core.exception.UtilException;
import com.github.jyoghurt.core.utils.ChainMap;
import org.apache.commons.lang3.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 复制对象属性，并支持扩展类型转换
 *
 * @author jtwu
 */
public class BeanUtils {
    private static Map<String, Method> methodMap = new HashMap<String, Method>();

    static {
        Method methods[] = BeanUtils.class.getMethods();
        for (Method method : methods) {
            methodMap.put(method.getName(), method);
        }
    }

    /**
     * 类似BeanUtisl.copyProperties复制对象 不同之处可以对同名但不同类型的属性赋值 可以配置类型转换方法
     * eg：String赋值给Date 需要编写StringToDate方法
     *
     * @param target 目标对象
     * @param source 源对象
     * @throws UtilException {@inheritDoc}
     */
    public static void copyProperties(Object source, Object target) throws UtilException {
        copyProperties(source, target, true);
    }

    /**
     * 类似BeanUtisl.copyProperties复制对象 不同之处可以对同名但不同类型的属性赋值 可以配置类型转换方法
     * eg：String赋值给Date 需要编写StringToDate方法
     *
     * @param target       目标对象
     * @param source       源对象
     * @param nullBeCopied 字段为null的是否复制
     * @throws UtilException {@inheritDoc}
     */
    public static void copyProperties(Object source, Object target, Boolean nullBeCopied) throws UtilException {
        copyProperties(source, target, null, null, nullBeCopied);
    }

    /**
     * 指定目标对象中需要复制的属性 支持属性间类型转换 配置类型转换方法 eg：String赋值给Date 需要编写StringToDate方法
     *
     * @param target              目标对象
     * @param source              源对象
     * @param effectiveProperties 需要复制的属性列表
     * @throws UtilException {@inheritDoc}
     */
    public static void copyProperties(Object source, Object target, String[] effectiveProperties) throws UtilException {
        copyProperties(source, target, effectiveProperties, null);
    }

    /**
     * 指定目标对象中需要复制的属性 支持属性间类型转换 配置类型转换方法 eg：String赋值给Date 需要编写StringToDate方法
     *
     * @param target              目标对象
     * @param source              源对象
     * @param effectiveProperties 需要复制的属性列表
     * @param ignoreProperties    不需要复制的属性列表
     * @throws UtilException {@inheritDoc}
     */
    public static void copyProperties(Object source, Object target, String[] effectiveProperties,
                                      String[] ignoreProperties) throws UtilException {
        copyProperties(source, target, effectiveProperties, ignoreProperties, true);
    }

    /**
     * 指定目标对象需要复制的有效属性和无效属性 支持属性间类型转换 配置类型转换方法 eg：String赋值给Date
     * 需要编写StringToDate方法
     *
     * @param target              目标对象
     * @param source              源对象
     * @param effectiveProperties 需要复制的属性列表
     * @param ignoreProperties    不需要复制的属性列表
     * @param nullBeCopied        字段为null是否复制
     * @throws UtilException {@inheritDoc}
     */
    public static void copyProperties(Object source, Object target, String[] effectiveProperties,
                                      String[] ignoreProperties, Boolean nullBeCopied) throws UtilException {
        // Field targetFields[] = destCls.getDeclaredFields();
        // 得到自省对象

        BeanInfo beanInfo = null;
        try {

            beanInfo = Introspector.getBeanInfo(source.getClass());

            // 得到key为属性名，value为属性对应的PropertyDescriptor的HashMap
            PropertyDescriptor sourcePds[] = beanInfo.getPropertyDescriptors();
            Map<String, PropertyDescriptor> sourcePdMap = new HashMap<String, PropertyDescriptor>();
            for (PropertyDescriptor sourcePd : sourcePds) {
                sourcePdMap.put(sourcePd.getName(), sourcePd);
            }

            List<String> effectiveList = (null == effectiveProperties) ? null : Arrays.asList(effectiveProperties);
            List<String> ignoreList = (null == ignoreProperties) ? null : Arrays.asList(ignoreProperties);
            PropertyDescriptor targetPds[] = Introspector.getBeanInfo(target.getClass()).getPropertyDescriptors();
            // 遍历目标对象的所有属性
            for (PropertyDescriptor targetPd : targetPds) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(sourcePdMap, targetPd.getName());
                if (null != targetPd.getWriteMethod() && null != sourcePd && (null == effectiveList || effectiveList
                        .contains(targetPd.getName())) && (null == ignoreList || !ignoreList.contains(targetPd
                        .getName()))) {
                    Method sourceReadMethod = sourcePd.getReadMethod();
                    if (null == sourceReadMethod) {
                        continue;
                    }
                    Object sourceValue = sourceReadMethod.invoke(source, null);
                    // 当需要验证并且源对象字段为空时跳过
                    if (nullBeCopied == false && null == sourceValue) {
                        continue;
                    }
                    Method writeMethod = targetPd.getWriteMethod();
                    if (sourcePd.getPropertyType().isAssignableFrom(targetPd.getPropertyType())) {
                        // 集合类型的需递归完成其内部对象复制
                        if (Collection.class.isAssignableFrom(sourcePd.getPropertyType())) {
                            copyCollection(target, effectiveProperties, ignoreProperties, nullBeCopied, targetPd,
                                    sourceValue, writeMethod);
                            continue;
                        }
                        // 如果目标对象和源对象类型相同时直接赋值
                        writeMethod.invoke(target, sourceValue);
                    } else { // 如果目标对象和源对象类型相不同时，调用类型转换方法
                        String converMethod = getMethodRuleName(sourcePd.getPropertyType().getName(), targetPd
                                .getPropertyType().getName());
                        if (hasConvertMethod(converMethod)) {
                            // 调用已注册的类型转换方法
                            sourceValue = getConvertMethod(converMethod).invoke(new BeanUtils(),
                                    new Object[]{sourceValue});
                            writeMethod.invoke(target, sourceValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new UtilException("复制对象错误", e);

        }
    }

    private static void copyCollection(Object target, String[] effectiveProperties, String[] ignoreProperties,
                                       Boolean nullBeCopied, PropertyDescriptor targetPd, Object sourceValue,
                                       Method writeMethod) throws IllegalAccessException, InvocationTargetException,
            UtilException, ClassNotFoundException, InstantiationException, NoSuchFieldException {
        Method targetReadMethod = targetPd.getReadMethod();
        Collection targetValue = (Collection) targetReadMethod.invoke(target, null);
        List tempList = new ArrayList();
        if (sourceValue == null) {
            writeMethod.invoke(target, sourceValue);
            return;
        }
        if (targetValue == null) {
            if (Set.class.isAssignableFrom(targetPd.getPropertyType())) {
                targetValue = new HashSet();
            } else if (List.class.isAssignableFrom(targetPd.getPropertyType())) {
                targetValue = new ArrayList();
            } else {
                return;
            }
        }
        Object[] sourceArray = ((Collection) sourceValue).toArray();
        Object[] targetArray = targetValue.toArray();
        for (int i = 0; i < sourceArray.length; i++) {
            if (targetValue.contains(sourceArray[i])) {
                for (int j = 0; j < targetArray.length; j++) {
                    if (sourceArray[i].equals(targetArray[j])) {
                        copyProperties(sourceArray[i], targetArray[j], effectiveProperties, ignoreProperties,
                                nullBeCopied);
                        tempList.add(targetArray[j]);
                        break;
                    }
                }
            } else {
                Object tempTarget = Class.forName(sourceArray[i].getClass().getName()).newInstance();
                //基本类型直接赋值
                if (sourceArray[i].getClass().isPrimitive() || sourceArray[i] instanceof String) {
                    tempTarget = sourceArray[i];
                } else {
                    copyProperties(sourceArray[i], tempTarget, effectiveProperties, ignoreProperties, nullBeCopied);

                }

                tempList.add(tempTarget);
            }
        }
        targetValue.clear();
        targetValue.addAll(tempList);
        return;
    }

    private static String getMethodRuleName(String sourceTypeName, String targetTypeName) {
        return sourceTypeName.substring(sourceTypeName.lastIndexOf(".") + 1, sourceTypeName.length()) + "To" +
                targetTypeName.substring(targetTypeName.lastIndexOf(".") + 1, targetTypeName.length());
    }

    private static boolean hasConvertMethod(String methodName) {
        return methodMap.containsKey(methodName);
    }

    private static Method getConvertMethod(String methodName) {
        return methodMap.get(methodName);
    }

    private static PropertyDescriptor getPropertyDescriptor(Map<String, PropertyDescriptor> pdMap,
                                                            String propertyName) {
        if (pdMap.containsKey(propertyName)) {
            return pdMap.get(propertyName);
        }
        return null;
    }

    /**
     * 将所有空字符串的属性设为null
     *
     * @param object JavaBean
     */
    public static void setEntryToNull(Object object) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(object.getClass());
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        PropertyDescriptor objectPds[] = beanInfo.getPropertyDescriptors();
        Object nullObj[] = new Object[1];
        nullObj[0] = null;
        for (PropertyDescriptor objectPd : objectPds) {
            if (objectPd.getPropertyType().isAssignableFrom(String.class) && null != objectPd.getWriteMethod()) {
                try {
                    String value = (String) objectPd.getReadMethod().invoke(object, null);
                    if (value != null && value.length() == 0) {
                        objectPd.getWriteMethod().invoke(object, nullObj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String dateToString(Object obj) {
        return obj.toString();
    }

    public static Date stringToTimestamp(String obj) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return Timestamp.valueOf(obj);
    }

    public static String timeStampToString(Timestamp obj) {
        return obj.toString();
    }

    /**
     * 返回某类的所有方法集合 key = 大写的方法名 vale = 方法
     *
     * @param aClass class对象
     * @return 方法集合
     */
    public static Map<String, Method> getMethodMap(Class aClass) {
        Map methodMap = new HashMap();
        Method[] methods = aClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            methodMap.put(methods[i].getName(), methods[i]);
        }
        return methodMap;
    }

    /**
     * 深度克隆,
     * 此方法在速度上并不理想
     *
     * @param originObj 源对象
     * @return 返回复制的实体
     * @throws UtilException {@inheritDoc}
     */
    public static Object deepClone(Object originObj) throws UtilException {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(byteOut);
            oos.writeObject(originObj);
            ois = new ObjectInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
            return ois.readObject();
        } catch (IOException e) {
            throw new UtilException("Clone Object failed in IO.", e);
        } catch (ClassNotFoundException e) {
            throw new UtilException("Class not found.", e);
        } finally {
            try {
                if (oos != null) oos.close();
                if (ois != null) ois.close();
            } catch (IOException e) {
                throw new UtilException(e);
            }
        }

    }

    public static void setPropertyValueByPName(Object targetObject, String property,
                                               Object... args) throws UtilException {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(property, targetObject.getClass());
            pd.getWriteMethod().invoke(targetObject, args);
        } catch (Exception e) {
            throw new UtilException("根据属性名称赋值出错", e);
        }

    }

    /**
     * 将所有对象的属性及值都放到map中
     *
     * @param objs 实体列表
     * @return 实体转map
     * @throws ServiceException {@inheritDoc}
     */
    public static ChainMap<String, Object> getValueMap(Object... objs) throws ServiceException {
        try {
            ChainMap<String, Object> map = new ChainMap<>();
            for (Object obj : objs) {
                if (null == obj) {
                    continue;
                }
                for (Class<?> c = obj.getClass(); Object.class != c; c = c.getSuperclass()) {
                    for (Field field : c.getDeclaredFields()) {
                        field.setAccessible(true);
                        Object value = field.get(obj);
                        if (null == value) {
                            continue;
                        }
                        if(field.getType().isAssignableFrom(String.class)&& StringUtils.isEmpty((String) value)){
                            continue;
                        }
                        map.put(field.getName(), value);

                    }
                }

            }
            return map;
        } catch (Exception e) {
            throw new ServiceException("Object to Map convert Error", e);
        }

    }
}
