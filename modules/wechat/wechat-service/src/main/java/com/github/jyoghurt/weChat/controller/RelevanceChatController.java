package com.github.jyoghurt.weChat.controller;

import com.github.jyoghurt.weChat.service.RelevanceChatService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 对话窗口控制器
 *
 */
@RestController
@LogContent(module = "对话窗口关系")
@RequestMapping("/relevanceChat")
public class RelevanceChatController extends BaseController {
	/**
	 * 对话窗口服务类
	 */
	@Resource
	private RelevanceChatService relevanceChatService;
	/**
	 * 列出对话窗口
	 */
	@LogContent("查询对话窗口关系")
	@RequestMapping(value = "/findGroup/{appId}/{appIdF}/{appIdQ}",method= RequestMethod.GET)
	public HttpResultEntity<?> list(@PathVariable String appId, @PathVariable String appIdF, @PathVariable String
			appIdQ)   {
        return getSuccessResult(relevanceChatService.findGroup(appId,appIdF,appIdQ));

	}
}
