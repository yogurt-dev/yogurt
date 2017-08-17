package com.github.jyoghurt.security.securityUserT.service;

import com.github.jyoghurt.security.enums.Module;
import com.github.jyoghurt.security.exception.SecurityException;
import com.github.jyoghurt.security.securityMenuT.domain.SecurityMenuT;
import com.github.jyoghurt.security.securityRoleT.domain.SecurityRoleT;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUserResourceR.domain.SecurityUserResourceR;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.security.securityUserT.enums.ResourceType;
import com.github.jyoghurt.security.securityUserT.enums.RoleType;
import com.github.jyoghurt.core.service.BaseService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * @author baoxb baoxiaobing@lvyushequ.com
 * @version V1.0
 * @Title: 用户管理服务层
 * @Package com.df.security.securityUserT.service;
 * @Description: 为前台提供与用户相关的服务
 * @date 2015-9-29
 */
public interface SecurityUserTService extends BaseService<SecurityUserT> {


    SecurityUserT queryUserById(String userId);

    JSONObject login(String userAccount, String passwd, String verificationCode, String devModel) throws SecurityException;

    SecurityUserT queryUserByUserAccount(String userAccount);
//    /**
//     * 查询所有用户信息
//     *
//     * @return
//
//     */
//    public QueryResult<SecurityUserT> findUserListAll(SecurityUserT securityUserT, QueryHandle queryHandle) ;

    /**
     * 查询所有单位信息，并根据用户ID将其对应的单位设置为选中
     *
     * @param loginUser 当前登录系统的用户
     * @param userId    被操作用户的ID
     * @return
     */
    List<SecurityUnitT> findUnitListAll(SecurityUserT loginUser, String userId);

    /**
     * 根据用户账号查询是否有重复数据
     *
     * @param account 用户账号
     * @return
     */
    int queryUserByAccount(String account);

    /**
     * 编辑用户
     *
     * @param securityUserT 用户模型
     * @param roleId        角色ID数组
     * @param unitId        所属单位ID
     */
    void editUser(SecurityUserT securityUserT, String[] roleId, String unitId);


    /**
     * 查询用户具备的角色列表
     * @param userId
     * @return
     */
    List<SecurityRoleT> queryUserRolesByUserId(String userId);

    /**
     * 更新用户名、联系方式、email地址
     * @param userId
     * @param name
     * @param linkWay
     * @param emailAddr
     */
    void updateNameAndLinkWay(String userId, String name, String linkWay, String emailAddr);

    /**
     * 更新账户信息
     * @param userId
     * @param userAccount
     * @param passwd
     */
    void updateAccountInfo(String userId, String userAccount, String passwd);

    /**
     * 更新用户的账户，并同步企业邮箱
     * @param userId
     * @param userAccount
     * @param password
     */
    void updateAccountInfoAndTencentSync(String userId, String userAccount, String password);


    /**
     * 判断当前用户是否为系统角色
     *
     * @param userId
     * @return
     */
    boolean isSysRole(String userId, String roleType);

    /**
     * 生成用户登录校验码，并发送邮件
     *
     * @param userAccount
     */
    void generateUserLoginVerification(String userAccount) throws SecurityException;


    /**
     * 处理订阅号服务号
     *
     * @param securityMenuTs
     * @return
     */
    List<SecurityMenuT> consoleMenu(List<SecurityMenuT> securityMenuTs, SecurityUserT securityUserT);

    /**
     * 根据角色信息查询人员集合
     *
     * @param unitID 单位ID
     * @param roleId 角色ID
     * @return
     */
    List<SecurityUserT> queryUserByRole(String unitID, String roleId);

    /**
     * 根据角色查询人员信息
     *
     * @param roleType     角色类型
     * @param resourceType 资源类型
     * @return
     */
    JSONArray queryUserByRole(RoleType roleType, ResourceType resourceType);


    /**
     * 根据用户查询其包含的资源
     *
     * @param securityUserT 用户
     * @param resourceType  资源类型
     * @return
     */
    List<SecurityUserResourceR> queryUserResourceByUser(SecurityUserT securityUserT, ResourceType resourceType);

    /**
     * 查询当前登录人包含的资源
     *
     * @param resourceType 资源类型
     * @return
     */
    List<SecurityUserResourceR> queryUserResourceByCurrentLoginUser(ResourceType resourceType);


    /**
     * 根据按钮code查询具备该按钮的用户集合
     *
     * @param buttonCode 按钮编码
     * @return
     */
    List<SecurityUserT> queryUserByButtonCode(String buttonCode);

    /**
     * 判断某用户的某角色，是否具备资源
     *
     * @param userId       用户ID
     * @param roleId       角色ID
     * @param resourceId   资源ID
     * @param module       所属模块
     * @param resourceType 资源类型
     * @return true 具备资源，false 不具备
     */
    boolean checkUserByRoleResource(String userId, String roleId, String resourceId, Module module, String resourceType);

    /**
     * 编辑用户
     *
     * @return
     */
    SecurityUserT editXD(SecurityUserT securityUserT);

    /**
     * 新增用户
     *
     * @param securityUserT
     * @return
     */
    SecurityUserT addXD(SecurityUserT securityUserT);

    List<String> filterUserByResourceId(List<String> userIds, String resourceId);


    List<String> queryUserByResourceId(String resourceId);

    void cacheEvict();

    /**
     * 执行用户登录的校验码
     */
    void cleanVerification();

    void convertRealUnitName(List<SecurityUserT> securityUserTs);

    /**
     * 绑定微信
     *
     * @param code
     * @param state
     */
    void boundWechat(String code, String state) throws SecurityException;

    /**
     * 绑定登录
     *
     * @param code
     * @param state
     */
    void wechatLogin(String code, String state) throws SecurityException;

    /**
     * 查询用户是否存在
     *
     * @param userAccount
     * @param password
     * @return
     */
    boolean checkUserExist(String userAccount, String password);

    /**
     * 根据 角色和资源信息查询用户信息
     *
     * @param roleId     角色
     * @param resourceId 资源
     * @return
     */
    List<SecurityUserT> queryUsersByRoleIdAndResourceId(String roleId, String resourceId);


    /**
     * 查询 用户ID与密码是否匹配
     * @param userId
     * @param password
     * @return
     */
    boolean checkUserPasswordCorrect(String userId,String password);

    /**
     * 验证用户id和密码是否匹配，匹配则返回用户
     * @param userId
     * @param password
     * @return
     */
    SecurityUserT checkUserIdAndPasswordCorrect(String userId,String password) throws SecurityException;

}
