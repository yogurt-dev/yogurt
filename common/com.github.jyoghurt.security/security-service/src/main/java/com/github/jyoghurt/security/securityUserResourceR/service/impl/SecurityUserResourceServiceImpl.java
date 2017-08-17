package com.github.jyoghurt.security.securityUserResourceR.service.impl;

import com.github.jyoghurt.security.enums.Module;
import com.github.jyoghurt.security.securityUserResourceR.dao.SecurityUserResourceMapper;
import com.github.jyoghurt.security.securityUserResourceR.domain.SecurityUserResourceR;
import com.github.jyoghurt.security.securityUserResourceR.service.SecurityUserResourceService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.security.securityUserT.service.SecurityUserTService;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.handle.SQLJoinHandle;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service("securityUserResourceService")
public class SecurityUserResourceServiceImpl extends ServiceSupport<SecurityUserResourceR, SecurityUserResourceMapper> implements SecurityUserResourceService {
    private final String value = "d12a54e629fa4358be6dae6ab016ca0f";
    @Autowired
    private SecurityUserResourceMapper securityUserResourceMapper;

    @Autowired
    private SecurityUserTService securityUserTService;

    @Override
    public SecurityUserResourceMapper getMapper() {
        return securityUserResourceMapper;
    }

    @Override
    public void logicDelete(Serializable id) {
        getMapper().logicDelete(SecurityUserResourceR.class, id);
    }

    @Override
    public SecurityUserResourceR find(Serializable id) {
        return getMapper().selectById(SecurityUserResourceR.class, id);
    }


    @Override
    public List<SecurityUserResourceR> fetchResourcesObj(String userId, Module module, String type) {
        return this.findAll(new SecurityUserResourceR().setUserId(userId).setBelongModel(module.getCode()).setResourceType(type));
    }

    @Override
    public List<String> fetchResourcesStr(String userId, Module module, String type) {
        List<SecurityUserResourceR> securityUserResourceRs = this.findAll(new SecurityUserResourceR().setUserId(userId).setBelongModel(module.getCode()).setResourceType(type));
        List<String> ids = new ArrayList();
        for (SecurityUserResourceR securityUserResourceR : securityUserResourceRs) {
            ids.add(securityUserResourceR.getResourceId());
        }
        return ids;
    }

    @Override
    public List<String> fetchResourcesStr(String userId, String type) {
        List<SecurityUserResourceR> securityUserResourceRs = this.findAll(new SecurityUserResourceR().setUserId(userId).setResourceType(type));
        List<String> ids = new ArrayList();
        for (SecurityUserResourceR securityUserResourceR : securityUserResourceRs) {
            ids.add(securityUserResourceR.getResourceId());
        }
        return ids;
    }

    @Override
    public List<String> fetchResourcesStr(String userId, String... types){
        List<String> ids =new ArrayList<>();
        for(String type:types){
            ids.addAll(fetchResourcesStr(userId,type));
        }
        return ids;
    }

    @Override
    public List<SecurityUserT> queryUsersBelongToResource(String resourceId, String roleId){
        List<SecurityUserT> securityUserTS = securityUserTService.findAll(new SecurityUserT().setDeleteFlag(false), new QueryHandle()
                .addJoinHandle(null, SQLJoinHandle.JoinType.INNER_JOIN, "SecurityUserResourceR ur on t.userId = ur.userId")
                .addJoinHandle(null, SQLJoinHandle.JoinType.INNER_JOIN, "SecurityUserRoleR uu on t.userId = uu.userId")
                .addWhereSql("ur.resourceId = #{data.resourceId} and uu.roleId = #{data.roleId}")
                .addExpandData("resourceId", resourceId)
                .addExpandData("roleId", roleId));
        return securityUserTS;
    }


}
