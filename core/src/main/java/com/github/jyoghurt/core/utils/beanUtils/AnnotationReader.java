package com.github.jyoghurt.core.utils.beanUtils;

import com.github.jyoghurt.core.annotationResolver.BaseResolver;
import com.github.jyoghurt.core.exception.UtilException;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA. User: jietianwu Date: 13-11-22 Time: 上午9:24 业务实体注解读取解析类
 * todo Annotation来源需改成动态注入
 */
public class AnnotationReader {
    private static Logger logger = LoggerFactory.getLogger(AnnotationReader.class);
    private static final ConcurrentHashMap<Class<?>, EntityBinder> entityBinders = new ConcurrentHashMap<>();

    /**
     * 读取并解析业务实体中的注解信息
     *
     * @param entityClass 业务实体
     * @return EntityBinder
     * @throws UtilException {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public static EntityBinder readEntity(Class<?> entityClass) throws UtilException {
        if (entityBinders.contains(entityClass)) {
            return entityBinders.get(entityClass);
        }
        EntityBinder entityBinder = new EntityBinder();
        // 得到key为属性名，value为属性对应的PropertyDescriptor的HashMap
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(entityClass).getPropertyDescriptors()) {
                if (pd.getName().equals("class")) {
                    continue;
                }
                Field field = getField(entityClass, pd.getName());

                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    //解析主键
                    if (pd.getReadMethod().isAnnotationPresent(Id.class) || pd.getReadMethod()
                            .isAnnotationPresent(EmbeddedId.class)) {
                        entityBinder.getIdBinderList().add(new IdBinder(pd.getName(), pd,
                                pd.getReadMethod().getAnnotation(EmbeddedId.class) == null ? Id.class :
                                        EmbeddedId.class));
                        continue;
                    }
                    if (!entityBinder.getAnnotationBinders().contains(annotation.annotationType())) {
                        try {
                            entityBinder.getAnnotationBinders().add(new AnnotationBinder(
                                    ((BaseResolver) SpringContextUtils.getBean(StringUtils.join(annotation.annotationType().getSimpleName(),
                                            "Resolver"))), (Class<Annotation>) annotation.annotationType()));
                        } catch (NoSuchBeanDefinitionException e) {

                            logger.warn("没有找到注解对应的解析器{}", annotation.annotationType().getName());
                            continue;
                        }
                    }
                    entityBinder.getAnnotationBinders().get(entityBinder.getAnnotationBinders().indexOf(new AnnotationBinder((Class<Annotation>) annotation.annotationType())))
                            .getFields().add(field);
                }
            }
        } catch (Exception e) {
            throw new UtilException(e);
        }
        entityBinders.put(entityClass, entityBinder);
        return entityBinder;
    }


    private static Field getField(Class<?> aClass, String fieldName) throws NoSuchFieldException {

        try {
            return aClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            try {
                return getField(aClass.getSuperclass(), fieldName);
            } catch (NullPointerException e1) {
                System.out.println(aClass.getName() + "**********" + fieldName);
                throw new NoSuchFieldError();
            }
        }
    }

    public static void reload() {
        entityBinders.clear();
    }

}
