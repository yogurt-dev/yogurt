package com.github.jyoghurt.security.SecurityRoleButtonR.controller;

import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.security.SecurityRoleButtonR.domain.SecurityRoleButtonR;
import com.github.jyoghurt.security.SecurityRoleButtonR.service.SecurityRoleButtonRService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import org.springframework.web.bind.annotation.*;
import pub.utils.SessionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * SecurityRoleButtonR控制器
 *
 */
@RestController
@LogContent(module = "角色按钮管理")
@RequestMapping("/securityRoleButtonR")
public class SecurityRoleButtonRController extends BaseController {


	/**
	 * SecurityRoleButtonR服务类
	 */
	@Resource
	private SecurityRoleButtonRService securityRoleButtonRService;

	/**
	 * 列出SecurityRoleButtonR
	 */
	@LogContent("查询SecurityRoleButtonR")
	@RequestMapping(value = "/list",method= RequestMethod.GET)
	public HttpResultEntity<?> list(SecurityRoleButtonR securityRoleButtonR ,QueryHandle queryHandle) throws
			Exception {
        return getSuccessResult(securityRoleButtonRService.getData(securityRoleButtonR, queryHandle.configPage().addOrderBy
				("createDateTime", "desc")));
	}


	/**
	 * 添加SecurityRoleButtonR
	 */
	@LogContent("添加SecurityRoleButtonR")
	@RequestMapping(method=RequestMethod.POST)
	public HttpResultEntity<?> add(@RequestBody SecurityRoleButtonR securityRoleButtonR)   {
		securityRoleButtonRService.save(securityRoleButtonR);
        return getSuccessResult();
	}

	/**
	 * 编辑SecurityRoleButtonR
	 */
	@LogContent("编辑SecurityRoleButtonR")
	@RequestMapping(method=RequestMethod.PUT)
	public HttpResultEntity<?> edit(@RequestBody SecurityRoleButtonR securityRoleButtonR)   {
		securityRoleButtonRService.updateForSelective(securityRoleButtonR);
        return getSuccessResult();
	}

	/**
	 * 删除单个SecurityRoleButtonR
	 */
	@LogContent("删除SecurityRoleButtonR")
	@RequestMapping(value = "/{mrbId}",method=RequestMethod.DELETE)
	public HttpResultEntity<?> delete(@PathVariable String mrbId)   {
		securityRoleButtonRService.delete(mrbId);
		return getSuccessResult();
	}

    /**
     * 查询单个SecurityRoleButtonR
	 */
	 @LogContent("查询单个SecurityRoleButtonR")
	 @RequestMapping(value = "/{mrbId}",method=RequestMethod.GET)
	 public HttpResultEntity<?> get(@PathVariable String mrbId)   {
		 return getSuccessResult(securityRoleButtonRService.find(mrbId));
	 }
	/**
	 * 根据当前用户获得按钮权限
	 */
	@LogContent("查询单个SecurityRoleButtonR")
	@RequestMapping(value = "/getButtonByUserId",method=RequestMethod.GET)
	public HttpResultEntity<?> getButtonByUserId()   {
		SecurityUserT usert = (SecurityUserT)session.getAttribute(SessionUtils.SESSION_MANAGER);

		List<String> list = securityRoleButtonRService.getButtonByUserId(usert.getUserId());
		if (list.size()>0){

			return getSuccessResult(list);
		}else{
			return getSuccessResult();
		}


	}
}
