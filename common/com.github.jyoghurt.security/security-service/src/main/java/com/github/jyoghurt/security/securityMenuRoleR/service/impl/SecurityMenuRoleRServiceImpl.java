package com.github.jyoghurt.security.securityMenuRoleR.service.impl;

import com.github.jyoghurt.core.service.impl.ServiceSupport;
import com.github.jyoghurt.security.securityMenuRoleR.dao.SecurityMenuRoleRMapper;
import com.github.jyoghurt.security.securityMenuRoleR.domain.SecurityMenuRoleR;
import com.github.jyoghurt.security.securityMenuRoleR.service.SecurityMenuRoleRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("securityMenuRoleRService")
public class SecurityMenuRoleRServiceImpl extends ServiceSupport<SecurityMenuRoleR, SecurityMenuRoleRMapper> implements SecurityMenuRoleRService {
	@Autowired
    private SecurityMenuRoleRMapper securityMenuRoleRMapper;

    @Override
	public SecurityMenuRoleRMapper getMapper() {
		return securityMenuRoleRMapper;
	}

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(SecurityMenuRoleR.class, id);
    }

    @Override
    public SecurityMenuRoleR find(Serializable id)  {
        return getMapper().selectById(SecurityMenuRoleR.class,id);
    }

//    @Override
//    public void deleteRelByRoleId(String roleId) {
//        getMapper().deleteRelByRoleId(roleId);
//    }
}
