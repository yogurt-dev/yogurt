package com.github.jyoghurt.security.securityUnitT.service.impl;

import com.github.jyoghurt.qqEmail.domain.UserInfoVo;
import com.github.jyoghurt.qqEmail.service.EmailVersionTService;
import com.github.jyoghurt.security.exception.SecurityException;
import com.github.jyoghurt.security.securityUnitT.dao.SecurityUnitTMapper;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUnitT.enums.UnitTypeEnum;
import com.github.jyoghurt.security.securityUnitT.service.SecurityUnitTService;
import com.github.jyoghurt.security.securityUnitT.vo.TreeViewVo;
import com.github.jyoghurt.security.securityUserResourceR.domain.SecurityUserResourceR;
import com.github.jyoghurt.security.securityUserResourceR.service.SecurityUserResourceService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.security.securityUserT.service.SecurityUserTService;
import com.github.jyoghurt.security.securityUserUnitR.domain.SecurityUserUnitR;
import com.github.jyoghurt.security.securityUserUnitR.service.SecurityUserUnitService;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.service.impl.ServiceSupport;
import com.github.jyoghurt.core.utils.JPAUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.utils.Encrypter;
import pub.utils.SessionUtils;
import pub.utils.SysVarEnum;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

@Service("securityUnitTService")
public class SecurityUnitTServiceImpl extends ServiceSupport<SecurityUnitT, SecurityUnitTMapper> implements
        SecurityUnitTService {


    @Autowired
    private SecurityUnitTMapper securityUnitTMapper;

    @Resource
    private EmailVersionTService emailVersionTService;

    @Resource
    private SecurityUserTService securityUserTService;

    @Resource
    private SecurityUserUnitService securityUserUnitService;

    @Resource
    private SecurityUserResourceService securityUserResourceService;

    @Override
    public SecurityUnitTMapper getMapper() {
        return securityUnitTMapper;
    }

    @Override
    public void delete(Serializable id) {
        getMapper().delete(SecurityUnitT.class, id);
    }

    @Override
    public SecurityUnitT find(Serializable id) {
        return getMapper().selectById(SecurityUnitT.class, id);
    }

//    @Override
//    public List<SecurityUnitT> findUnitByUserInfo(SecurityUserT userT)  {
//        return getMapper().findUnitByUserInfo(userT.getBelongCompany().getUnitId());
//    }

    @Override
    public SecurityUnitT findSecretByUnitId(String unitId) {
        return getMapper().selectById(SecurityUnitT.class, unitId);
    }

//    @Override
//    public List<SecurityUnitT> findUnitTreeByUserId(String userId)  {
//        return getMapper().findUnitTreeByUserId(userId);
//    }

//    @Override
//    public List<SecurityUnitT> findUnitTreeByUserIdAndUnitType(String userId, SysVarEnum unitType) {
//        if(StringUtils.isBlank(unitType.getCode())){
//            return null;
//        }
//        List<SecurityUnitT> securityUnitTS = getMapper().findUnitTreeByUserId(userId);
//        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(securityUnitTS)){
//            Iterator iterator = securityUnitTS.iterator();
//            while(iterator.hasNext()){
//                SecurityUnitT securityUnitT = (SecurityUnitT) iterator.next();
//                if(!unitType.getCode().equals(securityUnitT.getType())){
//                    iterator.remove();
//                }
//            }
//        }
//        return securityUnitTS;
//    }

//    @Override
//    public List<SecurityUnitT> findUnitTreeByUserIdAndUnitTypeAndCompType(String userId, SysVarEnum unitType, SysVarEnum compType) {
//        List<SecurityUnitT> securityUnitTS = this.findUnitTreeByUserIdAndUnitType(userId,unitType);
//        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(securityUnitTS)){
//            Iterator iterator = securityUnitTS.iterator();
//            while(iterator.hasNext()){
//                SecurityUnitT securityUnitT = (SecurityUnitT) iterator.next();
//                if(!compType.getCode().equals(securityUnitT.getCompType())){
//                    iterator.remove();
//                }
//            }
//        }
//        return securityUnitTS;
//    }

    @Override
    public void logicDel(String unitId) {
        getMapper().logicDel(this.getUnitIdList(this.findSubUnitTListByUnitId(unitId)));
    }

    @Override
    public List<SecurityUnitT> findSubUnitTListByUnitId(String unitId) {
        SecurityUnitT securityUnitT = this.find(unitId);
        if (securityUnitT == null) {
            return null;
        }
        List<SecurityUnitT> securityUnitTList = new ArrayList<>();
        securityUnitTList.add(securityUnitT);
        List<String> parentSecurityUnitTIdList = new ArrayList<>();
        parentSecurityUnitTIdList.add(unitId);
        return this.findSubSecurityUnitTListByParentUnitIds(securityUnitTList, parentSecurityUnitTIdList);
    }

    /**
     * 使用in方法，递归查询SecurityUnitT的子节点.add by limiao 20170721. 去掉存储过程
     *
     * @param securityUnitTList         securityUnitTList
     * @param parentSecurityUnitTIdList parentSecurityUnitTIdList
     * @return List<SecurityUnitT>
     */
    private List<SecurityUnitT> findSubSecurityUnitTListByParentUnitIds(List<SecurityUnitT> securityUnitTList, List<String> parentSecurityUnitTIdList) {
        if (CollectionUtils.isEmpty(parentSecurityUnitTIdList)) {
            return securityUnitTList;
        }
        List<SecurityUnitT> subSecurityUnitTs = getMapper().findSubSecurityUnitTListByUnitId(parentSecurityUnitTIdList);
        if (CollectionUtils.isNotEmpty(subSecurityUnitTs)) {
            securityUnitTList.addAll(subSecurityUnitTs);
            findSubSecurityUnitTListByParentUnitIds(securityUnitTList, this.getUnitIdList(subSecurityUnitTs));
        }
        return securityUnitTList;
    }

    /**
     * SecurityUnitT对象集合转换成unitId集合.add by limiao 20170721. 去掉存储过程
     *
     * @param securityUnitTList securityUnitTList
     * @return List<String>
     */
    private List<String> getUnitIdList(List<SecurityUnitT> securityUnitTList) {
        List<String> securityUnitTIdList = new ArrayList<>();
        for (SecurityUnitT securityUnitT : securityUnitTList) {
            securityUnitTIdList.add(securityUnitT.getUnitId());
        }
        return securityUnitTIdList;
    }

    @Override
    public String isCompany(String unitId) {
        String compType = this.find(unitId).getCompType();
        /*公司*/
        if (SysVarEnum.COMPANY_COMPTYPE.getCode().equals(compType)) {
            return SysVarEnum.DEPARTMENT_COMPTYPE.getCode();
        }
        return SysVarEnum.COMPANY_COMPTYPE.getCode();
    }

    @Override
    public List<UserInfoVo> fetchAddressInfo(String clientId, String clientSecurity) {
        try {
            return emailVersionTService.syncEmailUser(clientId, clientSecurity);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public void updateLocalData(List<UserInfoVo> userInfoVos) {
    }

    public SecurityUnitTMapper getSecurityUnitTMapper() {
        return securityUnitTMapper;
    }

    public void setSecurityUnitTMapper(SecurityUnitTMapper securityUnitTMapper) {
        this.securityUnitTMapper = securityUnitTMapper;
    }


    public List<SecurityUnitT> getDept(String type) {
        return findAll(new SecurityUnitT().setType(type).setParentId("-1").setDeleteFlag(false));
    }

    @Override
    public SecurityUnitT getFathCompanyInfo(String unitTId) {
        List<SecurityUnitT> securityUnitTs = this.findAll(new SecurityUnitT().setDeleteFlag(false), new QueryHandle());
        SecurityUnitT securityUnitT = Encrypter.getFathCompanyInfo(securityUnitTs, this.find
                (unitTId));
        return securityUnitT;
    }

    @Override
    public List<SecurityUnitT> queryCompanyExcludeCurrentUser(SecurityUserT securityUserT) {
        SecurityUnitT securityUnitT = securityUserT.getBelongCompany();
        List<SecurityUnitT> securityUnitTs = this.findAll(new SecurityUnitT().setCompType(SysVarEnum.COMPANY_COMPTYPE
                .getCode()).setDeleteFlag(false), new QueryHandle());

        List<SecurityUnitT> result = new ArrayList<SecurityUnitT>();

        for (SecurityUnitT securityUnitT1 : securityUnitTs) {
            if (securityUnitT1.getUnitId().equals(securityUnitT.getUnitId()) || "-1".equals(securityUnitT1.getUnitId())) {
                continue;
            }
            result.add(securityUnitT1);
        }
        return result;
    }


    /**
     * 生成用户管辖的机构信息
     *
     * @param userId
     * @return
     */
    @Override
    public List<SecurityUnitT> generateUserUnitR(String userId) {
        List<SecurityUnitT> securityUnitTs = new ArrayList<>();
        List<SecurityUserUnitR> securityUserUnitRs = securityUserUnitService.findAll(new SecurityUserUnitR()
                .setDeleteFlag(false).setUserIdR(userId));
        for (SecurityUserUnitR securityUserUnitR : securityUserUnitRs) {
            SecurityUnitT securityUnit = new SecurityUnitT();
            securityUnit.setUnitId(securityUserUnitR.getUnitIdR());
            securityUnit.setUnitName(securityUserUnitR.getUnitNameR());
            securityUnit.setParentId(securityUserUnitR.getParentUnitId());
            securityUnit.setCompType("1");
            securityUnit.setSortId(securityUnit.getSortId());
            securityUnitTs.add(securityUnit);
        }
        return securityUnitTs;
    }

    @Override
    public List<TreeViewVo> queryCompanyForUnitTree(SecurityUserT securityUserT) {
        List<TreeViewVo> collections = new ArrayList<>();
        List<TreeViewVo> result = new ArrayList<>();
        //获得所有组织机构
        List<SecurityUnitT> securityUnitAll = this.findAll(new SecurityUnitT().setDeleteFlag(false), new QueryHandle()
                .addOrderBy("sortId", "DESC")
                .addOrderBy("createDateTime", "DESC"));
        //获得所有人员
        List<SecurityUserT> securityUserAll = securityUserTService.findAll(new SecurityUserT().setDeleteFlag(false));
        //组装机构数据
        generateUnitInfo(collections, securityUnitAll);
        //组装人员数据
        generatePersonInfo(collections, securityUserAll);
        //构建用户所属公司下的组织机构及人员数据
        List<TreeViewVo> userBelongUnits = constituteTreeData(securityUserT.getBelongCompany().getUnitId(),
                collections,
                result);
        //补充上级路径
        getSuperUnitPath(securityUnitAll, securityUserT.getBelongCompany(), result);
        //添加本公司
        result.add(convertUnitToTreeView(securityUserT.getBelongCompany()));
        //组装用户管理的组织机构数据
        List<TreeViewVo> userUnits = generateManageUnitAndUserInfo(securityUserT.getUserId(), securityUnitAll,
                securityUserAll);
        //去重
        removeDuplicate(userUnits, userBelongUnits);
        result.addAll(userUnits);
        return result;
    }

    @Override
    public List<TreeViewVo> findSubNodeByNodeId(String subNodeId) {
        List<TreeViewVo> collections = new ArrayList<>();
        List<TreeViewVo> result = new ArrayList<>();
        //获得所有组织机构
        List<SecurityUnitT> securityUnitAll = this.findAll(new SecurityUnitT().setDeleteFlag(false), new QueryHandle()
                .addOrderBy("sortId", "DESC")
                .addOrderBy("createDateTime", "DESC"));
        //获得所有人员
        List<SecurityUserT> securityUserAll = securityUserTService.findAll(new SecurityUserT().setDeleteFlag(false));
        //组装机构数据
        generateUnitInfo(collections, securityUnitAll);
        //组装人员数据
        generatePersonInfo(collections, securityUserAll);
        //构建用户所属公司下的组织机构及人员数据
        List<TreeViewVo> userBelongUnits = constituteTreeData(subNodeId, collections, result);

        SecurityUnitT securityUnitT = this.find(subNodeId);

        //添加本公司
        result.add(convertUnitToTreeView(securityUnitT));
        return result;
    }

    @Override
    public List<TreeViewVo> findSubNodeOfUnitByCurrentNodeId(String currentNodeId) {
        List<TreeViewVo> collections = new ArrayList<>();
        List<TreeViewVo> result = new ArrayList<>();
        //获得所有组织机构
        List<SecurityUnitT> securityUnitAll = this.findAll(new SecurityUnitT().setDeleteFlag(false), new QueryHandle()
                .addOrderBy("sortId", "DESC")
                .addOrderBy("createDateTime", "DESC"));

        //组装机构数据
        generateUnitInfo(collections, securityUnitAll);
        //构建用户所属公司下的组织机构及人员数据
        List<TreeViewVo> userBelongUnits = constituteTreeData(currentNodeId, collections, result);

        SecurityUnitT securityUnitT = this.find(currentNodeId);

        //添加本公司
        result.add(convertUnitToTreeView(securityUnitT));
        return result;
    }

    @Override
    public List<TreeViewVo> queryCompanyForUnitTreeByUnit(SecurityUserT securityUserT, String unitId) {
        List<TreeViewVo> collections = new ArrayList<>();
        List<TreeViewVo> result = new ArrayList<>();
        //获得所有组织机构
        this.findAll(new SecurityUnitT().setDeleteFlag(false), new QueryHandle().addOrderBy("sortId", "DESC")
                .addOrderBy("createDateTime", "DESC"));
        List<SecurityUnitT> securityUnitAll = this.findAll(new SecurityUnitT().setDeleteFlag(false));
        //获得所有人员
        List<SecurityUserT> securityUserAll = securityUserTService.findAll(new SecurityUserT().setDeleteFlag(false));
        //组装机构数据
        generateUnitInfo(collections, securityUnitAll);
        //组装人员数据
        generatePersonInfo(collections, securityUserAll);
        //构建用户所属公司下的组织机构及人员数据
        List<TreeViewVo> userBelongUnits = constituteTreeData("ALL".equals(unitId) ? securityUserT.getBelongCompany()
                .getUnitId() : unitId, collections, result);
        SecurityUnitT securityUnitT = "ALL".equals(unitId) ? securityUserT.getBelongCompany() : this.find(unitId);
        //补充上级路径
        getSuperUnitPath(securityUnitAll, securityUnitT, result);
        //添加本公司
        result.add(convertUnitToTreeView(securityUnitT));
        //组装用户管理的组织机构数据
        List<TreeViewVo> userUnits = generateManageUnitAndUserInfo(securityUserT.getUserId(), securityUnitAll, securityUserAll);
        //去重
        removeDuplicate(userUnits, userBelongUnits);
        result.addAll(userUnits);
        return result;
    }


    @Override
    public List<TreeViewVo> queryUserBelongUnit(SecurityUserT loginUser) {
        List<TreeViewVo> collections = new ArrayList<>();
        List<TreeViewVo> result = new ArrayList<>();
        //获得所有组织机构
        List<SecurityUnitT> securityUnitAll = this.findAll(new SecurityUnitT().setDeleteFlag(false));
        //组装机构数据
        generateUnitInfo(collections, securityUnitAll);
        //构建组织机构树
        constituteTreeData(loginUser.getBelongCompany().getUnitId(), collections, result);
        //添加本单位
        result.add(convertUnitToTreeView(loginUser.getBelongCompany()));
        //补充上层路径
        getSuperUnitPath(securityUnitAll, loginUser.getBelongCompany(), result);
        //获取拥有的管理单位
        List<TreeViewVo> manageUnit = generateManageUnitAndUserInfo(loginUser.getUserId(), securityUnitAll);
        //补充上层路径
        getSuperUnitPath(securityUnitAll, loginUser.getBelongCompany(), manageUnit);
        //去重
        removeDuplicate(manageUnit, result);
        //添加结果集
        result.addAll(manageUnit);
        return result;
    }

    @Override
    public List<TreeViewVo> queryUserOwnCompanyInfo(SecurityUserT loginUser) {
        List<TreeViewVo> collections = new ArrayList<>();
        List<TreeViewVo> result = new ArrayList<>();
        //获得所有组织机构
        List<SecurityUnitT> securityUnitAll = this.findAll(new SecurityUnitT().setDeleteFlag(false));
        //组装机构数据
        generateUnitInfo(collections, securityUnitAll);
        //构建组织机构树
        constituteTreeData(loginUser.getBelongCompany().getUnitId(), collections, result);
        //添加本单位
        result.add(convertUnitToTreeView(loginUser.getBelongCompany()));
        //补充上层路径
        getSuperUnitPath(securityUnitAll, loginUser.getBelongCompany(), result);
        return result;
    }

    @Override
    public void queryUsersUnderUnit(List<TreeViewVo> allNodes, TreeViewVo currentNode, List<TreeViewVo> users) {
        for (TreeViewVo treeViewVo : allNodes) {
            if (currentNode.getUnitId().equals(treeViewVo.getParentId())) {
                if ("3".equals(treeViewVo.getCompType())) {
                    users.add(treeViewVo);
                    continue;
                }
                queryUsersUnderUnit(allNodes, treeViewVo, users);
            }
        }
    }

    @Override
    public List<TreeViewVo> queryAllUnitTreeNode() {
        List<SecurityUnitT> securityUnitTs = findAll(new SecurityUnitT().setDeleteFlag(false));
        List<TreeViewVo> treeViewVos = new ArrayList<>();
        for (SecurityUnitT securityUnitT : securityUnitTs) {
            treeViewVos.add(convertUnitToTreeView(securityUnitT));
        }
        return treeViewVos;
    }

    @Override
    public void queryUsersUnderUnit(String unitId, List<TreeViewVo> users) {
        //查询所有机构及人员
        List<TreeViewVo> treeViewVos = findSubNodeByNodeId("-1");
        SecurityUnitT securityUnitT = find(unitId);
        queryUsersUnderUnit(treeViewVos, this.convertUnitToTreeView(securityUnitT), users);
    }

    /**
     * 批量组装组织机构信息
     *
     * @return
     */
    private void generateUnitInfo(List<TreeViewVo> collections, List<SecurityUnitT> securityUnitAll) {

        //放入组织机构信息
        for (SecurityUnitT securityUnitT : securityUnitAll) {
            generateUnitInfoSingle(collections, securityUnitT);
        }
        //排序
        Collections.sort(collections, new Comparator<TreeViewVo>() {
            @Override
            public int compare(TreeViewVo o1, TreeViewVo o2) {
                if (o1.getSortId() == null) {
                    if (o2.getSortId() == null)
                        return 0;
                    else
                        return 1;
                } else {
                    if (o2.getSortId() == null) {
                        return -1;
                    } else {
                        if (o1.getSortId() > o2.getSortId()) {
                            return 1;
                        }
                        if (o1.getSortId() == o2.getSortId()) {
                            return 0;
                        }
                        return -1;
                    }
                }
            }
        });

    }

    //组装组织机构信息
    private void generateUnitInfoSingle(List<TreeViewVo> collections, SecurityUnitT securityUnitT) {
        TreeViewVo treeViewVo = new TreeViewVo();
        treeViewVo.setUnitId(securityUnitT.getUnitId());
        treeViewVo.setUnitName(securityUnitT.getUnitName());
        treeViewVo.setParentId(securityUnitT.getParentId());
        treeViewVo.setCompType(securityUnitT.getCompType());
        treeViewVo.setSortId(securityUnitT.getSortId());
        collections.add(treeViewVo);
    }

    private TreeViewVo convertUnitToTreeView(SecurityUnitT securityUnitT) {
        TreeViewVo treeViewVo = new TreeViewVo();
        try {
            PropertyUtils.copyProperties(treeViewVo, securityUnitT);
        } catch (Exception e) {
            logger.debug("属性转换出错：" + e);
        }
        return treeViewVo;
    }

    /**
     * 组装人员信息
     *
     * @param collections
     * @return
     */
    private void generatePersonInfo(List<TreeViewVo> collections, List<SecurityUserT> securityUserAll) {

        //放入人员信息
        putUnitInfoForUser(securityUserAll, collections);
    }

    /**
     * 生成用户管理的机构及机构下人员数据
     *
     * @param userId
     * @return
     */
    private List<TreeViewVo> generateManageUnitAndUserInfo(String userId, List<SecurityUnitT> securityUnitTs) {
        List<TreeViewVo> result = new ArrayList<>();
        List<SecurityUserResourceR> securityUserResourceRs = securityUserResourceService.findAll(new
                SecurityUserResourceR().setResourceType("unit").setUserId(userId).setDeleteFlag(false));
        List<SecurityUnitT> unitAll = new ArrayList<>();

        for (SecurityUnitT securityUnitT : securityUnitTs) {
            for (SecurityUserResourceR securityUserResourceR : securityUserResourceRs) {
                if (securityUnitT.getUnitId().equals(securityUserResourceR.getResourceId())) {
                    unitAll.add(securityUnitT);
                }
            }
        }
        //添加下属单位
        putUnitInfoForUserUnit(unitAll, result);
        return result;
    }


    /**
     * 生成用户管理的机构及机构下人员数据
     *
     * @param userId
     * @return
     */
    private List<TreeViewVo> generateManageUnitAndUserInfo(String userId, List<SecurityUnitT> securityUnitTs,
                                                           List<SecurityUserT> securityUserTs) {

        List<TreeViewVo> result = new ArrayList<>();
        List<SecurityUserResourceR> securityUserResourceRs = securityUserResourceService.findAll(new
                SecurityUserResourceR().setResourceType("unit").setUserId(userId).setDeleteFlag(false));
        List<SecurityUnitT> unitAll = new ArrayList<>();

        for (SecurityUnitT securityUnitT : securityUnitTs) {
            for (SecurityUserResourceR securityUserResourceR : securityUserResourceRs) {
                if (securityUnitT.getUnitId().equals(securityUserResourceR.getResourceId())) {
                    unitAll.add(securityUnitT);
                }
            }
        }
        //添加下属单位
        putUnitInfoForUserUnit(unitAll, result);
        //添加单位下属人员
        putUserInfoForUserUnit(unitAll, securityUserTs, result);
        return result;
    }


//    /**
//     * 生成用户管辖单位数据
//     *
//     * @param userId 当前登陆用户ID
//
//     */
//    private List<TreeViewVo> generateUserUnitInfo(String userId)   {
//        List<TreeViewVo> result = new ArrayList<>();
//        //获取用户管辖的所有单位及下属用户
//        List<SecurityUserUnitR> userUnitRs = securityUserUnitService.findAll(new SecurityUserUnitR().setDeleteFlag
//                (false).setUserIdR(userId), new QueryHandle().addJoinHandle("u.*", SQLJoinHandle.JoinType
//                .LEFT_OUTER_JOIN, "SecurityUserT u on t.unitIdR = u.belongOrg").addJoinHandle("ut.compType", SQLJoinHandle
//                .JoinType.INNER_JOIN, "SecurityUnitT ut on t.unitIdR = ut.unitId")
//                .customWhereSql("t" +
//                        ".userIdR=#{data" +
//                        ".userIdR}").addExpandData("userIdR", userId));
//        putUnitInfoForUserUnit(userUnitRs, result);
//        for (SecurityUserUnitR securityUserUnitR : userUnitRs) {
//            //获取单位下的用户信息
//            List<SecurityUserT> underUnits = securityUserUnitR.getUnderUsers();
//            //将用户信息放入
//            putUnitInfoForUser(underUnits, result);
//        }
//        return result;
//    }

    private void putUserInfoForUserUnit(List<SecurityUnitT> securityUnitTs, List<SecurityUserT> securityUserTs,
                                        List<TreeViewVo> treeViewVos) {

        List<SecurityUserT> securityUserTList = new ArrayList<>();
        for (SecurityUnitT securityUnitT : securityUnitTs) {
            for (SecurityUserT securityUserT1 : securityUserTs) {
                if (securityUnitT.getUnitId().equals(securityUserT1.getBelongOrg())) {
                    securityUserTList.add(securityUserT1);
                }
            }
        }
        putUnitInfoForUser(securityUserTList, treeViewVos);
    }


    private void putUnitInfoForUserUnit(List<SecurityUnitT> securityUnitTs, List<TreeViewVo> collections) {
        List<TreeViewVo> result = new ArrayList<>();
        for (SecurityUnitT securityUnitT : securityUnitTs) {
            TreeViewVo treeViewVo = new TreeViewVo();
            treeViewVo.setUnitId(securityUnitT.getUnitId());
            treeViewVo.setUnitName(securityUnitT.getUnitName());
            treeViewVo.setParentId(securityUnitT.getParentId());
            treeViewVo.setCompType(securityUnitT.getCompType());
            treeViewVo.setSortId(0);
            result.add(treeViewVo);
        }
        collections.addAll(result);
    }

//    private void putUnitInfoForUserUnit(List<SecurityUserUnitR> securityUserUnitRs, List<TreeViewVo> collections) {
//        List<TreeViewVo> result = new ArrayList<>();
//        //放入人员信息
//        for (SecurityUserUnitR securityUserUnitR : securityUserUnitRs) {
//            TreeViewVo treeViewVo = new TreeViewVo();
//            treeViewVo.setUnitId(securityUserUnitR.getUnitIdR());
//            treeViewVo.setUnitName(securityUserUnitR.getUnitNameR());
//            treeViewVo.setParentId(securityUserUnitR.getParentUnitId());
//            treeViewVo.setCompType(securityUserUnitR.getCompType());
//            treeViewVo.setSortId(0);
//            result.add(treeViewVo);
//        }
//        collections.addAll(result);
//    }

    private void putUnitInfoForUser(List<SecurityUserT> securityUserTs, List<TreeViewVo> collections) {
        List<TreeViewVo> result = new ArrayList<>();
        //放入人员信息
        for (SecurityUserT securityUserT : securityUserTs) {
            TreeViewVo treeViewVo = new TreeViewVo();
            treeViewVo.setUnitId(securityUserT.getUserId());
            treeViewVo.setUnitName(securityUserT.getUserName());
            treeViewVo.setParentId(securityUserT.getBelongOrg());
            treeViewVo.setCompType("3");
            treeViewVo.setSortId(0);
            result.add(treeViewVo);
        }
        collections.addAll(result);
    }


    /**
     * 获取机构树上层路径
     *
     * @param securityUnitAll
     * @param startNode
     * @return
     */
    private void getSuperUnitPath(List<SecurityUnitT> securityUnitAll, SecurityUnitT startNode, List<TreeViewVo> collections) {
        securityUnitAll.stream().filter(currentNode -> startNode.getParentId().equals(currentNode.getUnitId())).forEach(currentNode -> {
            generateUnitInfoSingle(collections, currentNode);
            getSuperUnitPath(securityUnitAll, currentNode, collections);
        });
    }

    /**
     * 去重组织机构
     *
     * @param targetUnits 去重目标
     * @param sourceUnit  参照
     */
    private void removeDuplicate(List<TreeViewVo> targetUnits, List<TreeViewVo> sourceUnit) {
        Iterator it = targetUnits.iterator();
        while (it.hasNext()) {
            TreeViewVo node = (TreeViewVo) it.next();
            for (TreeViewVo source : sourceUnit) {
                if (node.getUnitId().equals(source.getUnitId())) {
                    it.remove();
                }
            }
        }
    }


    /**
     * 构建组织机构树数据结构
     *
     * @param unitId           组织机构ID
     * @param securityUnitTs   数据集合
     * @param resultCollection 返回结果
     * @return
     */
    private List<TreeViewVo> constituteTreeData(String unitId, List<TreeViewVo> securityUnitTs,
                                                List<TreeViewVo> resultCollection) {
        securityUnitTs.stream().filter(securityUnitT -> unitId.equals(securityUnitT.getParentId())).forEach(securityUnitT -> {
            resultCollection.add(securityUnitT);
            constituteTreeData(securityUnitT.getUnitId(), securityUnitTs, resultCollection);
        });
        return resultCollection;
    }

    @Override
    public List filter(List targetList, UnitTypeEnum unitTypeEnum, String unitIdColumn) {
        if (CollectionUtils.isEmpty(targetList) || StringUtils.isEmpty(unitIdColumn)) {
            return targetList;
        }

        List<SecurityUnitT> units = this.findAll(new SecurityUnitT().setDeleteFlag(false).setCompType(unitTypeEnum.getTypeCode()));

        Iterator iterator = targetList.iterator();
        while (iterator.hasNext()) {
            Object entity = iterator.next();
            if (StringUtils.isEmpty((String) JPAUtils.getValue(entity, unitIdColumn))) {
                iterator.remove();
            }
            boolean deleteFlag = true;
            for (SecurityUnitT securityUnitT : units) {
                if ((JPAUtils.getValue(entity, unitIdColumn)).equals(securityUnitT.getUnitId())) {
                    deleteFlag = false;
                    break;
                }
            }
            if (deleteFlag) {
                iterator.remove();
            }
        }
        return targetList;
    }

    @Override
    public SecurityUnitT addUnit(SecurityUnitT securityUnitT) throws SecurityException {
        String parentId = securityUnitT.getParentId();
        if (parentId.indexOf("9999") >= 0) {
            securityUnitT.setParentId("-1");
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(securityUnitT.getCol2Col4s())) {
            String uuid = UUID.randomUUID().toString();
            securityUnitT.setCol1Col4s(uuid.substring(uuid.length() - 15, uuid.length()));
        }

        //查询父节点是否在 当前登录人所属机构下
        List<TreeViewVo> underUnits = this.findSubNodeOfUnitByCurrentNodeId(SessionUtils.getManager()
                .getBelongCompany().getUnitId());
        boolean canCreate = false;
        for (TreeViewVo underUnit : underUnits) {
            if (parentId.equals(underUnit.getUnitId())) {
                canCreate = true;
                break;
            }
        }
        //通讯录下的人，可以随意增加机构
        if (canCreate || "-1".equals(SessionUtils.getManager()
                .getBelongCompany().getUnitId())) {
            this.save(securityUnitT);
            return securityUnitT;
        }
        throw new SecurityException(SecurityException.ERROR_90805);
    }

    //    /**
//     * 查找集合中的根节点
//     * @param securityUnitTs
//     * @param startUnitT
//     * @return
//     */
//    private SecurityUnitT findRoot(List<SecurityUnitT> securityUnitTs,SecurityUnitT startUnitT){
//        boolean isParent = false;
//        SecurityUnitT parent = new SecurityUnitT();
//        for(SecurityUnitT securityUnitT:securityUnitTs){
//            if(securityUnitT.getUnitId().equals(startUnitT.getParentId())){
//                isParent = true;
//                parent = securityUnitT;
//                break;
//            }
//        }
//        if(isParent){
//            findRoot(securityUnitTs,parent);
//        }
//        return startUnitT;
//    }
}
