package com.github.jyoghurt.security.SecurityButtonT.service;

import com.github.jyoghurt.core.service.BaseService;
import com.github.jyoghurt.security.SecurityButtonT.domain.SecurityButtonT;

import java.util.List;


/**
 * SecurityButtonT服务层
 *
 */
public interface SecurityButtonTService extends BaseService<SecurityButtonT> {
   List<SecurityButtonT> getButtonByMenuId(String menuId);
}
