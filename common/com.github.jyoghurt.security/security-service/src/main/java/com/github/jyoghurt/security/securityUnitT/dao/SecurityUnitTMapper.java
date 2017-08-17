package com.github.jyoghurt.security.securityUnitT.dao;

import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SecurityUnitT Mapper
 */
public interface SecurityUnitTMapper extends BaseMapper<SecurityUnitT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    SecurityUnitT selectById(@Param(ENTITY_CLASS) Class<SecurityUnitT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<SecurityUnitT> pageData(@Param(ENTITY_CLASS) Class<SecurityUnitT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<SecurityUnitT> findAll(@Param(ENTITY_CLASS) Class<SecurityUnitT> entityClass, @Param(DATA) Map<String, Object> data);

    /**
     * 查询子单位.
     *
     * @param unitIdList unitIdList
     * @return SecurityUnitT
     */
    List<SecurityUnitT> findSubSecurityUnitTListByUnitId(@Param("unitIdList") List<String> unitIdList);

    /**
     * 删除单位及子节点.add by limiao 20170721
     *
     * @param unitIdList unitIdList
     */
    void logicDel(@Param("unitIdList") List<String> unitIdList);

}
