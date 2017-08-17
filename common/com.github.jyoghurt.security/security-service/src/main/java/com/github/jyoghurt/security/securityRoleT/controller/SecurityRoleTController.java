package com.github.jyoghurt.security.securityRoleT.controller;

import com.github.jyoghurt.security.securityRoleT.domain.SecurityRoleT;
import com.github.jyoghurt.security.securityRoleT.service.SecurityRoleTService;
import com.github.jyoghurt.security.securityUnitT.service.SecurityUnitTService;
import com.github.jyoghurt.security.securityUserRoleR.domain.SecurityUserRoleR;
import com.github.jyoghurt.security.securityUserRoleR.service.SecurityUserRoleRService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.security.securityUserT.service.SecurityUserTService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.handle.SQLJoinHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.result.QueryResult;
import com.github.jyoghurt.core.utils.ChainMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pub.utils.SessionUtils;
import pub.utils.SysVarEnum;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 角色控制器
 */
@Controller
@LogContent(module = "角色管理")
@RequestMapping("/service/securityRoleT")
public class SecurityRoleTController extends BaseController {

	/*========================================================资源引入==================================================================*/
    /**
     * 角色服务类
     */
    @Resource
    private SecurityRoleTService securityRoleTService;

    @Resource
    private SecurityUserTService securityUserTService;

    @Resource
    private SecurityUnitTService securityUnitTService;

    @Resource
    private SecurityUserRoleRService securityUserRoleRService;

    @Resource
    private HttpSession session;

	/*========================================================业务控制==================================================================*/

