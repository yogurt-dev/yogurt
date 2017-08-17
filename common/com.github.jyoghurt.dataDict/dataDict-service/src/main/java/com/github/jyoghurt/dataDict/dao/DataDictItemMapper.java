package com.github.jyoghurt.dataDict.dao;


import com.github.jyoghurt.dataDict.domain.DataDictItem;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DataDictItem Mapper
 */
public interface DataDictItemMapper extends BaseMapper<DataDictItem> {

    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    DataDictItem selectById(@Param(ENTITY_CLASS) Class entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<DataDictItem> pageData(@Param(ENTITY_CLASS) Class entityClass, @Param(DATA) Map map);

    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<DataDictItem> findAll(@Param(ENTITY_CLASS) Class entityClass, @Param(DATA) Map data);

    /**
     * 获取DictItemId SortNum最大值
     * add by limiao 20160202
     */
    @Select("select max(sortNum) from DataDictItem where parentDictItemId=#{parentDictItemId} and t.deleteFlag=0")
    Integer getMaxSortNum(@Param("parentDictItemId") String parentDictItemId);

    /**
     * 更新所有获取当前节点及所有子节点为不可用
     * add by limiao 20170721,去掉存储过程.
     */
    void logicDeleteDictItem(@Param("dictItemCodeList") List<String> dictItemCodeList);

    /**
     * 通过dictItemCode递归查询所有子节点
     * add by limiao 20170721,去掉存储过程.
     */
    List<DataDictItem> findSubDataDictItemByDictItemCode(@Param("dictItemCodeList") List<String> dictItemCodeList);


}
