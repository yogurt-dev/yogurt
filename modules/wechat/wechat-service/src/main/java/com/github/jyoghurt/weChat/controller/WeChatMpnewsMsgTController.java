package com.github.jyoghurt.weChat.controller;

import com.github.jyoghurt.weChat.domain.WeChatMpnewsMsgT;
import com.github.jyoghurt.weChat.service.WeChatMpnewsMsgTService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.result.QueryResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 微信消息记录表控制器
 *
 */
@RestController
@RequestMapping("/weChatMpnewsMsgT")
public class WeChatMpnewsMsgTController extends BaseController {


	/**
	 * 微信消息记录表服务类
	 */
	@Resource
	private WeChatMpnewsMsgTService weChatMpnewsMsgTService;
//
//	/**
//	 * 列出微信消息记录表
//	 */
//	@LogContent("查询微信消息记录表")
//	@RequestMapping(value = "/list",method=RequestMethod.GET)
//	public QueryResult<WeChatMpnewsMsgT> list(WeChatMpnewsMsgT weChatMpnewsMsgT ,QueryHandle queryAssistor)   {
//        return weChatMpnewsMsgTService.getData(weChatMpnewsMsgT,queryAssistor.configPage().addOrderBy("createDateTime",
//				"desc"));
//
//	}
//
//
//	/**
//	 * 添加微信消息记录表
//	 */
//	@LogContent("添加微信消息记录表")
//	@RequestMapping(method=RequestMethod.POST)
//	public HttpResultEntity<?> add(WeChatMpnewsMsgT weChatMpnewsMsgT)   {
//		weChatMpnewsMsgTService.save(weChatMpnewsMsgT);
//        return getSuccessResult();
//	}
//
	/**
	 * 编辑微信消息记录表
	 */
	@LogContent("编辑微信消息记录表")
	@RequestMapping(method= RequestMethod.PUT)
	public HttpResultEntity<?> edit(@RequestBody WeChatMpnewsMsgT weChatMpnewsMsgT)   {
		weChatMpnewsMsgTService.updateForSelective(weChatMpnewsMsgT);
        return getSuccessResult();
	}
//
	/**
	 * 删除单个微信消息记录表
	 */
	@LogContent("删除微信消息记录表")
	@RequestMapping(value = "/{Id}",method=RequestMethod.DELETE)
	public HttpResultEntity<?> delete(@PathVariable String Id)   {
		weChatMpnewsMsgTService.delete(Id);
		//session.getAttribute(SessionUtils.SESSIO)
		return getSuccessResult();
	}
	/**
	 * 查询单个微信消息记录表
	 */
	@LogContent("根据图文消息id查询子消息列表")
	@RequestMapping(value = "getListByMessageId/{messageId}",method=RequestMethod.GET)
	public HttpResultEntity<?> getListByMessageId(@PathVariable String messageId)   {
		QueryResult<WeChatMpnewsMsgT> queryResult = new QueryResult<WeChatMpnewsMsgT>();
		queryResult.setData(weChatMpnewsMsgTService.getListByMessageId(messageId));
		return getSuccessResult(queryResult);
	}
    /**
     * 查询单个微信消息记录表
	 */
	 @LogContent("查询单个微信消息记录表")
	 @RequestMapping(value = "/{Id}",method=RequestMethod.GET)
	 public HttpResultEntity<?> get(@PathVariable String Id)   {

		 return getSuccessResult(weChatMpnewsMsgTService.find(Id));

	 }

}
