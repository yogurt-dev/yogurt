package com.github.jyoghurt.security.securityRoleT.service.impl;

import com.github.jyoghurt.security.securityMenuRoleR.domain.SecurityMenuRoleR;
import com.github.jyoghurt.security.securityMenuRoleR.service.SecurityMenuRoleRService;
import com.github.jyoghurt.security.securityRoleT.dao.SecurityRoleTMapper;
import com.github.jyoghurt.security.securityRoleT.domain.SecurityRoleT;
import com.github.jyoghurt.security.securityRoleT.service.SecurityRoleTService;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUnitT.service.SecurityUnitTService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.security.securityUserT.service.SecurityUserTService;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.handle.SQLJoinHandle;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.utils.SysVarEnum;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service("securityRoleTService")
public class SecurityRoleTServiceImpl extends ServiceSupport<SecurityRoleT, SecurityRoleTMapper> implements SecurityRoleTService {

    @Autowired
    private SecurityRoleTMapper securityRoleTMapper;

    @Resource
    private SecurityMenuRoleRService securityMenuRoleRService;

    @Resource
    private SecurityUserTService securityUserTService;

    @Resource
    private SecurityUnitTService securityUnitTService;

    @Override
    public SecurityRoleTMapper getMapper() {
        return securityRoleTMapper;
    }

    public void add(SecurityUserT user, SecurityRoleT securityRoleT, String menus)  {

        /*如果登录的角色为超级管理员，那么新建的角色类型为系统角色，并且新建的角色没有所属单位*/
        if (securityUserTService.isSysRole(user.getUserId(), SysVarEnum.ADMIN_ROLETYPE.getCode())) {
            securityRoleT.setRoleType(SysVarEnum.SYSTEM_ROLETYPE.getCode());
            securityRoleT.setBelongUnit("");
            securityRoleT.setBelongUnitName("");
        }else{
            /*如果登录的角色为系统角色，那么新建的角色为用户角色，所属单位为当前登录人所属单位*/
            securityRoleT.setRoleType(SysVarEnum.USER_ROLETYPE.getCode());
            securityRoleT.setBelongUnit(user.getBelongOrg());
            securityRoleT.setBelongUnitName(securityUnitTService.find(user.getBelongOrg
                    ()).getUnitName());
        }



        save(securityRoleT);
        if (StringUtils.isNotEmpty(menus)) {
            String[] menuIds = menus.split(",");
            //添加当前用户与角色修改后的关系
            for (int i = 0; i < menuIds.length; i++) {
                SecurityMenuRoleR securityMenuRoleR = new SecurityMenuRoleR();
                securityMenuRoleR.setMenuId(menuIds[i]);
                securityMenuRoleR.setRoleId(securityRoleT.getRoleId());
                securityMenuRoleRService.save(securityMenuRoleR);
            }
        }
    }

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(SecurityRoleT.class, id);
    }

    @Override
    public void editRole(SecurityRoleT securityRoleT, String menus)  {

        //更新数据，只更新前台填写的非空字段
        updateForSelective(securityRoleT);
        //删除当角色与菜单的所有关系
        //更新关系
        securityMenuRoleRService.deleteByCondition(new SecurityMenuRoleR().setRoleId(securityRoleT.getRoleId()));

        //重新维护关系
        if (StringUtils.isNotEmpty(menus)) {
            String[] menuIds = menus.split(",");
            //添加当前用户与角色修改后的关系
            for (int i = 0; i < menuIds.length; i++) {
                SecurityMenuRoleR securityMenuRoleR = new SecurityMenuRoleR();
                securityMenuRoleR.setMenuId(menuIds[i]);
                securityMenuRoleR.setRoleId(securityRoleT.getRoleId());
                securityMenuRoleRService.save(securityMenuRoleR);
            }
        }
    }

    @Override
    public SecurityRoleT find(Serializable id)  {
        return getMapper().selectById(SecurityRoleT.class, id);
    }

    @Override
    public List<SecurityRoleT> findAllByUserId(String userId) {
        return getMapper().findAllByUserId(userId);
    }


    @Override
    public List<SecurityRoleT> queryRoleByUnitId(String unitId) {
        return getMapper().queryRoleByUnitId(unitId);
    }

    @Override
    public List<SecurityUnitT> findUnitListAll(SecurityUserT loginUser, String roleId)  {

        //如果是用户级
        if (SysVarEnum.USER_TYPE_USER.equals(loginUser.getType())) {
            List<SecurityUnitT> unitVs = new ArrayList<SecurityUnitT>();
            SecurityUnitT unit = new SecurityUnitT();
            unit.setUnitId(loginUser.getBelongOrg());
            unit.setUnitName(loginUser.getBelongOrgName());
            unitVs.add(unit);
            return unitVs;
        }
        return getMapper().findUnitListAll(roleId);
    }

    @Override
    public List<SecurityRoleT> queryRoleByUserId(String userId)  {
        return getMapper().queryRoleByUserId(userId);
    }

    @Override
    public List<SecurityRoleT> queryRoleByUserIdSelected(String userId)  {
        return getMapper().queryRoleByUserIdSelected(userId);
    }

    @Override
    public List<SecurityUserT> queryUsersUnderRole(String roleId) {
        List<SecurityUserT> securityUserTs = securityUserTService.findAll(new SecurityUserT().setDeleteFlag(false),
                new QueryHandle().addJoinHandle(null, SQLJoinHandle.JoinType.JOIN, "SecurityUserRoleR ur on t.userId " +
                        "= ur.userId").addWhereSql("ur.roleId = #{data.exRoleId}").addExpandData("exRoleId",roleId));
        return securityUserTs;
    }
}
