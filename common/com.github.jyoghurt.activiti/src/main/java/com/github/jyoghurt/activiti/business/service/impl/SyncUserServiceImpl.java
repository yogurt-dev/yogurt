package com.github.jyoghurt.activiti.business.service.impl;

import com.github.jyoghurt.activiti.business.service.SyncUserService;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.utils.SecurityPluginUtil;
import pub.utils.SysVarEnum;

/**
 * Created by dell on 2016/1/11.
 */
@Service("syncUserService")
public class SyncUserServiceImpl extends SecurityPluginUtil implements SyncUserService {
    @Autowired
    private ProcessEngine processEngine;

    @Override
    public String syncUser(SecurityUserT securityUserT, SecurityUnitT securityUnit, String clientId, String clientSecret, SysVarEnum sysVarEnum)   {
        User user = processEngine.getIdentityService().createUserQuery().userId(securityUserT.getUserAccount()).singleResult();
        if (SysVarEnum.MODIFY_OPPERTYPE.equals(sysVarEnum)) {
            if (user == null) {
                user = processEngine.getIdentityService().newUser(securityUserT.getUserAccount());
            }
            user.setEmail(securityUserT.getEmailAddr());
            user.setId(securityUserT.getUserAccount());
            user.setFirstName(securityUserT.getUserAccount());
            processEngine.getIdentityService().saveUser(user);
        }
        if(SysVarEnum.DELETE_OPPERTYPE.equals(sysVarEnum)){
            if(user!=null){
                processEngine.getIdentityService().deleteUser(securityUserT.getUserId());
            }
        }
        return null;
    }
}
