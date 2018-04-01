package com.github.jyoghurt.dataDict.annotationResolver;


import com.github.jyoghurt.dataDict.annotations.DataDictValueField;
import com.github.jyoghurt.dataDict.domain.DataDictValue;
import com.github.jyoghurt.dataDict.service.DataDictUtils;
import com.github.jyoghurt.core.annotationResolver.BaseResolver;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.exception.UtilException;
import com.github.jyoghurt.core.utils.JPAUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * Created by Administrator on 2015/6/3.
 */
@Component("DataDictValueFieldResolver")
public class DataDictValueFieldResolver implements BaseResolver {

    @Override
    public void resolve(Object entity, Field field) throws UtilException {
        //原和目标数据为空时不做处理
        try {
            //原和目标数据为空时不做处理
            PropertyDescriptor sourcePD = new PropertyDescriptor(field.getName(), entity.getClass());
            String fieldValue = (String) sourcePD.getReadMethod().invoke(entity, null);
            if (StringUtils.isEmpty(fieldValue)) {
                return ;
            }
            PropertyDescriptor tagerPD = new PropertyDescriptor(field.getAnnotation(DataDictValueField
                    .class).name(), entity.getClass());
            if (null != tagerPD.getReadMethod().invoke(entity, null)) {
                return ;
            }
            //获取枚举项，两种方式itemCode和itemField
            String dictItemCode = field.getAnnotation(DataDictValueField.class).itemCode();
            if (StringUtils.isNotEmpty(field.getAnnotation(DataDictValueField.class).itemField())) {
                String[] itemCodeFields = field.getAnnotation(DataDictValueField.class).itemField().split("\\.");
                Object object = entity;
                for (String itemCodeField : itemCodeFields) {
                    object = JPAUtils.getValue(object, itemCodeField);
                }
                dictItemCode = (String) object;
            }
            DataDictValue ev = DataDictUtils.getDataDictValue(dictItemCode, fieldValue);
            if (ev == null) {
                throw new BaseErrorException("枚举值读取错误,dictItemCode=" + dictItemCode + "class=" + entity.getClass()
                        .getName() + "&fieldName=" + sourcePD.getName() + "&value=" + sourcePD.getReadMethod()
                        .invoke(entity, null));
            }
            tagerPD.getWriteMethod().invoke(entity, ev.getDictValueName());
        } catch (Exception e) {
            throw new UtilException(e);
        }
    }
}