    /**
     * 添加角色
     *
     * @param securityRoleT 角色实体
     * @return 成功失败标识
     * @
     */
    @LogContent("添加角色")
    @RequestMapping(value = "/addRole", method = RequestMethod.POST)
    @ResponseBody
    public HttpResultEntity<?> addRole(@RequestBody SecurityRoleT securityRoleT) {
        securityRoleTService.add((SecurityUserT) SessionUtils.getAttr(request, SessionUtils.SESSION_MANAGER),
                securityRoleT, securityRoleT.getMenuTs());
        return getSuccessResult();
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return 成功失败标识
     * @
     */
    @LogContent("删除角色")
    @RequestMapping(value = "/{roleId}", method = RequestMethod.DELETE)
    @ResponseBody
    public HttpResultEntity<?> delete(@PathVariable String roleId) {
        securityRoleTService.delete(roleId);
        return getSuccessResult();
    }

    /**
     * 删除角色检查，如果该角色有用户在使用，那么给予提示
     *
     * @return
     * @
     */
    @LogContent("检查该角色是否有用户在使用")
    @RequestMapping(value = "/deleteCheck/{roleId}", method = RequestMethod.GET)
    @ResponseBody
    public HttpResultEntity<?> deleteCheck(@PathVariable String roleId) {
        SecurityUserRoleR securityUserRoleR = new SecurityUserRoleR();
        securityUserRoleR.setRoleId(roleId);
        List<SecurityUserRoleR> securityUserRoleRs = securityUserRoleRService.findAll(securityUserRoleR);
        if (securityUserRoleRs.size() > 0) {
            return getSuccessResult("该角色下包含".concat(((Integer) securityUserRoleRs.size()).toString()).concat
                    ("个用户在使用，是否继续？"));
        }
        return getSuccessResult("0");
    }

    /**
     * 编辑角色
     *
     * @param securityRoleT 角色实体
     * @return HttpResultEntity<?> 成功失败的标识
     * @
     */
    @LogContent("编辑角色")
    @RequestMapping(value = "/editRole", method = RequestMethod.PUT)
    @ResponseBody
    public HttpResultEntity<?> editRole(@RequestBody SecurityRoleT securityRoleT) {
        securityRoleTService.editRole(securityRoleT, securityRoleT.getMenuTs());
        return getSuccessResult();
    }

    /**
     * 角色信息查询，如果登录用户级别为“系统级”那么返回所有角色信息；如果登录用户为“用户级”，那么查询该用户所属单位下定义的角色信息
     *
     * @param securityRoleT 查询条件
     * @param queryHandle   分页信息
     * @return QueryResult<SecurityRoleT> 角色的信息列表，及分页信息
     * @
     * @modify 2015-10-23
     * @desc 1、如果当前登录系统人员所属角色为“超级管理员”，那么能够看到系统中所有已定义的角色 2、如果当前登录用户所属的角色为“系统角色”，
     * 那么能看到指定给他的所有“系统角色”及其所在单位下所有角色。
     * @author baoxiaobing@lvyushequ.com
     */
    @LogContent("查询角色")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpResultEntity<?> list(SecurityRoleT securityRoleT, QueryHandle queryHandle) {
        //获取当前登录用户的类型
        String belongUnit = ((SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER)).getBelongOrg();
        String userId = ((SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER)).getUserId();
        //如果当前登录用户所属角色不是系统超级管理员
        if (!securityUserTService.isSysRole(((SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER))
                .getUserId(), SysVarEnum.ADMIN_ROLETYPE.getCode())) {

            return getSuccessResult(setAuthorityBt(securityRoleTService.getData(securityRoleT, queryHandle
                    .configPage().addOrderBy("createDateTime",
                            "desc").addSqlJoinHandle("uv.unitName as belongUnitName", SQLJoinHandle.JoinType.LEFT_OUTER_JOIN,
                            "SecurityUnitT uv on t" + ".belongUnit = uv.unitId").addSqlJoinHandle(null, SQLJoinHandle
                            .JoinType.LEFT_OUTER_JOIN, "SecurityUserRoleR rr ON t.roleId = rr.roleId AND rr.userId = " +
                            "'" + userId + "'").addWhereSql("(rr" +
                            ".roleId is NOT NULL or t.belongUnit = '" + belongUnit +
                            "')" + (securityRoleT.getRoleName() == null ? "" : " and t.roleName like " + "'%" + securityRoleT.getRoleName() + "%'")
                    ))));

        }
        //如果是系统超级管理员，那么查询所有角色
        if (securityRoleT.getRoleName() != null) {
            return getSuccessResult(setAuthorityBt(securityRoleTService.getData(securityRoleT, queryHandle.configPage()
                    .addOrderBy("createDateTime",
                            "desc").addSqlJoinHandle("uv.unitName as belongUnitName", SQLJoinHandle.JoinType.LEFT_OUTER_JOIN,
                            "SecurityUnitT uv on t" + ".belongUnit = uv.unitId").addWhereSql("t.roleName like " +
                            "'%" + securityRoleT.getRoleName() + "%'"))));
        }
        return getSuccessResult(setAuthorityBt(securityRoleTService.getData(securityRoleT, queryHandle.configPage()
                .addOrderBy("createDateTime",
                        "desc").addSqlJoinHandle("uv.unitName as belongUnitName", SQLJoinHandle.JoinType.LEFT_OUTER_JOIN,
                        "SecurityUnitT uv on t" + ".belongUnit = uv.unitId"))));

    }

    /**
     * 角色信息查询，如果登录用户级别为“系统级”那么返回所有角色信息；如果登录用户为“用户级”，那么查询该用户所属单位下定义的角色信息
     *
     * @param securityRoleT 查询条件
     * @param queryHandle   分页信息
     * @return QueryResult<SecurityRoleT> 角色的信息列表，及分页信息
     * @
     * @modify 2015-10-23
     * @desc 1、如果当前登录系统人员所属角色为“超级管理员”，那么能够看到系统中所有已定义的角色 2、如果当前登录用户所属的角色为“系统角色”，
     * 那么能看到指定给他的所有“系统角色”及其所在单位下所有角色。
     *
     * 用户看到的角色是 分配给当前登陆人的角色+所点击人员所属公司具备的角色
     * @author baoxiaobing@lvyushequ.com
     */
    @LogContent("查询角色")
    @RequestMapping(value = "/listForGuid/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public HttpResultEntity<?> listForGuid(SecurityRoleT securityRoleT, QueryHandle queryHandle, @PathVariable String
            userId) {
        //获取当前登录用户的类型
        String currentUserId = ((SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER)).getUserId();

//        SecurityUnitT securityUnitT = securityUnitTService.getFathCompanyInfo(belongOrg);

        //如果当前登录用户所属角色不是系统超级管理员
        if (!securityUserTService.isSysRole(((SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER))
                .getUserId(), SysVarEnum.ADMIN_ROLETYPE.getCode())) {

            if (!("null".equals(userId))) {
                String belongOrg = securityUserTService.find(userId).getBelongOrg();
                queryHandle.addWhereSql("(rr.roleId is NOT NULL OR urr.resourceId IS NOT NULL or t.belongUnit = '" + belongOrg +
                        "')");
            }
            return getSuccessResult(new ChainMap<>().chainPut("data", setAuthorityBtForGuid(securityRoleTService
                    .findAll
                            (securityRoleT,
                                    queryHandle.configPage().addOrderBy("createDateTime", "desc")
                                            .addSqlJoinHandle("uv.unitName as belongUnitName", SQLJoinHandle.JoinType.LEFT_OUTER_JOIN, "SecurityUnitT uv on t" + ".belongUnit = uv.unitId")
                                            .addSqlJoinHandle("group_concat(mr.menuId) AS 'menuTs'", SQLJoinHandle
                                                    .JoinType.LEFT_OUTER_JOIN, "SecurityMenuRoleR mr ON t.roleId = mr.roleId")
                                            .addSqlJoinHandle(null, SQLJoinHandle.JoinType.LEFT_OUTER_JOIN, "SecurityUserRoleR rr ON t.roleId = rr.roleId AND rr.userId = '" + currentUserId + "'")
                                            .addSqlJoinHandle(null,SQLJoinHandle.JoinType.LEFT_OUTER_JOIN,
                                                    "SecurityUserResourceR urr ON t.roleId = urr.resourceId " +
                                                    "AND urr.userId = '" + currentUserId + "' " +
                                                    "AND urr.resourceType = 'role'")
                                            .addGroupBy("t.roleId")))));

        }
//        if (!"null".equals(userId)) {
//            String belongOrg = securityUserTService.find(userId).getBelongOrg();
//            queryHandle.customWhereSql("t.roleType IN('1','0','2') OR t" +
//                    ".belongUnit = '" + belongOrg + "'");
//        }
        return getSuccessResult(new ChainMap<>().chainPut("data", setAuthorityBtForGuid(securityRoleTService
                .findAll
                        (securityRoleT,
                                queryHandle.configPage().addOrderBy("createDateTime", "desc").addGroupBy("t.roleId")
                                        .addSqlJoinHandle("uv.unitName as belongUnitName", SQLJoinHandle.JoinType.LEFT_OUTER_JOIN,
                                                "SecurityUnitT uv on t" + ".belongUnit = uv.unitId").addSqlJoinHandle("group_concat" +
                                        "(mr.menuId) AS 'menuTs'", SQLJoinHandle.JoinType.LEFT_OUTER_JOIN, "SecurityMenuRoleR mr ON" +
                                        " t.roleId" +
                                        " = mr.roleId ")))));


    }

    /**
     * 根据用户ID查询其具备的角色
     *
     * @param userId
     * @return
     * @
     */
    @LogContent("根据组织机构ID查询下属角色信息")
    @RequestMapping(value = "/queryRoleByUserIdSelected/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public HttpResultEntity<?> queryRoleByUserIdSelected(@PathVariable String userId) {
        return getSuccessResult(securityRoleTService.queryRoleByUserIdSelected(userId));
    }


    /**
     * 获取用户修改页面的所属单位选择数据
     * 与老吴沟通结果：
     * 获取当前登录用户的级别
     * 1.如果为用户级，那么直接到session中获取该用户的所属单位信息，即只能新增本单位的用户
     * 2.如果为系统级，那么获取系统中所有的组织机构信息，并根据被修改用的ID获取选中的组织机构
     *
     * @param roleId 角色ID
     * @return
     * @
     */
    @LogContent("根据用户ID查询组织机构数据")
    @RequestMapping(value = "/queryUnitByRoleId/{roleId}", method = RequestMethod.GET)
    @ResponseBody
    public HttpResultEntity<?> queryUnitByRoleId(@PathVariable String roleId) {
        return getSuccessResult(securityRoleTService.findUnitListAll((SecurityUserT) session.getAttribute(SessionUtils
                .SESSION_MANAGER), roleId));
    }

    @LogContent("判断当前用户能否修改该角色")
    @RequestMapping("/checkIsCanModify")
    @ResponseBody
    public HttpResultEntity<?> checkIsCanModify(String roleType) {

        if (!(securityUserTService.isSysRole(((SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER))
                .getUserId(), SysVarEnum.ADMIN_ROLETYPE.getCode())) && SysVarEnum.SYSTEM_ROLETYPE.getCode().equals(roleType)) {
            return getSuccessResult("error");
        }
        return getSuccessResult("success");
    }


    /**
     * 设置每一条角色信息的按钮权限信息
     * 如果当前登录用户所属角色为 系统超级管理员角色，那么具备所有角色的编辑权限
     * 如果当前登录用户不是系统超级管理员角色，那么判断当前角色类型，“系统角色”：不可编辑；“用户角色”：可编辑
     * 如果当前登录用户所属角色为普通员工，那么没有权限
     * 权限为
     *
     * @return
     * @
     */
    private QueryResult<SecurityRoleT> setAuthorityBt(QueryResult<SecurityRoleT> roles) {
        //如果是超级管理员角色
        for (int i = 0; i < roles.getData().size(); i++) {
            String roleCode;
            if (securityUserTService.isSysRole(((SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER))
                    .getUserId(), SysVarEnum.ADMIN_ROLETYPE.getCode())) {
            /*设置所有角色可编辑*/
                roleCode = SysVarEnum.YES_STATICVAR.getCode();

            } else if (securityUserTService.isSysRole(((SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER))
                    .getUserId(), SysVarEnum.SYSTEM_ROLETYPE.getCode())) {     //如果是系统管理员角色
                roleCode = SysVarEnum.SYSTEM_ROLETYPE.getCode().equals(roles.getData().get(i).getRoleType())
                        ? SysVarEnum.NO_STATICVAR.getCode() : SysVarEnum.YES_STATICVAR.getCode();

            } else {
                roleCode = SysVarEnum.NO_STATICVAR.getCode();
            }
            roles.getData().get(i).setBtrow(roleCode);
        }
        return roles;
    }

    /**
     * 设置每一条角色信息的按钮权限信息
     * 如果当前登录用户所属角色为 系统超级管理员角色，那么具备所有角色的编辑权限
     * 如果当前登录用户不是系统超级管理员角色，那么判断当前角色类型，“系统角色”：不可编辑；“用户角色”：可编辑
     * 如果当前登录用户所属角色为普通员工，那么没有权限
     *
     * @return
     * @
     */
    private List<SecurityRoleT> setAuthorityBtForGuid(List<SecurityRoleT> roles) {
        if (securityUserTService.isSysRole(((SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER))
                .getUserId(), SysVarEnum.ADMIN_ROLETYPE.getCode())) {
            /*设置所有角色可编辑*/
            for (int i = 0; i < roles.size(); i++) {
                roles.get(i).setBtrow(SysVarEnum.YES_STATICVAR.getCode());
            }
        } else if (securityUserTService.isSysRole(((SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER))
                .getUserId(), SysVarEnum.SYSTEM_ROLETYPE.getCode())) {
            for (int i = 0; i < roles.size(); i++) {
                if (SysVarEnum.SYSTEM_ROLETYPE.getCode().equals(roles.get(i).getRoleType())) {
                    roles.get(i).setBtrow(SysVarEnum.NO_STATICVAR.getCode());
                } else if (SysVarEnum.USER_ROLETYPE.getCode().equals(roles.get(i).getRoleType())) {
                    roles.get(i).setBtrow(SysVarEnum.YES_STATICVAR.getCode());
                }
            }
        } else {
            for (int i = 0; i < roles.size(); i++) {
                roles.get(i).setBtrow(SysVarEnum.NO_STATICVAR.getCode());
            }
        }
        return roles;
    }

//    /**
//     * 角色名修改为 所属单位+角色名
//     * @param securityRoleTs
//     * @return
//     */
//    private List<SecurityRoleT> dataChange(List<SecurityRoleT> securityRoleTs) {
//        for (SecurityRoleT securityRoleT : securityRoleTs) {
//            securityRoleT.setRoleName((securityRoleT.getBelongUnitName() == null ? "系统角色" : securityRoleT
//                    .getBelongUnitName()) + "-" + securityRoleT
//                    .getRoleName());
//        }
//        return securityRoleTs;
//    }
}
