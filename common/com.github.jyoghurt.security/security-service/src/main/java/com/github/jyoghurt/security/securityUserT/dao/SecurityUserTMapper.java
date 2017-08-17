package com.github.jyoghurt.security.securityUserT.dao;

import com.github.jyoghurt.core.dao.BaseMapper;
import com.github.jyoghurt.core.dao.BaseMapperProvider;
import com.github.jyoghurt.core.exception.DaoException;
import com.github.jyoghurt.security.securityRoleT.domain.SecurityRoleT;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import org.apache.ibatis.annotations.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.SELECT;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;

/**
 * @Title: 用户管理持久层
 * @Package com.df.security.securityUserT.service;
 * @Description: 用户管理数据库操作
 * @author baoxb baoxiaobing@lvyushequ.com
 * @date 2015-9-29
 * @version V1.0
 */

public interface SecurityUserTMapper extends BaseMapper<SecurityUserT> {

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "selectById")
    SecurityUserT selectById(@Param(ENTITY_CLASS) Class<SecurityUserT> entityClass, @Param(ID) Serializable id);

    @SelectProvider(type = BaseMapperProvider.class, method = "pageData")
    List<SecurityUserT> pageData(@Param(ENTITY_CLASS) Class<SecurityUserT> entityClass, @Param(DATA) Map<String, Object> map);

    @Override
    @SelectProvider(type = BaseMapperProvider.class, method = "findAll")
    List<SecurityUserT> findAll(@Param(ENTITY_CLASS) Class<SecurityUserT> entityClass, @Param(DATA) Map<String, Object> data);

    @ResultMap("securityUserTMap")
//    @ResultType(SecurityUserT.class)
    @SelectProvider(type = UserProvider.class ,method = "findUserList")
    List<SecurityUserT> findUserList(@Param(ENTITY_CLASS) Class<SecurityUserT> entityClass, @Param(DATA) Map<String, Object> map);

    @Select("select *\n" +
            "from SecurityUnitT unit\n" +
            "LEFT JOIN  SecurityUserT user\n" +
            "  on unit.unitId = user.belongOrg and user.userId = #{userId}")
    List<SecurityUnitT> findUnitListAll(@Param("userId") String userId);

    @Select("select *\n" +
            "from SecurityUserRoleR ur\n" +
            "where ur.userId = #{userId}")
    List<SecurityRoleT> queryUserRolesByUserId(@Param("userId") String userId);

    @Select("select count(*) from SecurityUserT t where t.userAccount = #{account}")
    int queryUserByAccount(@Param("account") String account);

    @Select("select count(*) from SecurityUserT")
    int queryUserCount();

    class UserProvider extends BaseMapperProvider{
        public String findUserList(Map<String,Object> map) throws DaoException {
//            String type = (String)map.get("type");
            String sql = " t.userId,t.userName,t.linkWay,t.userAccount,t.type,r.roleId,r.roleName,unit.unitId as " +
                    "'belongOrg',unit" +
                    ".unitName as " +
                    "'belongOrgName'\n" +
                    "from SecurityUserT t\n" +
                    "  left join SecurityUserRoleR ur on t.userId = ur.userId\n" +
                    "  left join SecurityRoleT r on ur.roleId = r.roleId\n" +
                    "  left join SecurityUnitT unit ON t.belongOrg = unit.unitId";
//            if(SysVarEnum.USER_TYPE_USER.equals((String)map.get("type"))){
//                sql += " where t.belongOrg = '"+map.get("belongOrg")+"'";
//            }
            beginWithClass(map);
            SELECT(sql);
            createAllWhere((Map<String, Object>) map.get(DATA), true);
            return SQL();
        }
    }

//    @Select("SELECT *\n" +
//            "from SecurityUserT u\n" +
//            "where FIND_IN_SET(u.belongOrg,getChildCompanyList(#{unitId}))")
//    List<SecurityUserT> queryContentUserByCompId(@Param("unitId") String unitId);

    @Update("UPDATE SecurityUserT u\n" +
            "SET u.userAccount = #{userAccount},u.passwd = #{passwd}\n" +
            "WHERE\n" +
            "\tu.userId = #{userId}")
    void updateAccountInfo(@Param("userId") String userId,@Param("userAccount") String userAccount,@Param("passwd")
                           String passwd);

    @Update("UPDATE SecurityUserT u\n" +
            "SET u.userName = #{userName},u.linkWay = #{linkWay},u.emailAddr = #{emailAddr}\n" +
            "WHERE\n" +
            "\tu.userId = #{userId}")
    void updateNameAndLinkWay(@Param("userId") String userId,@Param("userName") String userName,@Param("linkWay") String
                              linkWay,@Param("emailAddr") String emailAddr);

    @Select("select * from\n" +
            "SecurityUserT u\n" +
            "join SecurityUserRoleR ur on u.userId = ur.userId and u.userId = #{userId}\n" +
            "join SecurityRoleT r on ur.roleId = r.roleId and r.roleType = #{roleType}")
    List<SecurityUserT> isSysRole(@Param("userId") String userId,@Param("roleType") String roleType);

    @Select("select DISTINCT t.* from SecurityUserT t\n" +
            "join SecurityUserRoleR ur on t.userId = ur.userId and ur.deleteFlag = '0'\n" +
            "join SecurityRoleButtonR br on ur.roleId = br.roleId and br.deleteFlag = '0'\n" +
            "join SecurityButtonT b on br.buttonId = b.buttonId\n" +
            "where b.buttonCode = #{buttonCode}")
    List<SecurityUserT> queryUserByButtonCode(@Param("buttonCode") String buttonCode);

    @Update("update SecurityUserT\n" +
            "set loginVerification = ''")
    void cleanVerification();
}
