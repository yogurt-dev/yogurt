package com.github.jyoghurt.security.securityDataDic.service;

import com.github.jyoghurt.core.service.BaseService;
import com.github.jyoghurt.security.securityDataDic.domain.SecurityDataDic;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;

import java.util.List;

/**
 * 字典管理服务层
 *
 */
public interface SecurityDataDicService extends BaseService<SecurityDataDic> {

    /**
     * 根据用户查询字典信息，如果用户为超级管理员，那么返回所有字典，如果是普通用户那么返回当前用户所属字典
     * @param userT 用户实体
     * @param dicName 字典名称
     * @return

     */
    public List<SecurityDataDic> queryDicByUser(SecurityUserT userT, String dicName) ;

    /**
     * 查询 4s店业务所在地 字典
     * @return

     */
    public List<SecurityDataDic> queryBusinessAreaDic() ;

}
