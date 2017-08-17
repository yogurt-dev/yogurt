package com.github.jyoghurt.security.SecurityRoleButtonR.service.impl;

import com.github.jyoghurt.core.service.impl.ServiceSupport;
import com.github.jyoghurt.security.SecurityButtonT.domain.SecurityButtonT;
import com.github.jyoghurt.security.SecurityRoleButtonR.dao.SecurityRoleButtonRMapper;
import com.github.jyoghurt.security.SecurityRoleButtonR.domain.SecurityRoleButtonR;
import com.github.jyoghurt.security.SecurityRoleButtonR.service.SecurityRoleButtonRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service("securityRoleButtonRService")
public class SecurityRoleButtonRServiceImpl extends ServiceSupport<SecurityRoleButtonR, SecurityRoleButtonRMapper> implements SecurityRoleButtonRService {
	@Autowired
    private SecurityRoleButtonRMapper securityRoleButtonRMapper;

    @Override
	public SecurityRoleButtonRMapper getMapper() {
		return securityRoleButtonRMapper;
	}

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(SecurityRoleButtonR.class, id);
    }

    @Override
    public SecurityRoleButtonR find(Serializable id)  {
        return getMapper().selectById(SecurityRoleButtonR.class,id);
    }

    @Override
    public List<String> getButtonByUserId(String userId)  {
        List<SecurityButtonT> list = getMapper().getButtonByUserId(userId);
        List<String> codeList = new ArrayList<String>();
        if (list.size()>0){
            StringBuffer buttonCode = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                codeList.add(list.get(i).getButtonCode());
            }

            return codeList;
        }else{
            return codeList;
        }


    }
}
