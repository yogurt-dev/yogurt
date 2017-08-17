package com.github.jyoghurt.security.securityUserUnitR.service.impl;

import com.github.jyoghurt.security.securityUserUnitR.dao.SecurityUserUnitMapper;
import com.github.jyoghurt.security.securityUserUnitR.domain.SecurityUserUnitR;
import com.github.jyoghurt.security.securityUserUnitR.service.SecurityUserUnitService;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("securityUserUnitService")
public class SecurityUserUnitServiceImpl extends ServiceSupport<SecurityUserUnitR, SecurityUserUnitMapper> implements SecurityUserUnitService {
	@Autowired
    private SecurityUserUnitMapper securityUserUnitMapper;

    @Override
	public SecurityUserUnitMapper getMapper() {
		return securityUserUnitMapper;
	}

    @Override
    public void logicDelete(Serializable id)  {
        getMapper().logicDelete(SecurityUserUnitR.class, id);
    }

    @Override
    public SecurityUserUnitR find(Serializable id)  {
        return getMapper().selectById(SecurityUserUnitR.class,id);
    }
}
