package com.github.jyoghurt.security.securityMenuRoleR.controller;

import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.security.securityMenuRoleR.service.SecurityMenuRoleRService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * 菜单角色关系控制器
 *
 */
@Controller
@LogContent(module = "菜单角色关系")
@RequestMapping("/service/securityMenuRoleR")
public class SecurityMenuRoleRController extends BaseController {


	/**
	 * 菜单角色关系服务类
	 */
	@Resource
	private SecurityMenuRoleRService securityMenuRoleRService;

//	/**
//	 * 列出菜单角色关系
//	 */
//	@LogContent("查询菜单角色关系")
//	@RequestMapping("/list")
//	@ResponseBody
//	public QueryResult<SecurityMenuRoleR> list(SecurityMenuRoleR securityMenuRoleR ,QueryHandle queryAssistor)   {
//        return securityMenuRoleRService.getData(securityMenuRoleR,queryAssistor.configPage().addOrderBy("createDateTime","desc"));
//
//	}
//
//
//	/**
//	 * 添加菜单角色关系
//	 */
//	@LogContent("添加菜单角色关系")
//	@RequestMapping("/add")
//	@ResponseBody
//	public HttpResultEntity<?> add(SecurityMenuRoleR securityMenuRoleR)   {
//		securityMenuRoleRService.save(securityMenuRoleR);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 编辑菜单角色关系
//	 */
//    @LogContent("编辑菜单角色关系")
//	@RequestMapping("/edit")
//    @ResponseBody
//	public HttpResultEntity<?> edit(SecurityMenuRoleR securityMenuRoleR)   {
//		securityMenuRoleRService.updateForSelective(securityMenuRoleR);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 删除单个菜单角色关系
//	 */
//	@LogContent("删除菜单角色关系")
//	@RequestMapping("/delete")
//	@ResponseBody
//	public HttpResultEntity<?> delete(Integer relId)   {
//		securityMenuRoleRService.delete(relId);
//		return getSuccessResult();
//	}
}
