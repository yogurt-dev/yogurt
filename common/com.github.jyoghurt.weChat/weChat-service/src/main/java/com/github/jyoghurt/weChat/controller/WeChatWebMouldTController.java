package com.github.jyoghurt.weChat.controller;

import com.github.jyoghurt.weChat.service.WeChatWebMouldTService;
import com.github.jyoghurt.core.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * WebMouldT控制器
 *
 */
@RestController
@RequestMapping("/weChatWebMouldT")
public class WeChatWebMouldTController extends BaseController {


	/**
	 * WebMouldT服务类
	 */
	@Resource
	private WeChatWebMouldTService weChatWebMouldTService;
//
//	/**
//	 * 列出WebMouldT
//	 */
//	@LogContent("查询WebMouldT")
//	@RequestMapping(value = "/list",method=RequestMethod.GET)
//	public QueryResult<WeChatWebMouldT> list(WeChatWebMouldT webMouldT ,QueryHandle queryAssistor)   {
//        return weChatWebMouldTService.getData(webMouldT,queryAssistor.configPage().addOrderBy("createDateTime",
//				"desc"));
//
//	}
//
//
//	/**
//	 * 添加WebMouldT
//	 */
//	@LogContent("添加WebMouldT")
//	@RequestMapping(method=RequestMethod.POST)
//	public HttpResultEntity<?> add(@RequestBody WeChatWebMouldT webMouldT)   {
//		weChatWebMouldTService.save(webMouldT);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 编辑WebMouldT
//	 */
//	@LogContent("编辑WebMouldT")
//	@RequestMapping(method=RequestMethod.PUT)
//	public HttpResultEntity<?> edit(@RequestBody WeChatWebMouldT webMouldT)   {
//		weChatWebMouldTService.updateForSelective(webMouldT);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 删除单个WebMouldT
//	 */
//	@LogContent("删除WebMouldT")
//	@RequestMapping(value = "/{mouldId}",method=RequestMethod.DELETE)
//	public HttpResultEntity<?> delete(@PathVariable String mouldId)   {
//		weChatWebMouldTService.delete(mouldId);
//		return getSuccessResult();
//	}
//
//    /**
//     * 查询单个WebMouldT
//	 */
//	 @LogContent("查询单个WebMouldT")
//	 @RequestMapping(value = "/{mouldId}",method=RequestMethod.GET)
//	 public HttpResultEntity<?> get(@PathVariable String mouldId)   {
//		 return getSuccessResult(weChatWebMouldTService.find(mouldId));
//	 }


}
