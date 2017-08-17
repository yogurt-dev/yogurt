package com.github.jyoghurt.security.SecurityButtonT.controller;

import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.security.SecurityButtonT.domain.SecurityButtonT;
import com.github.jyoghurt.security.SecurityButtonT.service.SecurityButtonTService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * SecurityButtonT控制器
 *
 */
@RestController
@LogContent(module = "SecurityButtonT")
@RequestMapping("/securityButtonT")
public class SecurityButtonTController extends BaseController {


	/**
	 * SecurityButtonT服务类
	 */
	@Resource
	private SecurityButtonTService securityButtonTService;

	/**
	 * 列出SecurityButtonT
	 */
	@LogContent("查询SecurityButtonT")
	@RequestMapping(value = "/list",method= RequestMethod.GET)
	public HttpResultEntity<?> list(SecurityButtonT securityButtonT ,QueryHandle queryHandle)   {
        return getSuccessResult(securityButtonTService.getData(securityButtonT, queryHandle.configPage().addOrderBy
				("createDateTime", "desc")));

	}


	/**
	 * 添加SecurityButtonT
	 */
	@LogContent("添加SecurityButtonT")
	@RequestMapping(method=RequestMethod.POST)
	public HttpResultEntity<?> add(@RequestBody SecurityButtonT securityButtonT)   {
		securityButtonTService.save(securityButtonT);
        return getSuccessResult();
	}

	/**
	 * 编辑SecurityButtonT
	 */
	@LogContent("编辑SecurityButtonT")
	@RequestMapping(method=RequestMethod.PUT)
	public HttpResultEntity<?> edit(@RequestBody SecurityButtonT securityButtonT)   {
		securityButtonTService.updateForSelective(securityButtonT);
        return getSuccessResult();
	}

	/**
	 * 删除单个SecurityButtonT
	 */
	@LogContent("删除SecurityButtonT")
	@RequestMapping(value = "/{buttonId}",method=RequestMethod.DELETE)
	public HttpResultEntity<?> delete(@PathVariable String buttonId)   {
		securityButtonTService.delete(buttonId);
		return getSuccessResult();
	}

    /**
     * 查询单个SecurityButtonT
	 */
	 @LogContent("查询单个SecurityButtonT")
	 @RequestMapping(value = "/{buttonId}",method=RequestMethod.GET)
	 public HttpResultEntity<?> get(@PathVariable String buttonId)   {
		 return getSuccessResult(securityButtonTService.find(buttonId));
	 }

	/**
	 * 根据功能查询按钮列表
	 */
	@LogContent("根据功能查询按钮列表")
	@RequestMapping(value = "/{menuId}",method=RequestMethod.GET)
	public List<SecurityButtonT> getButtonByMenuId(@PathVariable String menuId)   {
		List<SecurityButtonT> list = securityButtonTService.getButtonByMenuId(menuId);
		return list;

	}
}
