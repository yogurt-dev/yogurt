package com.github.jyoghurt.activiti.adapter.util;

import com.github.jyoghurt.dataDict.domain.DataDictValue;
import com.github.jyoghurt.dataDict.service.DataDictUtils;
import org.activiti.engine.form.FormProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * user: dell
 * date:2016/3/29.
 */
public class TransFormUtil {
    private static Logger logger = LoggerFactory.getLogger(TransFormUtil.class);

    public static String transFormCompontentTitle(FormProperty formProperty) {
        //解决activiti formProperty乱码问题  根据规则从数据字典中获取
        String[] titleArgs = formProperty.getName().split("\\.");
        if (titleArgs.length != 2) {
            logger.info("转换activiti formpropty compontentTitle失败,详细信息:{},原因:{}", formProperty.toString(), "compontentTitle格式不符");
        }
        try {
            if (titleArgs.length == 1) {
                return new String(titleArgs[0].getBytes());
            }
            //modify by limiao 数据字典改造 20161018
            DataDictValue dataDictValue = DataDictUtils.getDataDictValue(titleArgs[0], titleArgs[1]);
            if (null == dataDictValue) {
                logger.error("转换activiti formpropty compontentTitle失败,详细信息:{},原因:{}", formProperty.toString(), "未配置数据字典");
                return null;
            }
            return dataDictValue.getDictValueName();
        } catch (Exception e) {
            logger.error("转换activiti formpropty compontentTitle失败");
        }
        return null;
    }
}
