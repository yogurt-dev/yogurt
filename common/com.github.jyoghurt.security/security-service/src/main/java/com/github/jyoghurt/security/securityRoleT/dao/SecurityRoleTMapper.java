package com.github.jyoghurt.security.securityRoleT.dao;

import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import com.github.jyoghurt.security.securityRoleT.domain.SecurityRoleT;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SecurityRoleT Mapper
 */
public interface SecurityRoleTMapper extends BaseMapper<SecurityRoleT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    SecurityRoleT selectById(@Param(ENTITY_CLASS) Class<SecurityRoleT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<SecurityRoleT> pageData(@Param(ENTITY_CLASS) Class<SecurityRoleT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<SecurityRoleT> findAll(@Param(ENTITY_CLASS) Class<SecurityRoleT> entityClass, @Param(DATA) Map<String, Object> data);

    @ResultMap("securityRoleTMap")
    @Select("select r.*,u.userId\n" +
            "  from SecurityRoleT r\n" +
            "  JOIN SecurityUserRoleR ur on r.roleId = ur.roleId and ur.userId = #{userId}\n" +
            "  join SecurityUserT u on ur.userId = u.userId")
    List<SecurityRoleT> findAllByUserId(@Param("userId") String userId);

    @ResultMap("securityRoleTMap")
    @Select("select * from SecurityRoleT r\n" +
            "where r.belongUnit = #{unitId}")
    List<SecurityRoleT> queryRoleByUnitId(@Param("unitId") String unitId);

    @Select("select *\n" +
            "from SecurityUnitT unit\n" +
            "LEFT JOIN SecurityRoleT r on unit.unitId = r.belongUnit and r.roleId = #{roleId}")
    List<SecurityUnitT> findUnitListAll(@Param("roleId") String roleId);


    @Select("SELECT r.*,ut.unitName as 'belongUnitName'\n" +
            "from SecurityRoleT r\n" +
            "join SecurityUserT u on r.belongUnit = u.belongOrg and u.userId =#{userId}\n" +
            "join SecurityUnitT ut on u.belongOrg = ut.unitId")
    List<SecurityRoleT> queryRoleByUserId(@Param("userId") String userId);

    @Select("select r.*\n" +
            "from SecurityRoleT r\n" +
            "join SecurityUserRoleR ur on r.roleId = ur.roleId and ur.userId=#{userId}")
    List<SecurityRoleT> queryRoleByUserIdSelected(@Param("userId") String userId);

}
