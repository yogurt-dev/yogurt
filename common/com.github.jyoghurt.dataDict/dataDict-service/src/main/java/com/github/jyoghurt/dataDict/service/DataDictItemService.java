package com.github.jyoghurt.dataDict.service;


import com.github.jyoghurt.dataDict.domain.DataDictItem;
import com.github.jyoghurt.core.service.BaseService;

import java.util.List;

/**
 * 字典项服务层
 */
public interface DataDictItemService extends BaseService<DataDictItem> {

    /**
     * 验证dataDictItem的唯一性
     *
     * @param dictItemCode 数据字典ItemCode
     * @return boolean
     */
    boolean checkUniqueDataDictItem(String dictItemCode);

    /**
     * 验证dataDictItem的唯一性
     *
     * @param dictItemCode 数据字典ItemCode
     * @param dictItemId   数据字典ItemId
     * @return boolean
     */
    boolean checkUniqueDataDictItem(String dictItemCode, String dictItemId);

    /**
     * 获取相同parentDictItemId下最大排序值
     *
     * @param parentDictItemId 父parentDictItemId
     * @return int sortNum最大值
     */
    Integer getDictItemMaxSortNum(String parentDictItemId);


    /**
     * 更新所有dictItemId状态为不可用
     *
     * @param dictItemCode dictItemCode
     */
    void logicDeleteDictItemAndSubItemsByCode(String dictItemCode);

    /**
     * 根据节点code查询所有子节点. add by limiao 20170721,去掉存储过程.
     *
     * @param dictItemCode dictItemCode
     * @return List<DataDictItem>
     */
    List<DataDictItem> findSubDataDictItemByDictItemCode(String dictItemCode);

    /**
     * 获取字典项路径
     *
     * @param dictItemId item主键
     */
    String getItemPath(String dictItemId);

    /**
     * 获取数据字典项
     *
     * @param dictItemCode 字典项编码
     */
    DataDictItem getDictItemByItemCode(String dictItemCode);
}
