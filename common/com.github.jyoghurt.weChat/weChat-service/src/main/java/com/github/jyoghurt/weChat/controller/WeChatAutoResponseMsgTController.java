package com.github.jyoghurt.weChat.controller;

import com.github.jyoghurt.weChat.service.WeChatAutoResponseMsgTService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 微信自动回复消息控制器
 *
 */
@RestController
@LogContent(module = "微信自动回复消息")
@RequestMapping("/weChatAutoResponseMsgT")
public class WeChatAutoResponseMsgTController extends BaseController {


	/**
	 * 微信自动回复消息服务类
	 */
	@Resource
	private WeChatAutoResponseMsgTService weChatAutoResponseMsgTService;

//	/**
//	 * 列出微信自动回复消息
//	 */
//	@LogContent("查询微信自动回复消息")
//	@RequestMapping(value = "/list",method=RequestMethod.GET)
//	public HttpResultEntity<?> list(WeChatAutoResponseMsgT weChatAutoResponseMsgT ,QueryHandle queryHandle)   {
//        return getSuccessResult(weChatAutoResponseMsgTService.getData(weChatAutoResponseMsgT,queryHandle.configPage().addOrderBy("createDateTime",
//				"desc")));
//
//	}
//
//
//	/**
//	 * 添加微信自动回复消息
//	 */
//	@LogContent("添加微信自动回复消息")
//	@RequestMapping(method=RequestMethod.POST)
//	public HttpResultEntity<?> add(@RequestBody WeChatAutoResponseMsgT weChatAutoResponseMsgT)   {
//		weChatAutoResponseMsgTService.save(weChatAutoResponseMsgT);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 编辑微信自动回复消息
//	 */
//	@LogContent("编辑微信自动回复消息")
//	@RequestMapping(method=RequestMethod.PUT)
//	public HttpResultEntity<?> edit(@RequestBody WeChatAutoResponseMsgT weChatAutoResponseMsgT)   {
//		weChatAutoResponseMsgTService.updateForSelective(weChatAutoResponseMsgT);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 删除单个微信自动回复消息
//	 */
//	@LogContent("删除微信自动回复消息")
//	@RequestMapping(value = "/{msgId}",method=RequestMethod.DELETE)
//	public HttpResultEntity<?> delete(@PathVariable String msgId)   {
//		weChatAutoResponseMsgTService.delete(msgId);
//		return getSuccessResult();
//	}
//
//    /**
//     * 查询单个微信自动回复消息
//	 */
	 @LogContent("查询单个微信自动回复消息")
	 @RequestMapping(value = "/{msgId}",method= RequestMethod.GET)
	 public HttpResultEntity<?> get(@PathVariable String msgId)   {
		 return getSuccessResult(weChatAutoResponseMsgTService.find(msgId));
	 }
}
