package com.github.jyoghurt.dataDict.service.impl;

import com.github.jyoghurt.dataDict.dao.DataDictItemMapper;
import com.github.jyoghurt.dataDict.domain.DataDictItem;
import com.github.jyoghurt.dataDict.service.DataDictItemService;
import com.github.jyoghurt.dataDict.util.NumberUtils;
import com.github.jyoghurt.core.handle.OperatorHandle;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service("dataDictItemService")
public class DataDictItemServiceImpl extends ServiceSupport<DataDictItem, DataDictItemMapper> implements DataDictItemService {

    private static Logger logger = LoggerFactory.getLogger(DataDictItemServiceImpl.class);

    @Autowired
    private DataDictItemMapper dataDictItemMapper;

    @Override
    public DataDictItemMapper getMapper() {
        return dataDictItemMapper;
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void delete(Serializable id) {
        dataDictItemMapper.delete(DataDictItem.class, id);
    }

    @Override
    public DataDictItem find(Serializable id) {
        return dataDictItemMapper.selectById(DataDictItem.class, id);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void save(DataDictItem entity) {
        super.save(entity);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void saveBatch(List<DataDictItem> entities) {
        super.saveBatch(entities);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void saveForSelective(DataDictItem entity) {
        super.saveForSelective(entity);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void update(DataDictItem entity) {
        super.update(entity);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void deleteByCondition(DataDictItem entity) {
        super.deleteByCondition(entity);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void deleteByCondition(DataDictItem entity, QueryHandle queryHandle) {
        super.deleteByCondition(entity, queryHandle);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void updateForSelective(DataDictItem entity) {
        super.updateForSelective(entity);
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public boolean checkUniqueDataDictItem(String dictItemCode) {
        DataDictItem dataDictItem = new DataDictItem();
        dataDictItem.setDictItemCode(dictItemCode);
        Long count = super.count(dataDictItem, new QueryHandle().addOperatorHandle("dictItemCode", OperatorHandle.operatorType.EQUAL));
        return count == 0;
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public boolean checkUniqueDataDictItem(String dictItemCode, String dictItemId) {
        DataDictItem dataDictItem = new DataDictItem();
        dataDictItem.setDictItemCode(dictItemCode);
        dataDictItem.setDictItemId(dictItemId);
/*        Long count = super.count(dataDictItem, new QueryHandle().addOperatorHandle("dictItemCode", OperatorHandle.operatorType.EQUAL)
                .addOperatorHandle("dictItemId", OperatorHandle.operatorType*//**//*.NOT_EQUAL));*/
        return true;
    }

    @Override
    public synchronized Integer getDictItemMaxSortNum(String parentDictItemId) {
        Integer sortNum = dataDictItemMapper.getMaxSortNum(parentDictItemId);
        sortNum = NumberUtils.nullToZero(sortNum);
        return sortNum;
    }

    @CacheEvict(value = "dataDict", allEntries = true)
    @Override
    public void logicDeleteDictItemAndSubItemsByCode(String dictItemCode) {
        dataDictItemMapper.logicDeleteDictItem(getDictItemCodeList(this.findSubDataDictItemByDictItemCode(dictItemCode)));
    }

    @Override
    public List<DataDictItem> findSubDataDictItemByDictItemCode(String dictItemCode) {
        DataDictItem dataDictItem = this.findDataDictItemByCode(dictItemCode);
        if (dataDictItem == null) {
            return null;
        }
        List<DataDictItem> dataDictItemList = new ArrayList<>();
        dataDictItemList.add(dataDictItem);
        List<String> parentDataDictItemList = new ArrayList<>();
        parentDataDictItemList.add(dictItemCode);
        dataDictItemList = this.findSubDataDictItemByParentDictItemCodeList(dataDictItemList, parentDataDictItemList);
        return dataDictItemList;
    }

    /**
     * 递归查询子数据字典项.add by limiao 20170721,去掉存储过程.
     *
     * @param dataDictItemList       引用最后的返回值list
     * @param parentDataDictItemList 父dataDictItemCode集合
     * @return List<DataDictItem>
     */
    private List<DataDictItem> findSubDataDictItemByParentDictItemCodeList(List<DataDictItem> dataDictItemList, List<String> parentDataDictItemList) {
        if (CollectionUtils.isEmpty(parentDataDictItemList)) {
            return dataDictItemList;
        }
        List<DataDictItem> subDataDictItems = dataDictItemMapper.findSubDataDictItemByDictItemCode(parentDataDictItemList);
        if (CollectionUtils.isNotEmpty(subDataDictItems)) {
            dataDictItemList.addAll(subDataDictItems);
            findSubDataDictItemByParentDictItemCodeList(dataDictItemList, getDictItemCodeList(subDataDictItems));
        }
        return dataDictItemList;
    }

    /**
     * 数据字典对象集合转换成code集合.add by limiao 20170721,去掉存储过程.
     *
     * @param dictItemList dictItemList
     * @return List<String>
     */
    private List<String> getDictItemCodeList(List<DataDictItem> dictItemList) {
        List<String> dataDictItemList = new ArrayList<>();
        for (DataDictItem dataDictItem : dictItemList) {
            dataDictItemList.add(dataDictItem.getDictItemCode());
        }
        return dataDictItemList;
    }

    /**
     * 通过dictItemCode查询DataDictItem.add by limiao 20170721,去掉存储过程.
     *
     * @param dictItemCode dictItemCode
     * @return DataDictItem
     */
    private DataDictItem findDataDictItemByCode(String dictItemCode) {
        List<DataDictItem> list = this.findAll(new DataDictItem().setDictItemCode(dictItemCode), new QueryHandle());
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public String getItemPath(String dictItemCode) {
        DataDictItem dataDictItem = getDictItemByItemCode(dictItemCode);
        if (dataDictItem == null) {
            return null;
        }
        if (StringUtils.isEmpty(dataDictItem.getParentDictItemCode())) {
            return dataDictItem.getDictItemCode();
        }
        String itemPath = getItemPath(dataDictItem.getParentDictItemCode());
        if (null == itemPath) {
            return dataDictItem.getDictItemCode();
        }
        return StringUtils.join(itemPath, ",", dataDictItem.getDictItemCode());
    }

    @Override
    public DataDictItem getDictItemByItemCode(String dictItemCode) {
        List<DataDictItem> dictItems = findAll(new DataDictItem().setDictItemCode(dictItemCode));
        if (CollectionUtils.isEmpty(dictItems)) {
            return null;
        }
        return dictItems.get(0);
    }
}
