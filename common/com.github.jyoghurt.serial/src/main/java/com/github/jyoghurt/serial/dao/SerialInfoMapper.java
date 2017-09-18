package com.github.jyoghurt.serial.dao;


import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import com.github.jyoghurt.serial.domain.SerialInfoT;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SerialInfo Mapper
 */
public interface SerialInfoMapper extends BaseMapper<SerialInfoT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    SerialInfoT selectById(@Param(ENTITY_CLASS) Class<SerialInfoT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<SerialInfoT> pageData(@Param(ENTITY_CLASS) Class<SerialInfoT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<SerialInfoT> findAll(@Param(ENTITY_CLASS) Class<SerialInfoT> entityClass, @Param(DATA) Map<String, Object> data);

    @Update("update SerialInfoT s\n" +
            "set s.serialNo = s.serialNo+${step}\n" +
            "where s.module = #{module} and s.generateDate = STR_TO_DATE(#{date},'%Y-%m-%d')")
    void updateSeriNo(@Param("module") String module, @Param("date") String date, @Param("step") Integer step);

    @Update("update SerialInfoT s\n" +
            "set s.serialNo = s.serialNo+${step}\n" +
            "where s.module = #{module}")
    void updateNormalSeriNo(@Param("module") String module, @Param("step") Integer step);

    @Update("update SerialInfoT s\n" +
            "set s.serialNo = s.serialNo+${step}\n" +
            "where s.module = #{module} and s.type = #{type}")
    void updateCommSeriNo(@Param("module") String module, @Param("step") Integer step, @Param("type") String type);

    @Select("select *\n" +
            "from SerialInfoT s\n" +
            "where s.module = #{module} and s.type = #{type} and s.generateDate = STR_TO_DATE(#{date},'%Y-%m-%d')")
    List<SerialInfoT> queryListAll(@Param("module") String module, @Param("date") String date, @Param("type") String type);
}
