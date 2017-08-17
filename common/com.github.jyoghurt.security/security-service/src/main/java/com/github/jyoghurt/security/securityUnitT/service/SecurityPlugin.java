package com.github.jyoghurt.security.securityUnitT.service;

import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import pub.utils.SysVarEnum;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.security.securityUnitT.service
 * @Description:
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2015-11-18 13:58
 */
public interface SecurityPlugin {

    /**
     * 同步用户信息
     *
     * @param securityUserT
     * @param securityUnit
     * @param clientId
     * @param clientSecret
     * @param action
     * @return

     * @
     */
    public String syncUser(SecurityUserT securityUserT, SecurityUnitT securityUnit, String clientId, String
            clientSecret, SysVarEnum action)  ;


    /**
     * 同步组织机构信息
     *
     * @param securityUnitT
     * @param clientId
     * @param clientSecret
     * @param action
     * @return

     * @
     */
    public String syncUnit(SecurityUnitT securityUnitT, String clientId, String clientSecret, SysVarEnum action)  ;


    /**
     * 工作流同步接口
     * @param securityUserT
     * @param operType

     */
    public void syncHumFlow(SecurityUserT securityUserT,SysVarEnum operType) ;


}
