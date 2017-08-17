package com.github.jyoghurt.qqEmail.service;

import com.github.jyoghurt.qqEmail.domain.EmailVersionT;
import com.github.jyoghurt.qqEmail.domain.UserInfoVo;
import com.github.jyoghurt.core.service.BaseService;

import java.util.List;


/**
 * email版本服务层
 *
 */
public interface EmailVersionTService extends BaseService<EmailVersionT> {

    List<UserInfoVo> syncEmailUser(String clientId, String clientSecret)  ;


}
