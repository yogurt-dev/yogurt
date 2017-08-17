package com.github.jyoghurt.security.securityUserRoleR.service;

import com.github.jyoghurt.core.service.BaseService;
import com.github.jyoghurt.security.securityUserRoleR.domain.SecurityUserRoleR;

/**
 * 菜单服务层
 *
 */
public interface SecurityUserRoleRService extends BaseService<SecurityUserRoleR> {

    public void deleteRelByUserId(String userId) ;

}
