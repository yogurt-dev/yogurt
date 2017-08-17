package com.github.jyoghurt.security.securityUnitT.service;

import com.github.jyoghurt.qqEmail.domain.UserInfoVo;
import com.github.jyoghurt.security.exception.SecurityException;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUnitT.enums.UnitTypeEnum;
import com.github.jyoghurt.security.securityUnitT.vo.TreeViewVo;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.core.service.BaseService;

import java.util.List;

/**
 *   
 *
 * @author baoxiaobing@lvyushequ.com 
 * @author baoxiaobing@lvyushequ.com
 * @author baoxiaobing@lvyushequ.com
 * @version V1.1
 * @Title: SecurityUnitTService
 * @Package com.df.security.securityUnitT.service
 * @Description: 组织机构管理服务层
 * @date 2015-08-15
 * @modify
 * @desc:添加通讯录同步动作
 * @date:2015-10-28
 * @modify
 * @desc:删除findUnitByUserInfo 由原有的从数据库读取组织机构数数据，改为内存
 * @data 2016-5-9
 */

public interface SecurityUnitTService extends BaseService<SecurityUnitT> {

    /**
     * 根据组织机构ID 查询 相关信息
     *
     * @param unitId
     * @return info.put("appId", unit.getAppId());
     * info.put("secretKey",unit.getSecretKey());
     */
    SecurityUnitT findSecretByUnitId(String unitId);

    /**
     * 根据用户ID查询其所在单位及下属单位信息
     *
     * @param userId
     * @return
     */
    //List<SecurityUnitT> findUnitTreeByUserId(String userId) ;

    /**
     * 根据用户ID、机构类型 查询其所在单位及下属单位信息
     * @param userId
     * @param unitType
     * @return
     */
    //List<SecurityUnitT> findUnitTreeByUserIdAndUnitType(String userId,SysVarEnum unitType);

    /**
     * 根据用户ID、机构类型、公司类型查询其所在单位及下属单位信息
     * @param userId
     * @param type 公司类型
     * @param compType 机构类型
     * @return
     */
    //List<SecurityUnitT> findUnitTreeByUserIdAndUnitTypeAndCompType(String userId, SysVarEnum type, SysVarEnum compType);

    /**
     * 根据单位ID，逻辑删除该单位及下属单位
     *
     * @param unitId
     */
    void logicDel(String unitId);

    /**
     * 查询单位子节点. add by limiao 20170721
     *
     * @param unitId unitId
     * @return List<SecurityUnitT>
     */
    List<SecurityUnitT> findSubUnitTListByUnitId(String unitId);

    String isCompany(String unitId);

    /**
     * 调用腾讯提供接口，获取人员数据
     *
     * @param clientId
     * @param clientSecurity
     * @return
     */
    List<UserInfoVo> fetchAddressInfo(String clientId, String clientSecurity);

    /**
     * 将获取的腾讯通讯录更新到本地数据库
     *
     * @param userInfoVos
     */
    void updateLocalData(List<UserInfoVo> userInfoVos);

    /**
     * 根据公司类型查询其下属部门列表
     *
     * @param type 组织机构类型 0-4s店 1-保险代理公司 2-保险公估公司
     */
    List<SecurityUnitT> getDept(String type);

    /**
     * 根据组织机构ID，获取其上级公司
     *
     * @param unitTId
     * @return
     */
    SecurityUnitT getFathCompanyInfo(String unitTId);

    /**
     * 查询所有公司除了 当前登录用户所属公司
     *
     * @param securityUserT
     * @return
     */
    List<SecurityUnitT> queryCompanyExcludeCurrentUser(SecurityUserT securityUserT);

    List<SecurityUnitT> generateUserUnitR(String userId);

    /**
     * 查询当前用户下属的组织机构及人员
     *
     * @param userInfo 登陆用户信息
     * @return
     */
    List<TreeViewVo> queryCompanyForUnitTree(SecurityUserT userInfo);

    /**
     * 根据当前机构ID查询下属人及机构信息
     *
     * @param subNodeId
     * @return
     */
    List<TreeViewVo> findSubNodeByNodeId(String subNodeId);


    /**
     * 根据当前机构ID，查询下属机构
     *
     * @param currentNodeId
     * @return
     */
    List<TreeViewVo> findSubNodeOfUnitByCurrentNodeId(String currentNodeId);

    /**
     * 查询当前用户下属的组织机构及人员
     *
     * @param securityUserT 登陆用户信息
     * @param unitId        部门Id
     * @return
     */
    List<TreeViewVo> queryCompanyForUnitTreeByUnit(SecurityUserT securityUserT, String unitId);

    /**
     * 查询 当前用户 下属的所有组织机构
     *
     * @param loginUser 当前登录用户
     * @return 组织机构树信息
     */
    List<TreeViewVo> queryUserBelongUnit(SecurityUserT loginUser);

    /**
     * 查询节点下所有的人员节点，包含子孙节点
     *
     * @param allNodes    所有的节点
     * @param currentNode 当前节点
     * @param users       包含的人员节点
     */
    void queryUsersUnderUnit(List<TreeViewVo> allNodes, TreeViewVo currentNode, List<TreeViewVo> users);

    /**
     * 查询所有机构节点
     *
     * @return
     */
    List<TreeViewVo> queryAllUnitTreeNode();

    /**
     * 查询机构下的所有人员信息
     */
    void queryUsersUnderUnit(String unitId, List<TreeViewVo> users);

    /**
     * 根据公司类型筛选数据
     *
     * @param targetList   目标数据
     * @param unitTypeEnum 公司类型
     * @param unitIdColumn 对应的公司Id属性名称
     * @return
     */
    List filter(List targetList, UnitTypeEnum unitTypeEnum, String unitIdColumn);

    /**
     * 新增组织机构
     *
     * @param securityUnitT
     * @return
     */
    SecurityUnitT addUnit(SecurityUnitT securityUnitT) throws SecurityException;

    /**
     * 查询用户上级及下级单位
     *
     * @param securityUserT
     * @return
     */
    List<TreeViewVo> queryUserOwnCompanyInfo(SecurityUserT securityUserT);
}
