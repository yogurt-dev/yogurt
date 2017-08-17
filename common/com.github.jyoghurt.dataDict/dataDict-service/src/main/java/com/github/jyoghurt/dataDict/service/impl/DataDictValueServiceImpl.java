package com.github.jyoghurt.dataDict.service.impl;


import com.github.jyoghurt.dataDict.dao.DataDictValueMapper;
import com.github.jyoghurt.dataDict.domain.DataDictValue;
import com.github.jyoghurt.dataDict.exception.DataDictValueException;
import com.github.jyoghurt.dataDict.service.DataDictValueService;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;

@Service("dataDictValueService")
public class DataDictValueServiceImpl extends ServiceSupport<DataDictValue, DataDictValueMapper> implements DataDictValueService {
    @Autowired
    private DataDictValueMapper dataDictValueMapper;

    @Override
    public DataDictValueMapper getMapper() {
        return dataDictValueMapper;
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void delete(Serializable id) {
        getMapper().delete(DataDictValue.class, id);
    }

    @Override
    public DataDictValue find(Serializable id) {
        return getMapper().selectById(DataDictValue.class, id);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void save(DataDictValue entity) {
        super.save(entity);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void saveBatch(List<DataDictValue> entities) {
        super.saveBatch(entities);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void saveForSelective(DataDictValue entity) {
        super.saveForSelective(entity);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void update(DataDictValue entity) {
        super.update(entity);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void deleteByCondition(DataDictValue entity) {
        super.deleteByCondition(entity);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void deleteByCondition(DataDictValue entity, QueryHandle queryHandle) {
        super.deleteByCondition(entity, queryHandle);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void updateForSelective(DataDictValue entity) {
        super.updateForSelective(entity);
    }

    private boolean validateDataDictValueRepeat(DataDictValue dataDictValue) {
        QueryHandle queryHandle = new QueryHandle();
        String whereSql = "t.dictItemCode =#{data.dictItemCode1} and t.dictValueCode=#{data.dictValueCode1}";
        queryHandle.addExpandData("dictItemCode1", dataDictValue.getDictItemCode())
                .addExpandData("dictValueCode1", dataDictValue.getDictValueCode());
        if (!StringUtils.isEmpty(dataDictValue.getDictValueId())) {
            whereSql = whereSql + "and t.dictValueId!=#{data.dictValueId1}";
            queryHandle.addExpandData("dictValueId1", dataDictValue.getDictValueId());
        }
        queryHandle.addWhereSql(whereSql);
        Long count = super.count(new DataDictValue(), queryHandle);
        return count > 0;
    }

    private boolean validateDataDictValueNameRepeat(DataDictValue dataDictValue) {
        QueryHandle queryHandle = new QueryHandle();
        String whereSql = "t.dictItemCode =#{data.dictItemCode1} and t.dictValueName =#{data.dictValueName1} ";
        queryHandle.addExpandData("dictItemCode1", dataDictValue.getDictItemCode());
        queryHandle.addExpandData("dictValueName1", dataDictValue.getDictValueName());
//        if (StringUtils.isEmpty(dataDictValue.getParentDictItemCode())) {
//            whereSql = whereSql + " and (t.parentDictItemCode=#{data.parentDictItemCode1} or t.parentDictItemCode is null) ";
//        } else {
//            whereSql = whereSql + " and t.parentDictItemCode=#{data.parentDictItemCode1}";
//        }
//        queryHandle.addExpandData("parentDictItemCode1", dataDictValue.getParentDictItemCode());
//        if (StringUtils.isEmpty(dataDictValue.getParentDictValueCode())) {
//            whereSql = whereSql + " and (t.parentDictValueCode=#{data.parentDictValueCode1} or t.parentDictValueCode is null) ";
//        } else {
//            whereSql = whereSql + " and t.parentDictValueCode=#{data.parentDictValueCode1}";
//        }
//        queryHandle.addExpandData("parentDictItemCode1", dataDictValue.getParentDictItemCode());
        if (!StringUtils.isEmpty(dataDictValue.getDictValueId())) {
            whereSql = whereSql + " and t.dictValueId!=#{data.dictValueId1}";
            queryHandle.addExpandData("dictValueId1", dataDictValue.getDictValueId());
        }
        queryHandle.addWhereSql(whereSql);
        Long count = super.count(new DataDictValue(), queryHandle);
        return count > 0;
    }

    private void validateParam(DataDictValue dataDictValue, boolean flag) throws DataDictValueException {
        if (StringUtils.isEmpty(dataDictValue.getDictValueCode())) {
            throw new DataDictValueException(DataDictValueException.ERROR_1802);
        }
        if (StringUtils.isEmpty(dataDictValue.getDictValueName())) {
            throw new DataDictValueException(DataDictValueException.ERROR_1801);
        }
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void updateDataDictValue(DataDictValue dataDictValue, boolean flag) throws DataDictValueException {
        validateParam(dataDictValue, flag);
        if (validateDataDictValueRepeat(dataDictValue)) {
            throw new DataDictValueException();
        }
        if (!flag && validateDataDictValueNameRepeat(dataDictValue)) {
            throw new DataDictValueException(DataDictValueException.ERROR_1803);
        }
        this.updateForSelective(dataDictValue);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void addDataDictValue(DataDictValue dataDictValue, boolean flag) throws DataDictValueException {
        validateParam(dataDictValue, flag);
        if (validateDataDictValueRepeat(dataDictValue)) {
            throw new DataDictValueException();
        }
        if (!flag && validateDataDictValueNameRepeat(dataDictValue)) {
            throw new DataDictValueException(DataDictValueException.ERROR_1803);
        }
        this.save(dataDictValue);
    }

}
