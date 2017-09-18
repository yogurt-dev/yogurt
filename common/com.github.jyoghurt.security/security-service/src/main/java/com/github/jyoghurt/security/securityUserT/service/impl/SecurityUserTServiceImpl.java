package com.github.jyoghurt.security.securityUserT.service.impl;

import com.github.jyoghurt.msgcen.common.utils.MsgSendUtil;
import com.github.jyoghurt.msgcen.exception.MsgException;
import com.github.jyoghurt.security.enums.Module;
import com.github.jyoghurt.security.exception.SecurityException;
import com.github.jyoghurt.security.securityMenuT.domain.SecurityMenuT;
import com.github.jyoghurt.security.securityMenuT.service.SecurityMenuTService;
import com.github.jyoghurt.security.securityRoleT.domain.SecurityRoleT;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUnitT.service.SecurityUnitTService;
import com.github.jyoghurt.security.securityUserResourceR.domain.SecurityUserResourceR;
import com.github.jyoghurt.security.securityUserResourceR.service.SecurityUserResourceService;
import com.github.jyoghurt.security.securityUserRoleR.domain.SecurityUserRoleR;
import com.github.jyoghurt.security.securityUserRoleR.service.SecurityUserRoleRService;
import com.github.jyoghurt.security.securityUserT.dao.SecurityUserTMapper;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.security.securityUserT.enums.ResourceType;
import com.github.jyoghurt.security.securityUserT.enums.RoleType;
import com.github.jyoghurt.security.securityUserT.handler.WechatHandler;
import com.github.jyoghurt.security.securityUserT.service.SecurityUserTService;
import com.github.jyoghurt.security.securityUserUnitR.domain.SecurityUserUnitR;
import com.github.jyoghurt.security.securityUserUnitR.service.SecurityUserUnitService;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.handle.OperatorHandle;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.handle.SQLJoinHandle;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pub.utils.Encrypter;
import pub.utils.SecurityCustomUtils;
import pub.utils.SessionUtils;
import pub.utils.SysVarEnum;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author baoxb baoxiaobing@lvyushequ.com
 * @version V1.0
 * @Title: 用户管理服务层
 * @Package com.df.security.securityUserT.service;
 * @Description: 为前台提供与用户相关的服务
 * @date 2015-9-29
 */


@Service("securityUserTService")
public class SecurityUserTServiceImpl extends ServiceSupport<SecurityUserT, SecurityUserTMapper> implements SecurityUserTService {

    @Autowired
    private SecurityUserTMapper securityUserTMapper;

    @Resource
    private SecurityUserRoleRService securityUserRoleRService;

    @Resource
    private SecurityMenuTService securityMenuTService;

    @Resource
    private SecurityUserResourceService securityUserResourceService;

    @Resource
    private SecurityUserUnitService securityUserUnitService;

    @Resource
    private SecurityUnitTService securityUnitTService;

    @Override
    public SecurityUserTMapper getMapper() {
        return securityUserTMapper;
    }

    @Override
    public void delete(Serializable id) {
        //同步企业邮箱
        SecurityCustomUtils.syncTencentUser(new SecurityUserT().setUserId(id.toString()),SysVarEnum.DELETE_OPPERTYPE);
        getMapper().delete(SecurityUserT.class, id);
    }


    @Override
    public SecurityUserT find(Serializable id) {
        return getMapper().selectById(SecurityUserT.class, id);
    }

    @Override
    @Cacheable(value = "security", key = "#root.targetClass + #root.methodName+#userId")
    public SecurityUserT queryUserById(String userId) {
        SecurityUserT user = this.find(userId);
        if (null == user) {
            return null;
        }
        user.setRoles(this.queryUserRolesByUserId(userId));
        user.setSecurityUserResourceRs(securityUserResourceService.findAll(new SecurityUserResourceR().setUserId(userId)));
        user.setSecurityUserUnitRs(securityUserUnitService.findAll(new SecurityUserUnitR().setUserIdR(userId)));
        return user;
    }

