package com.github.jyoghurt.security.SecurityButtonT.service.impl;

import com.github.jyoghurt.core.service.impl.ServiceSupport;
import com.github.jyoghurt.security.SecurityButtonT.dao.SecurityButtonTMapper;
import com.github.jyoghurt.security.SecurityButtonT.domain.SecurityButtonT;
import com.github.jyoghurt.security.SecurityButtonT.service.SecurityButtonTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service("securityButtonTService")
public class SecurityButtonTServiceImpl extends ServiceSupport<SecurityButtonT, SecurityButtonTMapper> implements SecurityButtonTService {
	@Autowired
    private SecurityButtonTMapper securityButtonTMapper;

    @Override
	public SecurityButtonTMapper getMapper() {
		return securityButtonTMapper;
	}

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(SecurityButtonT.class, id);
    }

    @Override
    public SecurityButtonT find(Serializable id)  {
        return getMapper().selectById(SecurityButtonT.class,id);
    }

    @Override
    public List<SecurityButtonT> getButtonByMenuId(String menuId)  {

        return getMapper().getButtonByMenuId(menuId);
    }
}
