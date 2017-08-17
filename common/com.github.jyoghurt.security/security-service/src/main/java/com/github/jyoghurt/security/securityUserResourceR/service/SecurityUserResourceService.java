package com.github.jyoghurt.security.securityUserResourceR.service;

import com.github.jyoghurt.security.enums.Module;
import com.github.jyoghurt.security.securityUserResourceR.domain.SecurityUserResourceR;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.core.service.BaseService;

import java.util.List;

/**
 * 人员资源关系服务层
 *
 */
public interface SecurityUserResourceService extends BaseService<SecurityUserResourceR> {

    /**
     * 查询用户下包含的资源
     * @param userId
     * @param module
     * @param type
     * @return

     */
    List<SecurityUserResourceR> fetchResourcesObj(String userId, Module module,String type) ;

    /**
     * 根据所属模块，资源类型，查询用户下包含的资源，返回字符串集合
     * @param userId 当前登陆人ID
     * @param module 模块
     * @param type 资源类型
     * @return

     */
    List<String> fetchResourcesStr(String userId, Module module,String type) ;

    /**
     * 根据当前登陆人，资源类型，查询用户下包含的资源，返回字符串集合
     * @param userId 当前登陆人ID
     * @param type 资源类型
     * @return

     */
    List<String> fetchResourcesStr(String userId,String type) ;

    List<String> fetchResourcesStr(String userId, String... types);

    /**
     * 查询某资源下的人员列表
     * @param resourceId
     * @return
     */
    List<SecurityUserT> queryUsersBelongToResource(String resourceId,String roleId);
}
