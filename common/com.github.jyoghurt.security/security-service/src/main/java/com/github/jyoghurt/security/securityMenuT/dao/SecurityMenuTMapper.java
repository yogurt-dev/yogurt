package com.github.jyoghurt.security.securityMenuT.dao;

import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import com.github.jyoghurt.security.securityMenuT.domain.SecurityMenuT;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SecurityMenuT Mapper
 */
public interface SecurityMenuTMapper extends BaseMapper<SecurityMenuT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    SecurityMenuT selectById(@Param(ENTITY_CLASS) Class<SecurityMenuT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<SecurityMenuT> pageData(@Param(ENTITY_CLASS) Class<SecurityMenuT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<SecurityMenuT> findAll(@Param(ENTITY_CLASS) Class<SecurityMenuT> entityClass, @Param(DATA) Map<String, Object> data);

 /*   @Select("select m.menuId,m.menuName,m.parentId,mr.roleId as roleId,m.isLeaf\n" +
            "from SecurityMenuT m\n" +
            "LEFT JOIN SecurityMenuRoleR mr on m.menuId = mr.menuId and mr.roleId = #{roleId} order by m.sortId")
    List<SecurityMenuT> queryMenuByRoleId(@Param("roleId") String roleId);*/


    @Select("SELECT\n" +
            "\tallM.menuId,\n" +
            "\tallM.menuName,\n" +
            "\tallM.parentId,\n" +
            "\tpM.roleId,\n" +
            "\tallM.isLeaf\n" +
            "FROM\n" +
            "\t(\n" +
            "\t\tSELECT DISTINCT\n" +
            "\t\t\tm.menuId,\n" +
            "\t\t\tm.menuName,\n" +
            "\t\t\tm.parentId,\n" +
            "\t\t\tm.isLeaf ,m.sortId\n" +
            "\t\tFROM\n" +
            "\t\t\tSecurityMenuT m\n" +
            "\t\tJOIN SecurityMenuRoleR mr ON m.menuId = mr.menuId\n" +
            "\t\tAND FIND_IN_SET(\n" +
            "\t\t\tmr.roleId,\n" +
            "\t\t\t#{pId}\n" +
            "\t\t)\n" +
            "\t) allM\n" +
            "LEFT JOIN (\n" +
            "\tSELECT\n" +
            "\t\tm.menuId,\n" +
            "\t\tm.menuName,\n" +
            "\t\tm.parentId,\n" +
            "\t\tmr.roleId AS roleId,\n" +
            "\t\tm.isLeaf\n" +
            "\tFROM\n" +
            "\t\tSecurityMenuT m\n" +
            "\tJOIN SecurityMenuRoleR mr ON m.menuId = mr.menuId\n" +
            "\tAND mr.roleId = #{cId}\n" +
            ") pM ON allM.menuId = pM.menuId ORDER BY allM.sortId")
    List<SecurityMenuT> queryMenuByRoleId(@Param("pId") String pId,@Param("cId") String cId);

    @Select("SELECT\n" +
            "\tallM.menuId,\n" +
            "\tallM.menuName,\n" +
            "\tallM.parentId,\n" +
            "\tpM.roleId,\n" +
            "\tallM.isLeaf\n" +
            "FROM\n" +
            "\t(\n" +
            "\t\tSELECT DISTINCT\n" +
            "\t\t\tm.menuId,\n" +
            "\t\t\tm.menuName,\n" +
            "\t\t\tm.parentId,\n" +
            "\t\t\tm.isLeaf ,m.sortId\n" +
            "\t\tFROM\n" +
            "\t\t\tSecurityMenuT m\n"+
            "\t) allM\n" +
            "LEFT JOIN (\n" +
            "\tSELECT DISTINCT\n" +
            "\t\tm.menuId,\n" +
            "\t\tm.menuName,\n" +
            "\t\tm.parentId,\n" +
            "\t\tmr.roleId AS roleId,\n" +
            "\t\tm.isLeaf\n" +
            "\tFROM\n" +
            "\t\tSecurityMenuT m\n" +
            "\tJOIN SecurityMenuRoleR mr ON m.menuId = mr.menuId\n" +
            "\tAND mr.roleId = #{cId}\n" +
            ") pM ON allM.menuId = pM.menuId ORDER BY allM.sortId")
    List<SecurityMenuT> queryMenuByRoleIdAdmin(@Param("cId") String cId);
}
