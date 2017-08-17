package com.github.jyoghurt.security.SecurityRoleButtonR.service;

import com.github.jyoghurt.core.service.BaseService;
import com.github.jyoghurt.security.SecurityRoleButtonR.domain.SecurityRoleButtonR;

import java.util.List;


/**
 * SecurityRoleButtonR服务层
 *
 */
public interface SecurityRoleButtonRService extends BaseService<SecurityRoleButtonR> {

   // List<SecurityButtonT> getButtonByUserId(String userId);
    List<String> getButtonByUserId(String userId);
}
