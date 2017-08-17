package com.github.jyoghurt.security.securityDataDic.dao;

import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import com.github.jyoghurt.security.securityDataDic.domain.SecurityDataDic;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SecurityDataDic Mapper
 *
 */
public interface SecurityDataDicMapper extends BaseMapper<SecurityDataDic> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    SecurityDataDic selectById(@Param(ENTITY_CLASS) Class<SecurityDataDic> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<SecurityDataDic> pageData(@Param(ENTITY_CLASS) Class<SecurityDataDic> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<SecurityDataDic> findAll(@Param(ENTITY_CLASS) Class<SecurityDataDic> entityClass, @Param(DATA) Map<String, Object> data);

    @Select("select DISTINCT t.col1Col4s as 'key',t.col2Col4s as 'dicName' from \n" +
            "SecurityUnitT t\n" +
            "where t.type = '0'\n" +
            "and t.col1Col4s <> '' and t.col2Col4s <> ''")
    List<SecurityDataDic> queryBusinessAreaDic();
}
