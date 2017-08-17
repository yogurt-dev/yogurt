package com.github.jyoghurt.dataDict.service;


import com.github.jyoghurt.dataDict.domain.DataDictValue;
import com.github.jyoghurt.dataDict.exception.DataDictValueException;
import com.github.jyoghurt.core.service.BaseService;

/**
 * 枚举值管理服务层
 */
public interface DataDictValueService extends BaseService<DataDictValue> {

    void updateDataDictValue(DataDictValue dataDictValue, boolean flag) throws DataDictValueException;

    void addDataDictValue(DataDictValue dataDictValue, boolean flag) throws DataDictValueException;

}
