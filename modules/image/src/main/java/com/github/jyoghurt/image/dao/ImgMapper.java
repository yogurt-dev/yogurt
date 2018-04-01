package com.github.jyoghurt.image.dao;

import com.github.jyoghurt.image.domain.ImgT;
import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Img Mapper
 *
 */
public interface ImgMapper extends BaseMapper<ImgT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    ImgT selectById(@Param(ENTITY_CLASS) Class<ImgT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<ImgT> pageData(@Param(ENTITY_CLASS) Class<ImgT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<ImgT> findAll(@Param(ENTITY_CLASS) Class<ImgT> entityClass, @Param(DATA) Map<String, Object> data);

    @Update("update\n" +
            "ImgT t\n" +
            "set t.naturalkey = #{setBusinessId}\n" +
            "where t.naturalkey = #{businessId}")
    void updateNaturalKeyByBusinessId(@Param("setBusinessId") String setBusinessId,@Param("businessId") String
            businessId);

    @Update("update\n" +
            "ImgT t\n" +
            "set t.naturalkey = #{setBusinessId}\n" +
            "where t.naturalkey = #{businessId} and type = #{type}")
    void updateNaturalKeyByBusinessIdAndType(@Param("setBusinessId") String setBusinessId,@Param("businessId") String
            businessId, @Param("type") String type);

    @Update("update\n" +
            "ImgT t\n" +
            "set t.naturalkey = #{businessId}\n" +
            "where t.imgId = #{imgId}")
    void updateNaturalKeyByImgId(@Param("businessId") String businessId,@Param("imgId") String imgId);
}
