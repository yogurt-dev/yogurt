package com.github.jyoghurt.security.securityDataDic.service.impl;

import com.github.jyoghurt.security.securityDataDic.dao.SecurityDataDicMapper;
import com.github.jyoghurt.security.securityDataDic.domain.SecurityDataDic;
import com.github.jyoghurt.security.securityDataDic.service.SecurityDataDicService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service("securityDataDicService")
public class SecurityDataDicServiceImpl extends ServiceSupport<SecurityDataDic, SecurityDataDicMapper> implements SecurityDataDicService {
    @Autowired
    private SecurityDataDicMapper securityDataDicMapper;

    @Override
    public SecurityDataDicMapper getMapper() {
        return securityDataDicMapper;
    }

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(SecurityDataDic.class, id);
    }

    @Override
    public SecurityDataDic find(Serializable id)  {
        return getMapper().selectById(SecurityDataDic.class,id);
    }

    @Override
    public List<SecurityDataDic> queryDicByUser(SecurityUserT userT, String dicName)  {

//        if(SysVarEnum.USER_TYPE_MANAGER.getCode().equals(userT.getType())){//如果当前用户为超级管理员
            return findAll(new SecurityDataDic(),new QueryHandle().addWhereSql(StringUtils.join("t.dicName = '",
                    dicName,"'")));
//        }else{//如果当前用户为普通用户
//            return findAll(new SecurityDataDic(),new QueryHandle()
//                    .addSqlJoinHandle(null, SQLJoinHandle.JoinType.LEFT_OUTER_JOIN, "SecurityUnitT u on t.key = " +
//                            "u.type " +
//                            "JOIN SecurityUserT us on u.unitId = us.belongOrg and us.userId = " +
//                            "'" + userT.getUserId() + "'"));
//        }
    }

    @Override
    public List<SecurityDataDic> queryBusinessAreaDic()  {
        return getMapper().queryBusinessAreaDic();
    }
}
