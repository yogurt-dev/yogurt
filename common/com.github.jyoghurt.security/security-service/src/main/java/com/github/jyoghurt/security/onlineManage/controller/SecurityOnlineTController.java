package com.github.jyoghurt.security.onlineManage.controller;

import com.github.jyoghurt.security.onlineManage.domain.SecurityOnlineT;
import com.github.jyoghurt.security.onlineManage.service.SecurityOnlineTService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 登录用户统计控制器
 *
 */
@RestController
@LogContent(module = "登录用户统计")
@RequestMapping("/securityOnlineT")
public class SecurityOnlineTController extends BaseController {


	/**
	 * 登录用户统计服务类
	 */
	@Resource
	private SecurityOnlineTService securityOnlineTService;

	/**
	 * 列出登录用户统计
	 */
	@LogContent("查询登录用户统计")
	@RequestMapping(value = "/list",method=RequestMethod.GET)
	public HttpResultEntity<?> list(SecurityOnlineT securityOnlineT , QueryHandle queryHandle)   {
        return getSuccessResult(securityOnlineTService.getOnlineUsers());
	}
//
//
//	/**
//	 * 添加登录用户统计
//	 */
//	@LogContent("添加登录用户统计")
//	@RequestMapping(method=RequestMethod.POST)
//	public HttpResultEntity<?> add(@RequestBody SecurityOnlineT securityOnlineT)   {
//		securityOnlineTService.save(securityOnlineT);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 编辑登录用户统计
//	 */
//	@LogContent("编辑登录用户统计")
//	@RequestMapping(method=RequestMethod.PUT)
//	public HttpResultEntity<?> edit(@RequestBody SecurityOnlineT securityOnlineT)   {
//		securityOnlineTService.updateForSelective(securityOnlineT);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 删除单个登录用户统计
//	 */
//	@LogContent("删除登录用户统计")
//	@RequestMapping(value = "/{uuid}",method=RequestMethod.DELETE)
//	public HttpResultEntity<?> delete(@PathVariable String uuid)   {
//		securityOnlineTService.delete(uuid);
//		return getSuccessResult();
//	}
//
//    /**
//     * 查询单个登录用户统计
//	 */
//	 @LogContent("查询单个登录用户统计")
//	 @RequestMapping(value = "/{uuid}",method=RequestMethod.GET)
//	 public HttpResultEntity<?> get(@PathVariable String uuid)   {
//		 return getSuccessResult(securityOnlineTService.find(uuid));
//	 }
}
