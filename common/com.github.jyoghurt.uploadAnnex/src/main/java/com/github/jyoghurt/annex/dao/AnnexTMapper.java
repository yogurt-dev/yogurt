package com.github.jyoghurt.annex.dao;


import com.github.jyoghurt.annex.domain.AnnexT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * AnnexT Mapper
 *
 */
public interface AnnexTMapper extends BaseMapper<AnnexT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    AnnexT selectById(@Param(ENTITY_CLASS) Class<AnnexT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<AnnexT> pageData(@Param(ENTITY_CLASS) Class<AnnexT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<AnnexT> findAll(@Param(ENTITY_CLASS) Class<AnnexT> entityClass, @Param(DATA) Map<String, Object> data);
}
