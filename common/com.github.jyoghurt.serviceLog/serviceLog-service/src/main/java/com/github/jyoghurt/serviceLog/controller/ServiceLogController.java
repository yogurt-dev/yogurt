package com.github.jyoghurt.serviceLog.controller;


import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.serviceLog.domain.ServiceLogT;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.github.jyoghurt.serviceLog.service.ServiceLogService;

import javax.annotation.Resource;


/**
 * 订单接口控制器
 *
 */
@RestController
@LogContent(module = "订单接口")
@RequestMapping("/serviceLog")
public class ServiceLogController extends BaseController {


	/**
	 * 订单接口服务类
	 */
	@Resource
	private ServiceLogService serviceLogService;

	/**
	 * 列出订单接口
	 */
	@LogContent("查询订单接口")
	@RequestMapping(value = "/list",method= RequestMethod.GET)
	public HttpResultEntity<?> list(ServiceLogT serviceLogT , QueryHandle queryHandle)   {
        return getSuccessResult(serviceLogService.getData(serviceLogT.setDeleteFlag(false),queryHandle.configPage().addOrderBy("createDateTime",
				"desc")));

	}
//
//
//	/**
//	 * 添加订单接口
//	 */
//	@LogContent("添加订单接口")
//	@RequestMapping(method=RequestMethod.POST)
//	public HttpResultEntity<?> add(@RequestBody ServiceLogT serviceLogT)   {
//		serviceLogService.save(serviceLogT);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 编辑订单接口
//	 */
//	@LogContent("编辑订单接口")
//	@RequestMapping(method=RequestMethod.PUT)
//	public HttpResultEntity<?> edit(@RequestBody ServiceLogT serviceLogT)   {
//		serviceLogService.updateForSelective(serviceLogT);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 删除单个订单接口
//	 */
//	@LogContent("删除订单接口")
//	@RequestMapping(value = "/{${prikey}}",method=RequestMethod.DELETE)
//	public HttpResultEntity<?> logicDelete(@PathVariable String ${prikey})   {
//		serviceLogService.logicDelete(${prikey});
//		return getSuccessResult();
//	}
//
//    /**
//     * 查询单个订单接口
//	 */
//	 @LogContent("查询单个订单接口")
//	 @RequestMapping(value = "/{${prikey}}",method=RequestMethod.GET)
//	 public HttpResultEntity<?> get(@PathVariable String $prikey)   {
//		 return getSuccessResult(serviceLogService.find($prikey));
//	 }
}
