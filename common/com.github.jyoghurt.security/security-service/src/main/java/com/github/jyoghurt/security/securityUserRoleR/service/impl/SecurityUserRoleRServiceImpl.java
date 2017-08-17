package com.github.jyoghurt.security.securityUserRoleR.service.impl;

import com.github.jyoghurt.core.service.impl.ServiceSupport;
import com.github.jyoghurt.security.securityUserRoleR.dao.SecurityUserRoleRMapper;
import com.github.jyoghurt.security.securityUserRoleR.domain.SecurityUserRoleR;
import com.github.jyoghurt.security.securityUserRoleR.service.SecurityUserRoleRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("securityUserRoleRService")
public class SecurityUserRoleRServiceImpl extends ServiceSupport<SecurityUserRoleR, SecurityUserRoleRMapper> implements SecurityUserRoleRService {
	@Autowired
    private SecurityUserRoleRMapper securityUserRoleRMapper;

    @Override
	public SecurityUserRoleRMapper getMapper() {
		return securityUserRoleRMapper;
	}

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(SecurityUserRoleR.class, id);
    }

    @Override
    public SecurityUserRoleR find(Serializable id)  {
        return getMapper().selectById(SecurityUserRoleR.class,id);
    }

    @Override
    public void deleteRelByUserId(String userId)  {
        securityUserRoleRMapper.deleteRelByUserId(userId);
    }
}
