package com.github.jyoghurt.qqEmail.service.impl;

import com.github.jyoghurt.qqEmail.common.util.QQEmailUtil;
import com.github.jyoghurt.qqEmail.dao.EmailVersionTMapper;
import com.github.jyoghurt.qqEmail.domain.EmailVersionT;
import com.github.jyoghurt.qqEmail.domain.UserInfoVo;
import com.github.jyoghurt.qqEmail.domain.UserUpdateListVo;
import com.github.jyoghurt.qqEmail.service.EmailVersionTService;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Service("emailVersionTService")
public class EmailVersionTServiceImpl extends ServiceSupport<EmailVersionT, EmailVersionTMapper> implements EmailVersionTService {
    @Autowired
    private EmailVersionTMapper emailVersionTMapper;

    @Override
    public EmailVersionTMapper getMapper() {
        return emailVersionTMapper;
    }

    @Override
    public void delete(Serializable id)  {
        getMapper().delete(EmailVersionT.class, id);
    }

    @Override
    public EmailVersionT find(Serializable id)  {
        return getMapper().selectById(EmailVersionT.class, id);
    }

    @Override
    public List<UserInfoVo> syncEmailUser(String clientId, String clientSecret)   {
        /*获取token*/
        String token =  QQEmailUtil.getToken(clientId, clientSecret).get("access_token").toString();
        /*获取当前版本号*/
        EmailVersionT versionT = getMapper().findCurrentVersion(clientId);
         /*若不存在当前版本，则版本号取初始版本*/
        String currentVersion = versionT == null ? "0" : versionT.getCurrentVersion();
        /*根据版本号获取当前版本用户变更记录*/
        UserUpdateListVo userUpdateListVo = QQEmailUtil.getUserListByVer(currentVersion, token);
        if (userUpdateListVo.getVer() == null) {
            throw new BaseErrorException("根据版本号获取当前版本用户变更记录失败！");
        }
        List<UserInfoVo> returnList = QQEmailUtil.getUserInfoByAlias(userUpdateListVo.getList(), token);
        /*若调用接口均成功保存版本号*/
        EmailVersionT newVerson = new EmailVersionT();
        /*保存新版本号*/
        newVerson.setCurrentVersion(userUpdateListVo.getVer());
        /*保存上个版本号*/
        newVerson.setLastVersion(currentVersion);
        newVerson.setClientId(clientId);
        newVerson.setCreateDateTime(new Date());
      //  getMapper().save(newVerson);
        return returnList;
    }

}
