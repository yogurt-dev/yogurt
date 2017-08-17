package com.github.jyoghurt.security.securityMenuT.controller;

import com.github.jyoghurt.security.securityMenuT.domain.SecurityMenuT;
import com.github.jyoghurt.security.securityMenuT.service.SecurityMenuTService;
import com.github.jyoghurt.security.securityRoleT.domain.SecurityRoleT;
import com.github.jyoghurt.security.securityRoleT.service.SecurityRoleTService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.handle.SQLJoinHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.web.bind.annotation.*;
import pub.utils.SessionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 菜单控制器
 */
@RestController
@LogContent(module = "菜单管理")
@RequestMapping("/service/securityMenuT")
public class SecurityMenuTController extends BaseController {


    /**
     * 菜单服务类
     */
    @Resource
    private SecurityMenuTService securityMenuTService;

    @Resource
    private SecurityRoleTService securityRoleTService;

    @Resource
    private HttpSession session;

    /**
     * 获取当前登录用户的菜单内容
     *
     * @return
     * @Modify by baoxiaobing@lvyushequ.com
     * 1、修改返回内容结构，包含两部分。当前登录人的菜单；包含提醒的菜单
     */
    @LogContent("用户获取菜单")
    @RequestMapping(value = "/getMenu", method = RequestMethod.GET)
    public HttpResultEntity<?> getUserMenu()   {
        return getSuccessResult(securityMenuTService.getUserMenu(session));
    }

    /**
     * 列出菜单
     */
    @LogContent("查询菜单")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpResultEntity<?> list(SecurityMenuT securityMenuT, QueryHandle queryHandle)   {
        if (securityMenuT.getMenuName() != null) {
            return getSuccessResult(securityMenuTService.getData(securityMenuT, queryHandle.configPage().search().addOrderBy
                    ("sortId", "asc").addWhereSql("t.menuName like '%" + securityMenuT.getMenuName() + "%'" + "and t" +
                    ".menuId<>'-1'")));
        }
        return getSuccessResult(securityMenuTService.getData(securityMenuT, queryHandle.configPage().search().addOrderBy
                ("sortId", "asc").addWhereSql("t.menuId<>'-1'")));
    }

    /**
     * 根据角色ID查询所属菜单
     */
    @LogContent("根据角色ID查询所属菜单")
    @RequestMapping(value = "/queryMenuByRoleId/{roleId}", method = RequestMethod.GET)
    @ResponseBody
    public HttpResultEntity<?> queryMenuByRoleId(@PathVariable String roleId)   {
        SecurityUserT user = (SecurityUserT) session.getAttribute
                (SessionUtils.SESSION_MANAGER);

        if ("admin".equals(user.getUserAccount())) {
            return getSuccessResult(securityMenuTService.queryMenuByRoleIdAdmin(roleId));
        }

        List<SecurityRoleT> roles = securityRoleTService.findAllByUserId(user.getUserId());
        String name_str = "";
        for (int i = 0; i < roles.size(); i++) {

            if (i > 0 || i < roles.size()) {
                name_str += ",";
            }
            name_str += roles.get(i).getRoleId();

        }

        return getSuccessResult(securityMenuTService.queryMenuByRoleId(name_str, roleId));
    }

    /**
     * 查询菜单字典
     *
     * @return
     * @
     */
    @LogContent("查询菜单字典")
    @RequestMapping(value = "/queryMenuDic", method = RequestMethod.GET)
    @ResponseBody
    public HttpResultEntity<?> queryMenuDic()   {
        return getSuccessResult(securityMenuTService.findAll(new SecurityMenuT(), new QueryHandle().addSqlJoinHandle
                ("parent" +
                        ".menuName as parentName", SQLJoinHandle.JoinType.LEFT_OUTER_JOIN, "SecurityMenuT parent on t" +
                        ".parentId = parent.menuId")));
    }


    /**
     * 添加菜单
     */
    @LogContent("添加菜单")
    @RequestMapping(method = RequestMethod.POST)
    public HttpResultEntity<?> add(@RequestBody SecurityMenuT securityMenuT)   {
        securityMenuTService.save(securityMenuT);
        return getSuccessResult();
    }

    /**
     * 编辑菜单
     */
    @LogContent("编辑菜单")
    @RequestMapping(method = RequestMethod.PUT)
    public HttpResultEntity<?> edit(@RequestBody SecurityMenuT securityMenuT)   {
        securityMenuTService.updateForSelective(securityMenuT);
        return getSuccessResult();
    }

    /**
     * 删除单个菜单
     */
    @LogContent("删除菜单")
    @RequestMapping(value = "/{menuId}", method = RequestMethod.DELETE)
    @ResponseBody
    public HttpResultEntity<?> delete(@PathVariable String menuId)   {
        securityMenuTService.delete(menuId);
        return getSuccessResult();
    }
}