    @Override
    public SecurityUserT queryUserByUserAccount(String userAccount) {
        List<SecurityUserT> users = this.findAll(new SecurityUserT().setUserAccount(userAccount));
        if (null == users || 0 == users.size()) {
            return null;
        }
        SecurityUserT user = users.get(0);
        user.setRoles(this.queryUserRolesByUserId(user.getUserId()));
        user.setSecurityUserResourceRs(securityUserResourceService.findAll(new SecurityUserResourceR().setUserId(user.getUserId())));
        return user;
    }




    @Override
    public List<SecurityUnitT> findUnitListAll(SecurityUserT loginUser, String userId) {
        List<SecurityUnitT> unitVs = new ArrayList();
        //如果是用户级
        if (SysVarEnum.USER_TYPE_USER.getCode().equals(loginUser.getType())) {
            SecurityUnitT unit = new SecurityUnitT();
            unit.setUnitId(loginUser.getBelongOrg());
            unit.setUnitName(loginUser.getBelongOrgName());
            unitVs.add(0, unit);
        } else {
            unitVs = getMapper().findUnitListAll(userId);
        }
        return unitVs;
    }

    @Override
    public int queryUserByAccount(String account) {
        return getMapper().queryUserByAccount(account);
    }


    public void editUser(SecurityUserT securityUserT) {
        if (null != securityUserT) {
            this.updateForSelective(securityUserT);
        }
    }

    @Override
    public void editUser(SecurityUserT securityUserT, String[] roleId, String unitId) {// TODO: 2016/8/4
        //更新数据，只更新前台填写的非空字段
        securityUserT.setBelongOrg(unitId);
        securityUserT.setType(SysVarEnum.USER_TYPE_USER.getCode());
        this.editUser(securityUserT);
        //删除当前用户与角色的所有关系
        securityUserRoleRService.deleteRelByUserId(securityUserT.getUserId());
        //添加当前用户与角色修改后的关系
        for (int i = 0; i < roleId.length; i++) {
            SecurityUserRoleR userRoleR = new SecurityUserRoleR();
            userRoleR.setUserId(securityUserT.getUserId());
            userRoleR.setRoleId(roleId[i]);
            securityUserRoleRService.save(userRoleR);
        }
    }

//    @Override
//    public List<SecurityUserT> queryContentUserByCompId(String compId) {
//        return getMapper().queryContentUserByCompId(compId);
//    }

    @Override
    public List<SecurityRoleT> queryUserRolesByUserId(String userId) {
        return getMapper().queryUserRolesByUserId(userId);
    }

    @Override
    public void updateNameAndLinkWay(String userId, String name, String linkWay, String emailAddr) {
        SecurityUserT securityUserT = this.find(userId);
        securityUserT.setUserName(name);
        securityUserT.setLinkWay(linkWay);
        securityUserT.setEmailAddr(emailAddr);
        this.editUser(securityUserT);
    }

    @Override
    public void updateAccountInfo(String userId, String userAccount, String passwd) {
        SecurityUserT securityUserT = this.find(userId);
        securityUserT.setUserAccount(userAccount);
        securityUserT.setPasswd(passwd);
        this.editUser(securityUserT);
    }

