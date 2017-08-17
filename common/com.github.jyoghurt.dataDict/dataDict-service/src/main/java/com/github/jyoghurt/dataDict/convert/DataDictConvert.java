package com.github.jyoghurt.dataDict.convert;

import com.github.jyoghurt.dataDict.domain.DataDictValue;

import java.util.HashMap;
import java.util.List;


public class DataDictConvert {

    public static HashMap<String, DataDictValue> convert(List<DataDictValue> allDataDictValuesList) {
        HashMap<String, DataDictValue> map = new HashMap<>();
        for (DataDictValue dataDictValue : allDataDictValuesList) {
            map.put(getParentKey(dataDictValue), dataDictValue);
        }
        return map;
    }

    public static String getKey(DataDictValue dataDictValue) {
        String itemCode = dataDictValue.getDictItemCode() == null ? "" : dataDictValue.getDictItemCode();
        String valueCode = dataDictValue.getDictValueCode() == null ? "" : dataDictValue.getDictValueCode();
        return itemCode + "__-__" + valueCode;
    }

    public static String getParentKey(DataDictValue dataDictValue) {
        String itemCode = dataDictValue.getParentDictItemCode() == null ? "" : dataDictValue.getParentDictItemCode();
        String valueCode = dataDictValue.getParentDictValueCode() == null ? "" : dataDictValue.getParentDictValueCode();
        return itemCode + "__-__" + valueCode;
    }
}
