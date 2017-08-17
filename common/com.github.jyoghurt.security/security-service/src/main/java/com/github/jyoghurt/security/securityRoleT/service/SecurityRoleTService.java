package com.github.jyoghurt.security.securityRoleT.service;

import com.github.jyoghurt.security.securityRoleT.domain.SecurityRoleT;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.core.service.BaseService;

import java.util.List;

/**
 * 菜单服务层
 *
 */
public interface SecurityRoleTService extends BaseService<SecurityRoleT> {

    /**
     * 新增角色
     * @param securityRoleT 角色实体
     * @param menus  包含菜单

     */
    void add(SecurityUserT curentUser, SecurityRoleT securityRoleT, String menus) ;

    /**
     * 查询字典中的角色列表，根据用户ID判断哪些角色为该用户所用
     * @param userId
     * @return
     */
    List<SecurityRoleT> findAllByUserId(String userId);

    /**
     * 根据组织机构ID查询角色信息
     * @param unitId
     * @return
     */
    List<SecurityRoleT> queryRoleByUnitId(String unitId);

    /**
     * 编辑角色信息
     * @param securityRoleT 角色实体
     * @param menus 角色包含菜单信息

     */
    void editRole(SecurityRoleT securityRoleT,String menus) ;

    /**
     * 查询所有单位信息，并根据用户ID将其对应的单位设置为选中
     * @param loginUser 当前登录系统的用户
     * @param roleId 被操作角色ID
     * @return

     */
    List<SecurityUnitT> findUnitListAll(SecurityUserT loginUser, String roleId) ;

    /**
     * 查询所有单位信息，并根据用户ID将其对应的单位设置为选中
     * @param userId 用户Id
     * @return

     */
    List<SecurityRoleT> queryRoleByUserId(String userId) ;


    /**
     * 根据用户ID查询其具备的角色列表
     * @param userId 用户Id
     * @return

     */
    List<SecurityRoleT> queryRoleByUserIdSelected(String userId) ;


    /**
     * 根据角色ID查询角色下的人员信息
     * @param roleId
     * @return
     */
    List<SecurityUserT> queryUsersUnderRole(String roleId);
}
