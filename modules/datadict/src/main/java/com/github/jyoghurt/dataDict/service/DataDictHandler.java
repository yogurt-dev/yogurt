package com.github.jyoghurt.dataDict.service;


import com.github.jyoghurt.dataDict.domain.DataDictValue;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.utils.StringUtils;

public class DataDictHandler {

    public static String get400Tel() {
        DataDictValue dataDictValue = DataDictUtils.getDataDictValue("tel400", "tel400");
        if (dataDictValue == null || StringUtils.isEmpty(dataDictValue.getDictValueName())) {
            throw new BaseErrorException("获取400电话失败！");
        }
        return dataDictValue.getDictValueName();
    }

    public static String getDeliveryTime() {
        DataDictValue dataDictValue = DataDictUtils.getDataDictValue("deliveryTime", "deliveryTime");
        if (dataDictValue == null || StringUtils.isEmpty(dataDictValue.getDictValueName())) {
            throw new BaseErrorException("获取deliveryTime失败！");
        }
        return dataDictValue.getDictValueName();
    }

}