    @Override
    public boolean isSysRole(String userId, String roleType) {
        List<SecurityUserT> userTs = getMapper().isSysRole(userId, roleType);
        if (userTs.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public List<SecurityMenuT> consoleMenu(List<SecurityMenuT> securityMenuTs, SecurityUserT securityUserT) {

        SecurityUnitT securityUnitT = securityUserT.getBelongCompany();

        String appId = securityUnitT.getAppId();
        String appIdF = securityUnitT.getAppIdF();
        //取超级管理员获得第一个
        SecurityUserT userT = this.find("7d6007e5-9659-441d-be00-5us484f8856d");

        for (int i = 0; i < securityMenuTs.size(); i++) {
            securityMenuTs.get(i).setMenuType(SysVarEnum.NORMAL_MENUTYPE.getMessage());
            //如果是订阅号
            if ((securityMenuTs.get(i).getMenuUrl()).contains("TYPE_SUBSCRIPTION")) {
                if (StringUtils.isEmpty(appId)) {
                    securityMenuTs.get(i).setMenuType(SysVarEnum.UNNORMAL_MENUTYPE.getMessage());
                    securityMenuTs.get(i).setMenuUrl("订阅号尚未配置，请联系驴鱼科技:" + userT.getLinkWay());
                }
            }
            //如果是服务号
            if ((securityMenuTs.get(i).getMenuUrl()).contains("TYPE_SERVICE")) {
                if (StringUtils.isEmpty(appIdF)) {
                    securityMenuTs.get(i).setMenuType(SysVarEnum.UNNORMAL_MENUTYPE.getMessage());
                    securityMenuTs.get(i).setMenuUrl("服务号尚未配置，请联系驴鱼科技:" + userT.getLinkWay());
                }
            }
        }

        return securityMenuTs;
    }

    @Override
    public List<SecurityUserT> queryUserByRole(String belongUnit, String roleId) {
        SecurityUserT condition = new SecurityUserT();
        if (StringUtils.isEmpty(belongUnit) && StringUtils.isEmpty(roleId)) {
            throw new BaseErrorException("传入参数无效，roleId和unitId不能同时为空");
        }

        //如果只传了单位ID，那么查询该单位下人员（不递归，直属该单位下的人员@老吴）
        if (!StringUtils.isEmpty(belongUnit) && StringUtils.isEmpty(roleId)) {
            condition.setBelongOrg(belongUnit);
            return findAll(condition);
        }

        //如果只传了角色ID，那么查询该角色下的所有人
        if (StringUtils.isEmpty(belongUnit) && !StringUtils.isEmpty(roleId)) {
            return findAll(new SecurityUserT(), new QueryHandle().addSqlJoinHandle(null, SQLJoinHandle.JoinType.JOIN,
                    "SecurityUserRoleR ur on t.userId = ur.userId and ur.roleId='".concat(roleId).concat("'")));
        }

        //如果即传了单位ID，又传了角色ID（查询该单位下直属人，同时角色为该角色的@老吴）
        condition.setBelongOrg(belongUnit);
        return findAll(condition, new QueryHandle().addSqlJoinHandle(null, SQLJoinHandle.JoinType.JOIN,
                "SecurityUserRoleR ur on t.userId = ur.userId".concat(" and ur.roleId = '").concat(roleId).concat("'")));
    }

    @Override
    public boolean checkUserByRoleResource(String userId, String roleId, String resourceId, Module module, String resourceType) {
        if (org.apache.commons.lang.StringUtils.isEmpty(userId) || org.apache.commons.lang.StringUtils.isEmpty(resourceId) || null == module || org.apache.commons.lang.StringUtils.isEmpty(resourceType)) {
            logger.error("判断某用户的某角色，是否具备资源出错，错误：参数异常！");
            throw new BaseErrorException("传入参数不合法");
        }

        List<SecurityUserT> securityUserTs = this.findAll(new SecurityUserT(), new QueryHandle()
                .addJoinHandle(null, SQLJoinHandle.JoinType.JOIN, "SecurityUserRoleR ur on t.userId = ur.userId")
                .addJoinHandle(null, SQLJoinHandle.JoinType.JOIN, "SecurityUserResourceR sur on t.userId = sur.userId")
                .addWhereSql("t.userId = #{data.userId} and ur.roleId = #{data.roleId} and sur.resourceId = #{data" +
                        ".resourceId} and sur.belongModel=#{data.module} and sur.resourceType=#{data.resourceType}")
                .addExpandData("userId", userId).addExpandData("roleId", roleId)
                .addExpandData("resourceId", resourceId).addExpandData("module", module.getCode()).addExpandData("resourceType", resourceType));
        if (CollectionUtils.isNotEmpty(securityUserTs)) {
            return true;
        }
        return false;
    }

    @Override
    @CacheEvict(value = "security", key = "#root.targetClass + 'queryUserById'+#userId")
    public void updateAccountInfoAndTencentSync(String userId, String userAccount, String password) {
        SecurityUserT securityUserT = this.find(userId);
        securityUserT.setUserAccount(userAccount);
        securityUserT.setPasswd(password);
        SecurityCustomUtils.syncTencentUser(securityUserT,SysVarEnum.MODIFY_OPPERTYPE);
        this.updateAccountInfo(userId, userAccount, Encrypter
                .MD5(password));
    }

    @Override
    public JSONArray queryUserByRole(RoleType roleType, ResourceType resourceType) {
        List<SecurityUserT> securityUserTs = this.findAll(new SecurityUserT(), new QueryHandle().addJoinHandle(null,
                SQLJoinHandle.JoinType.JOIN, "SecurityUserRoleR ur on t.userId = ur.userId and ur.roleId = " +
                        "'" + roleType.getStateCode() + "'")
                .addJoinHandle("urr.resourceId belongResources,urr.resourceName belongResourcesName", SQLJoinHandle.JoinType.LEFT_OUTER_JOIN,
                        "SecurityUserResourceR urr on t" +
                                ".userId = urr.userId and urr.resourceType = '" + resourceType.getStateCode() + "'"));

        //增加员工所属门店
        JSONArray result = new JSONArray();
        for (SecurityUserT securityUserT : securityUserTs) {
            JSONObject user = new JSONObject();
            user.put("userId", securityUserT.getUserId());
            user.put("userName", securityUserT.getUserName());
            user.put("roles", securityUserT.getRoles() == null ? "" : securityUserT.getRoles());
            user.put("portrait", securityUserT.getPortrait() == null ? "" : securityUserT.getPortrait());
            user.put("belongResource", securityUserT.getBelongResources() == null ? "" : securityUserT.getBelongResources());
            user.put("belongResourcesName", securityUserT.getBelongResourcesName() == null ? "" : securityUserT
                    .getBelongResourcesName());
            result.add(user);
        }

        return result;
    }

    @Override
    @CacheEvict(value = "security", key = "#root.targetClass + 'queryUserById'+#securityUserT.userId")
    public SecurityUserT editXD(SecurityUserT securityUserT) {
        //同步企业邮箱
        SecurityCustomUtils.syncTencentUser(securityUserT,SysVarEnum.MODIFY_OPPERTYPE);

        //处理用户资源关系
        List<SecurityUserResourceR> securityUserResourceRs = securityUserT.getSecurityUserResourceRs();
        //删除原有关系数据
        securityUserResourceService.deleteByCondition(new SecurityUserResourceR().setUserId(securityUserT.getUserId()));
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(securityUserResourceRs)) {
            for (SecurityUserResourceR securityUserResourceR : securityUserResourceRs) {
                securityUserResourceR.setUserId(securityUserT.getUserId());
            }
            for (SecurityUserResourceR securityUserResourceR : securityUserResourceRs) {
                if (null == securityUserResourceR.getBelongModel()) {
                    securityUserResourceR.setBelongModel("");
                }
            }
            securityUserResourceService.saveBatch(securityUserResourceRs);
        }

        //处理用户管理单位关系
        List<SecurityUserUnitR> securityUserUnitRs = securityUserT.getSecurityUserUnitRs();
        securityUserUnitService.deleteByCondition(new SecurityUserUnitR().setUserIdR(securityUserT.getUserId()));//删除关系

        if (CollectionUtils.isNotEmpty(securityUserUnitRs)) {
            securityUserUnitService.saveBatch(securityUserUnitRs);
        }
        this.updateNameAndLinkWay(securityUserT.getUserId(), securityUserT.getUserName(), securityUserT
                .getLinkWay(), securityUserT.getEmailAddr());
        return securityUserT;
    }

    @Override
    public List<SecurityUserResourceR> queryUserResourceByUser(SecurityUserT securityUserT, ResourceType resourceType) {
        QueryHandle queryHandle = new QueryHandle();
        String sql = "userId = #{data.userId}";
        if(resourceType!=ResourceType.ALL){
            sql = org.apache.commons.lang3.StringUtils.join(sql," and resourceType = #{data.resourceType}");
            queryHandle.addExpandData("resourceType",resourceType.getStateCode());
        }
        queryHandle.addWhereSql(sql);
        queryHandle.addExpandData("userId",securityUserT.getUserId());
        return securityUserResourceService.findAll(new SecurityUserResourceR().setDeleteFlag(false),queryHandle);
    }

    @Override
    public List<SecurityUserResourceR> queryUserResourceByCurrentLoginUser(ResourceType resourceType) {
        SecurityUserT currentLoginUser = SessionUtils.getManager();
        return queryUserResourceByUser(currentLoginUser,resourceType);
    }

    @Override
    public SecurityUserT addXD(SecurityUserT securityUserT) {
        SecurityCustomUtils.syncTencentUser(securityUserT,SysVarEnum.ADD_OPPERTYPE);


        this.save(securityUserT);

        List<SecurityUserResourceR> securityUserResourceRs = securityUserT.getSecurityUserResourceRs();
        for (SecurityUserResourceR securityUserResourceR : securityUserResourceRs) {
            securityUserResourceR.setUserId(securityUserT.getUserId());
        }
        List<SecurityUserUnitR> securityUserUnitRList = securityUserT.getSecurityUserUnitRs();
        for (SecurityUserUnitR securityUserUnitR : securityUserUnitRList) {
            securityUserUnitR.setUserIdR(securityUserT.getUserId());
        }
        //保存用户资源关系
        securityUserResourceService.saveBatch(securityUserResourceRs);
        securityUserUnitService.saveBatch(securityUserUnitRList);
        return securityUserT;
    }


    @Override
    public void generateUserLoginVerification(String userAccount) throws SecurityException {
        //查询用户是否存在
        List<SecurityUserT> loginUser = this.findAll(new SecurityUserT(),new QueryHandle().addWhereSql("t" +
                ".userAccount = #{data.userAccount} or t.emailAddr = #{data.userAccount}")
                .addExpandData
                ("userAccount",userAccount));

        if(CollectionUtils.isEmpty(loginUser)){
            throw new SecurityException(SecurityException.ERROR_90801);
        }

        SecurityUserT securityUserT =  loginUser.get(0);
        //生成用户登录校验码
        String verificationCode = String.valueOf(Math.random()).substring(2, 8);

        //发送邮件
        if(org.apache.commons.lang.StringUtils.isEmpty(securityUserT.getEmailAddr())){
            throw new SecurityException(SecurityException.ERROR_90802);
        }
//        UserLoginEmailTmpl

        try {
            JSONObject param = new JSONObject();
            param.put("verificationCode", verificationCode);

            MsgSendUtil.send(securityUserT, "USER_LOGIN_VERIFICATION_TRIGGER", param);
        } catch (MsgException e) {
            e.printStackTrace();
        }

        //更新用户的登录校验码字段
        this.updateForSelective(securityUserT.setLoginVerification(verificationCode));
    }

    public List<String> filterUserByResourceId(List<String> userIds, String resourceId) {
        QueryHandle queryHandle = new QueryHandle();
        List<SecurityUserResourceR> resources = securityUserResourceService.findAll(new SecurityUserResourceR()
                .setResourceId(resourceId), queryHandle.addOperatorHandle("userId", OperatorHandle.operatorType.IN,
                userIds.toArray()));
        List<String> haveResourceUserIds = new ArrayList<>();
        for (SecurityUserResourceR resource : resources) {
            haveResourceUserIds.add(resource.getUserId());
        }
        return haveResourceUserIds;
    }

    @Override
    public List<String> queryUserByResourceId(String resourceId) {
        List<SecurityUserResourceR> resources = securityUserResourceService.findAll(new SecurityUserResourceR()
                .setResourceId(resourceId));
        List<String> userIds = new ArrayList<>();
        for (SecurityUserResourceR resource : resources) {
            userIds.add(resource.getUserId());
        }
        return userIds;
    }

    @Override
    public List<SecurityUserT> queryUserByButtonCode(String buttonCode) {
        return getMapper().queryUserByButtonCode(buttonCode);
    }

    @Override
    @CacheEvict(value = "security", allEntries = true)
    public void cacheEvict() {}

    @Override
    public void cleanVerification() {
        this.getMapper().cleanVerification();
    }

    @Override
    public void convertRealUnitName(List<SecurityUserT> securityUserTs) {
        List<SecurityUnitT> securityUnitTs = securityUnitTService.findAll(new SecurityUnitT());
        for(SecurityUserT securityUserT:securityUserTs){
            for(SecurityUnitT securityUnitT :securityUnitTs){
                if(securityUnitT.getUnitId().equals(securityUserT.getBelongOrg())){
                    securityUserT.setBelongOrgName(securityUnitT.getUnitName());
                }
            }
        }
    }

    @Override
    public void boundWechat(String code, String state) throws SecurityException {
        //获取微信开放平台得access_token
        JSONObject accesstoken = WechatHandler.fetchAccesstoken(code,state);
        SecurityUserT securityUserT = SessionUtils.getManager();
        if(null==securityUserT){
            throw new SecurityException(SecurityException.ERROR_90804);
        }

        securityUserT.setOpen_id((String) accesstoken.get("openid"));

        //查找当前微信用户是否与系统账号绑定
        List<SecurityUserT> hasBoundOpenIdPersonList = this.findAll(new SecurityUserT().setDeleteFlag(false).setOpen_id(
                (String) accesstoken.get("openid")));
        //解除绑定
        if(CollectionUtils.isNotEmpty(hasBoundOpenIdPersonList)){
            SecurityUserT hasBoundOpenIdPerson = hasBoundOpenIdPersonList.get(0);
            hasBoundOpenIdPerson.setOpen_id("");
            this.update(hasBoundOpenIdPerson);
        }
        //绑定账号
        this.updateForSelective(securityUserT);
    }

    @Override
    public void wechatLogin(String code, String state) throws SecurityException {
        JSONObject data = WechatHandler.fetchAccesstoken(code,state);
        String openId = (String)data.get("openid");
        List<SecurityUserT> securityUserTs = this.findAll(new SecurityUserT().setOpen_id(openId).setDeleteFlag(false));
        if(CollectionUtils.isEmpty(securityUserTs)){
            throw new SecurityException(SecurityException.ERROR_90803);
        }
        putUserInfoToMemery(securityUserTs);
    }




    @Override
    public JSONObject login(String userAccount, String passwd, String verificationCode,String devModel) throws
            SecurityException {
        List<SecurityUserT> user;
        JSONObject result = new JSONObject();

        if("true".equals(devModel)){
            user = this.findAll(new SecurityUserT(), new QueryHandle().addWhereSql
                    ("(t.userAccount = #{data.userAccount} or t.emailAddr=#{data.userAccount}) and t.passwd = '" + Encrypter
                            .MD5(passwd) + "'").addExpandData("userAccount",
                    userAccount));
        }else{
            user = this.findAll(new SecurityUserT(), new QueryHandle().addWhereSql
                    ("(t.userAccount = #{data.userAccount} or t.emailAddr=#{data.userAccount}) and t.passwd = '" + Encrypter
                            .MD5(passwd) + "' and t.loginVerification = #{data.loginVerification}").addExpandData("userAccount",
                    userAccount).addExpandData("loginVerification",verificationCode));
        }
        if(CollectionUtils.isEmpty(user)){
            throw new SecurityException(SecurityException.ERROR_90801);
        }

        List<String> storeIds = securityUserResourceService.fetchResourcesStr(user.get(0).getUserId(),"store");
        result.put("storeIds",storeIds);
        putUserInfoToMemery(user);
        return result;
    }

    private void putUserInfoToMemery(List<SecurityUserT> securityUserTs){
        if (CollectionUtils.isNotEmpty(securityUserTs)) {
            //记录session//
            SecurityUserT currentUser = securityUserTs.get(0);
            this.updateForSelective(currentUser);
            SecurityUnitT securityUnitT = securityUnitTService.find(currentUser.getBelongOrg());

            SecurityUnitT fatherCompany = new SecurityUnitT();

            if (StringUtils.isEmpty(securityUnitT.getCompType())) {
                fatherCompany = securityUnitTService.find("-1");
            } else {
                fatherCompany = securityUnitTService.getFathCompanyInfo(securityUserTs.get(0)
                        .getBelongOrg());
            }


            securityUserTs.get(0).setBelongCompany(fatherCompany);
            SessionUtils.setManager(securityUserTs.get(0));
            //查询用户并记录
            userMenuQuery();
        }
    }

    /**
     * 根据用户ID获取所属用户列表，并存储到session中
     *
     * @
     */
    private void userMenuQuery()   {
        //根据用户ID查询用户所属用户集合

        List<SecurityMenuT> menues = securityMenuTService.findAll(new SecurityMenuT(), new QueryHandle()
                .addDistinct()
                .addSqlJoinHandle(null, SQLJoinHandle.JoinType.INNER_JOIN, "SecurityMenuRoleR mr on t.menuId = mr.menuId")
                .addSqlJoinHandle(null, SQLJoinHandle.JoinType.INNER_JOIN, "SecurityRoleT r on mr.roleId = r.roleId")
                .addSqlJoinHandle(null, SQLJoinHandle.JoinType.INNER_JOIN, org.apache.commons.lang3.StringUtils.join
                        ("SecurityUserRoleR ur on r.roleId = ur.roleId and ur.userId = '", SessionUtils
                                .getManager().getUserId(), "'")).addOrderBy("t.sortId", "asc")
        );
        //保存菜单信息到session中

        SessionUtils.setAttr(SessionUtils.SESSION_MENU, this.consoleMenu(menues, SessionUtils.getManager()));
    }


    @Override
    public boolean checkUserExist(String userAccount, String password) {
        List<SecurityUserT> securityUserTS = this.findAll(new SecurityUserT().setUserAccount(userAccount).setPasswd
                (Encrypter.MD5(password)).setDeleteFlag(false));

        if(securityUserTS.size()>0){
            return true;
        }
        return false;
    }

    @Override
    public List<SecurityUserT> queryUsersByRoleIdAndResourceId(String roleId, String resourceId) {
        List<SecurityUserT> result = this.findAll(new SecurityUserT().setDeleteFlag(false),new QueryHandle().addDistinct()
                .addJoinHandle(null, SQLJoinHandle.JoinType.INNER_JOIN, "SecurityUserResourceR us on t.userId = us.userId")
                .addJoinHandle(null, SQLJoinHandle.JoinType.INNER_JOIN, "SecurityUserRoleR ur on t.userId = ur.userId")
                .addWhereSql("ur.roleId = #{data.roleId} and us.resourceId = #{data.resourceId} and ur.deleteFlag " +
                        "= false and us.deleteFlag = false").addExpandData("roleId",roleId).addExpandData("resourceId", resourceId));
        return result;
    }

    @Override
    public boolean checkUserPasswordCorrect(String userId, String password) {
        long count = this.count(new SecurityUserT().setDeleteFlag(false).setUserId(userId).setPasswd(Encrypter.MD5
                (password)),new QueryHandle());
        if(count>0){
            return true;
        }
        return false;
    }

    @Override
    public SecurityUserT checkUserIdAndPasswordCorrect(String userId, String password) throws SecurityException {
        SecurityUserT securityUserT = find(userId);
        if (securityUserT == null) {
            throw new SecurityException(SecurityException.ERROR_90806);
        }
        if (!securityUserT.getPasswd().equals(Encrypter.MD5(password))) {
            throw new SecurityException(SecurityException.ERROR_90806);
        }
        return securityUserT;
    }
}
