package com.github.jyoghurt.security.securityUserRoleR.controller;

import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.security.securityUserRoleR.service.SecurityUserRoleRService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * 菜单控制器
 */
@Controller
@LogContent(module = "用户角色管理")
@RequestMapping("/service/securityUserRoleR")
public class SecurityUserRoleRController extends BaseController {


    /**
     * 菜单服务类
     */
    @Resource
    private SecurityUserRoleRService securityUserRoleRService;

//	/**
//	 * 列出菜单
//	 */
//	@LogContent("查询菜单")
//	@RequestMapping("/list")
//	@ResponseBody
//	public QueryResult<SecurityUserRoleR> list(SecurityUserRoleR securityUserRoleR ,QueryHandle queryAssistor)   {
//        return securityUserRoleRService.getData(securityUserRoleR,queryAssistor.configPage().addOrderBy("createDateTime","desc"));
//
//	}
//
//
//	/**
//	 * 添加菜单
//	 */
//	@LogContent("添加菜单")
//	@RequestMapping("/add")
//	@ResponseBody
//	public HttpResultEntity<?> add(SecurityUserRoleR securityUserRoleR)   {
//		securityUserRoleRService.save(securityUserRoleR);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 编辑菜单
//	 */
//    @LogContent("编辑菜单")
//	@RequestMapping("/edit")
//    @ResponseBody
//	public HttpResultEntity<?> edit(SecurityUserRoleR securityUserRoleR)   {
//		securityUserRoleRService.updateForSelective(securityUserRoleR);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 删除单个菜单
//	 */
//	@LogContent("删除菜单")
//	@RequestMapping("/delete")
//	@ResponseBody
//	public HttpResultEntity<?> delete(Integer relId)   {
//		securityUserRoleRService.delete(relId);
//		return getSuccessResult();
//	}
}
