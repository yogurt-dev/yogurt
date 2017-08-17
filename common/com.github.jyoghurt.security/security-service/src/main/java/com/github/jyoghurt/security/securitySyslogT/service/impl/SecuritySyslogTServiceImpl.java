package com.github.jyoghurt.security.securitySyslogT.service.impl;

import com.github.jyoghurt.security.securitySyslogT.dao.SecuritySyslogTMapper;
import com.github.jyoghurt.security.securitySyslogT.domain.SecuritySyslogT;
import com.github.jyoghurt.security.securitySyslogT.service.SecuritySyslogTService;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service("securitySyslogTService")
public class SecuritySyslogTServiceImpl extends ServiceSupport<SecuritySyslogT, SecuritySyslogTMapper> implements SecuritySyslogTService {
	@Autowired
    private SecuritySyslogTMapper securitySyslogTMapper;

    @Override
	public SecuritySyslogTMapper getMapper() {
		return securitySyslogTMapper;
	}

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(SecuritySyslogT.class, id);
    }

    @Override
    public SecuritySyslogT find(Serializable id)  {
        return getMapper().selectById(SecuritySyslogT.class,id);
    }

}
