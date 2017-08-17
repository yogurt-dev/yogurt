package com.github.jyoghurt.security.securityUnitT.controller;

import com.github.jyoghurt.qqEmail.domain.UserInfoVo;
import com.github.jyoghurt.security.exception.SecurityException;
import com.github.jyoghurt.security.securityDataDic.service.SecurityDataDicService;
import com.github.jyoghurt.security.securityUnitT.domain.SecurityUnitT;
import com.github.jyoghurt.security.securityUnitT.service.SecurityUnitTService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.security.securityUserT.service.SecurityUserTService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.*;
import pub.utils.SecurityCustomUtils;
import pub.utils.SessionUtils;
import pub.utils.SysVarEnum;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * 单位管理控制器
 */
@RestController
@RequestMapping("/securityUnitT")
public class SecurityUnitTController extends BaseController implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    /**
     * 单位管理服务类
     */
    @Resource
    private SecurityUnitTService securityUnitTService;

    @Resource
    private SecurityDataDicService securityDataDicService;

    @Resource
    private SecurityUserTService securityUserTService;


    /**
     * 添加单位管理
     */
    @LogContent("添加单位管理")
    @RequestMapping(value = "/addUnit", method = RequestMethod.POST)
    public HttpResultEntity<?> addUnit(@RequestBody SecurityUnitT securityUnitT) throws SecurityException {
        return getSuccessResult(securityUnitTService.addUnit(securityUnitT));
    }

    /**
     * 编辑单位管理
     */

    @LogContent("编辑单位管理")
    @RequestMapping(value = "/editUnit", method = RequestMethod.PUT)
    public HttpResultEntity<?> editUnit(@RequestBody SecurityUnitT securityUnitT)   {

        if (!StringUtils.isNotEmpty(securityUnitT.getCol1Col4s())) {
            String uuid = UUID.randomUUID().toString();
            securityUnitT.setCol1Col4s(uuid.substring(uuid.length() - 15, uuid.length()));
        }
        securityUnitTService.updateForSelective(securityUnitT);
        return getSuccessResult(securityUnitT);
    }

    /**
     * 删除单个单位管理
     */
    @LogContent("删除单位管理")
    @RequestMapping(value = "/{unitId}", method = RequestMethod.DELETE)
    public HttpResultEntity<?> delete(@PathVariable String unitId)   {
        try {
            SecurityCustomUtils.syncUnit(securityUnitTService.find(unitId), securityUnitTService
                    .findAll(new SecurityUnitT()), SysVarEnum.DELETE_OPPERTYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        securityUnitTService.logicDel(unitId);
        return getSuccessResult();
    }

    /**
     * 查询单个单位管理
     */
    @LogContent("查询单个单位管理")
    @RequestMapping(value = "/find/{unitId}", method = RequestMethod.GET)
    public HttpResultEntity<?> find(@PathVariable String unitId)   {
        return getSuccessResult(securityUnitTService.find(unitId));
    }

//    /**
//     * 根据当前登录用户信息，查询当前单位及下属单位信息
//     *
//     * @return
//     * @
//     */
//    @LogContent("根据当前登录用户信息，查询当前单位及下属单位信息")
//    @RequestMapping(value = "/findUnitByUserInfo", method = RequestMethod.GET)
//    public HttpResultEntity<?> findUnitByUserInfo()   {
//
//        return getSuccessResult(securityUnitTService.findUnitByUserInfo((SecurityUserT) session.getAttribute
//                (SessionUtils.SESSION_MANAGER)));
//    }

    /**
     * 根据当前登录用户信息，构建组织机构人员树
     *
     * @return
     * @
     */
    @LogContent("根据当前登录用户信息，构建组织机构人员树")
    @RequestMapping(value = "/findUnitByUserInfo", method = RequestMethod.GET)
    public HttpResultEntity<?> findUnitByUserInfo()   {
        //获取用户所属组织机构
        SecurityUserT loginUser = (SecurityUserT) session.getAttribute
                (SessionUtils.SESSION_MANAGER);
        return getSuccessResult(securityUnitTService.queryCompanyForUnitTree(loginUser));
    }

    /**
     * 查询当前机构下的机构和人的信息
     *
     * @return
     * @
     */
    @LogContent("查询当前机构下的机构和人的信息")
    @RequestMapping(value = "/findSubNodeByNodeId/{nodeId}", method = RequestMethod.GET)
    public HttpResultEntity<?> findSubNodeByNodeId(@PathVariable String nodeId)   {
        return getSuccessResult(securityUnitTService.findSubNodeByNodeId(nodeId));
    }

    /**
     * 根据类型查询单位
     *
     * @return
     * @
     */
    @LogContent("根据类型查询单位")
    @RequestMapping(value = "/findUnitByType/{type}", method = RequestMethod.GET)
    public HttpResultEntity<?> queryUnitTypeByType(@PathVariable String type)   {
        //获取用户所属组织机构
        SecurityUserT loginUser = (SecurityUserT) session.getAttribute
                (SessionUtils.SESSION_MANAGER);
        QueryHandle queryHandle = new QueryHandle();
        queryHandle.addWhereSql("unitId NOT IN ('-1')");
        return getSuccessResult(securityUnitTService.findAll(new SecurityUnitT().setDeleteFlag(false), queryHandle));
    }

    /**
     * 根据当前登录用户信息，构建组织机构人员树
     *
     * @param unitId 部门Id
     * @return
     * @
     */
    @LogContent("根据当前登录用户信息，构建组织机构人员树")
    @RequestMapping(value = "/findUnitByUserInfo/{unitId}", method = RequestMethod.GET)
    public HttpResultEntity<?> findUnitByUserInfo(@PathVariable String unitId)   {
        //获取用户所属组织机构
        SecurityUserT loginUser = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        return getSuccessResult(securityUnitTService.queryCompanyForUnitTreeByUnit(loginUser,unitId));
    }

    /**
     * 根据当前登录用户信息，构建组织机构树
     *
     * @return 组织机构树信息
     * @
     */
    @LogContent("根据当前登录用户信息，构建组织机构树")
    @RequestMapping(value = "/generateUnitTree", method = RequestMethod.GET)
    public HttpResultEntity<?> generateUnitTree()   {
        return getSuccessResult(securityUnitTService.queryUserBelongUnit((SecurityUserT) session.getAttribute
                (SessionUtils.SESSION_MANAGER)));
    }

    /**
     * 查询所有组织机构信息
     *
     * @return
     * @
     */
    @LogContent("查询所有组织机构信息")
    @RequestMapping(value = "/findUnitAll", method = RequestMethod.GET)
    public HttpResultEntity<?> findUnitAll()   {
        return getSuccessResult(securityUnitTService.findAll(new SecurityUnitT().setDeleteFlag(false), new QueryHandle
                ()));
    }
    /**
     * 查询所有组织机构信息
     *
     * @return
     * @
     */
    @LogContent("查询所有组织机构信息")
    @RequestMapping(value = "/findUnitTreeByUnitId/{unitId}", method = RequestMethod.GET)
    public HttpResultEntity<?> findUnitAll(@PathVariable String unitId)   {
        return getSuccessResult(securityUnitTService.findAll(new SecurityUnitT().setDeleteFlag(false), new QueryHandle
                ()));
    }
    /**
     * 根据当前登录用户信息，查询单位类型
     *
     * @return
     * @
     */
    @LogContent("根据当前登录用户信息，查询当前单位及下属单位类型")
    @RequestMapping(value = "/queryUnitTypeByUser", method = RequestMethod.GET)
    public HttpResultEntity<?> queryUnitTypeByUser()   {
        return getSuccessResult(securityDataDicService.queryDicByUser(((SecurityUserT) session.getAttribute(SessionUtils
                .SESSION_MANAGER)), "companyType"));
    }

    /**
     * 根据当前登录用户查询该用户所属单位，及其下属单位信息
     *
     * @return
     * @
     */
//    @LogContent("根据当前登录用户查询该用户所属单位，及其下属单位信息")
//    @RequestMapping("/findUnitTreeByUserId")
//    public HttpResultEntity<?> findUnitTreeByUserId()   {
//        return getSuccessResult(securityUnitTService.findUnitTreeByUserId(((SecurityUserT) session.getAttribute(SessionUtils
//                .SESSION_MANAGER)).getUserId()));
//    }


    /**
     * 移动节点
     *
     * @return
     * @
     */
    @LogContent("移动节点")
    @RequestMapping(value = "/dragAndDrop/{compType}/{sourceId}/{targetType}/{targetId}/{operType}", method =
            RequestMethod.PUT)
    public HttpResultEntity<?> dragAndDrop(@PathVariable String compType, @PathVariable String sourceId,
                                           @PathVariable String targetType, @PathVariable String targetId,
                                           @PathVariable String operType)   {

        //定义目标对象
        Object sourceNode;
        Object targetNode;

        //判断节点类型，以获得对应的实体
        //如果是人
        if (SysVarEnum.HUM_COMPTYPE.getCode().equals(compType)) {
            //获得人员记录
            sourceNode = securityUserTService.find(sourceId);
        } else {
            //如果是公司或部门
            sourceNode = securityUnitTService.find(sourceId);
        }


        if (SysVarEnum.HUM_COMPTYPE.getCode().equals(targetType)) {
            //获得人员记录
            targetNode = securityUserTService.find(targetId);
        } else {
            //如果是公司或部门
            targetNode = securityUnitTService.find(targetId);
        }

        //获取父节点ID
        String targetNodeId = "";
        String targetNodeName = "";
        String targetParentId = "";
        String targetParentName = "";

        if (targetNode instanceof SecurityUnitT) {
            targetNodeId = ((SecurityUnitT) targetNode).getUnitId();
            targetNodeName = ((SecurityUnitT)targetNode).getUnitName();
            targetParentId = ((SecurityUnitT) targetNode).getParentId();
        }

        if (targetNode instanceof SecurityUserT) {
            targetNodeId = ((SecurityUserT) targetNode).getUserId();
            targetParentId = ((SecurityUserT) targetNode).getBelongOrg();

        }

        //根据操作类型不同，确定记录更新方式
        //如果是“after”或“before”那么，原节点的父节点ID属性，应与目标节点得父节点ID属性相同
        //如果是“over”，原节点的父节点ID属性，应为目标节点ID
        if ("prev".equals(operType) || "next".equals(operType)) {
            if (sourceNode instanceof SecurityUserT) {
                ((SecurityUserT) sourceNode).setBelongOrg(targetParentId);
                ((SecurityUserT) sourceNode).setBelongOrgName(targetNodeName);
            } else if (sourceNode instanceof SecurityUnitT) {
                ((SecurityUnitT) sourceNode).setParentId(targetParentId);
            }
        }

        if ("inner".equals(operType)) {
            if (sourceNode instanceof SecurityUserT) {
                ((SecurityUserT) sourceNode).setBelongOrg(targetNodeId);
                ((SecurityUserT) sourceNode).setBelongOrgName(targetNodeName);
            }
            if (sourceNode instanceof SecurityUnitT) {
                ((SecurityUnitT) sourceNode).setParentId(targetNodeId);
            }
        }

        //入库
        if (sourceNode instanceof SecurityUserT) {
            securityUserTService.updateForSelective((SecurityUserT) sourceNode);
        }
        if (sourceNode instanceof SecurityUnitT) {
            securityUnitTService.updateForSelective((SecurityUnitT) sourceNode);
        }

        return getSuccessResult();

    }

    /**
     * 根据当前登录用户查询该用户所属单位，及其下属单位信息
     *
     * @return
     * @
     */

    @LogContent("同步组织机构")
    @RequestMapping("/addressSync")
    public HttpResultEntity<?> addressSync()   {
        List<UserInfoVo> updateData = securityUnitTService.fetchAddressInfo(SysVarEnum.CLIENT_ID.getCode(), SysVarEnum
                .CLIENT_SECURITY.getCode());

        securityUnitTService.updateLocalData(updateData);
        return null;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
