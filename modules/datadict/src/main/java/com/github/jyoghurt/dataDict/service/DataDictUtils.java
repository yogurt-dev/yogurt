package com.github.jyoghurt.dataDict.service;


import com.github.jyoghurt.dataDict.domain.DataDictValue;
import com.github.jyoghurt.core.utils.SpringContextUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA. User: metarne Date: 13-4-5 Time: 上午11:44
 * 数据字典服务代理类接口
 */

public class DataDictUtils {
    private static DataDictService dataDictService;

    private static DataDictService getDataDictService() {
        if (null == dataDictService) {
            dataDictService = (DataDictService) SpringContextUtils.getBean("dataDictService");
        }
        return dataDictService;
    }

    /**
     * 根据字典项编码获取字典值列表
     *
     * @param dictItemCode 字典项编码
     * @return
     */
    public static List<DataDictValue> geDataDictValuesByItemCode(String dictItemCode)  {
        return getDataDictService().geDataDictValuesByItemCode(dictItemCode);
    }

    /**
     * 根据字典项编码和字典值编码获取字典信息
     *
     * @param dictItemCode  字典项编码
     * @param dictValueCode 字典值编码
     * @return 字典值
      {@inheritDoc}
     */
    public static DataDictValue getDataDictValue(String dictItemCode, String dictValueCode)  {
        return getDataDictService().getDataDictValue(dictItemCode, dictValueCode);
    }

    /**
     * 根据字典项编码和字典值编码获取字典信息
     *
     * @param enumValue 枚举值
     * @return 字典值
      {@inheritDoc}
     */
    public static DataDictValue getDataDictValue(Enum enumValue)  {
        return getDataDictService().getDataDictValue(enumValue);
    }

    /**
     * 获取字典项的所有字典值
     *
     * @param dictItemCode 字典项编码
     * @return 字典值
      {@inheritDoc}
     */
    public static List<DataDictValue> getDataDictValues(String dictItemCode)  {
        return getDataDictService().getDataDictValues(dictItemCode);
    }
    public static Map<String ,DataDictValue> getDataDictValueMap(String dictItemCode)  {
            List<DataDictValue> dataDictValues = getDataDictService().getDataDictValues(dictItemCode);
            if(CollectionUtils.isEmpty(dataDictValues)){
                return Collections.EMPTY_MAP;
            }
            Map<String,DataDictValue> map = new HashMap();
            for(DataDictValue value : dataDictValues){
                map.put(value.getDictValueCode(),value);
            }
            return map;
    }
    /**
     * 把类名当做枚举项code，查询旗下所有枚举值，并根据枚举值的code与类属性的对应关系，自动填充对象
     * eg：SmsConfig
     * public class SmsConfig {
     * private String serverPort;
     * private String accountSid;
     * private String appId;
     * private String accountToken;
     * private String serverIP;
     * ...
     * }
     * <p>
     * 该方法会自动将枚举项SmsConfig下的枚举值的value填充到对应的属性上
     *
     * @param entityClass 需要返回的枚举类
     * @return 枚举项对象

     */
    public static Object getDataDictObject(Class entityClass)  {
        return getDataDictService().getDataDictObject(entityClass);
    }
    /**
     * 根据字典编码等信息查询字典值
     *
     * @param dictItemCode  字典项编码
     * @param dictValueCode 字典值编码
     * @return
     */
//    public DataDictValue getDataDictValue(String dictItemCode, String dictValueCode) ;


    /**
     * 递归查询数据字典名字
     *
     * @param dictItemCode  字典项编码
     * @param dictValueCode 字典值编码
     * @param extend        字典名是否包含父字典项名称
     */
    public static String getDataDictValueName(String dictItemCode, String dictValueCode, Boolean extend)  {
        return getDataDictService().getDataDictValueName(dictItemCode, dictValueCode, extend);
    }

    /**
     * 清除缓存
     *

     */
    public static void cacheEvict()  {
        getDataDictService().cacheEvict();
    }


    /**
     * 查询字典值列表
     *
     * @param dictItemCode        字典项编码
     * @param dictValueDesc       字典值描述
     * @param parentDictValueCode 字典值编码
     * @return 字典值列表
     */
    public static List<DataDictValue> getDataDictValues(String dictItemCode, String parentDictValueCode, String dictValueDesc)  {
        return getDataDictService().getDataDictValues(dictItemCode, parentDictValueCode, dictValueDesc);
    }

    /**
     * 根据字典项编码获取字典值列表
     *
     * @param dictItemCode  字典项编码
     * @param dictValueDesc 字典值描述
     * @return 字典值列表
     */
    public static List<DataDictValue> geDataDictValues(String dictItemCode, String dictValueDesc)  {
        return getDataDictService().geDataDictValues(dictItemCode, dictValueDesc);
    }


    public static List<DataDictValue> getDataDictAndSubValues(String dictItemCode, String parentDictValueCode)  {
        return getDataDictService().getDataDictAndSubValues(dictItemCode, parentDictValueCode);
    }

}
