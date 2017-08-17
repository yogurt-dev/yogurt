package com.github.jyoghurt.security.securityMenuT.service;

import com.github.jyoghurt.security.securityMenuT.domain.SecurityMenuT;
import com.github.jyoghurt.core.service.BaseService;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 菜单服务层
 */
public interface SecurityMenuTService extends BaseService<SecurityMenuT> {

    Map<String, List<SecurityMenuT>> queryMenuByRoleId(String pId,String roleId) ;
    Map<String, List<SecurityMenuT>> queryMenuByRoleIdAdmin(String roleId) ;

    Map<String,List<SecurityMenuT>> getUserMenu(HttpSession session);

}
