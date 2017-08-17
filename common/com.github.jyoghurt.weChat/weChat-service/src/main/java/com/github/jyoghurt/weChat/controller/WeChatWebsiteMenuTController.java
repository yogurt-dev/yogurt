package com.github.jyoghurt.weChat.controller;

import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.weChat.domain.WeChatWebsiteMenuT;
import com.github.jyoghurt.weChat.domain.WeChatWebsiteT;
import com.github.jyoghurt.weChat.service.WeChatWebsiteMenuTService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.web.bind.annotation.*;
import pub.utils.SessionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * WebsiteMenuT控制器
 *
 */
@RestController
@RequestMapping("/weChatWebsiteMenuT")
public class WeChatWebsiteMenuTController extends BaseController {


	/**
	 * WebsiteMenuT服务类
	 */
	@Resource
	private WeChatWebsiteMenuTService weChatWebsiteMenuTService;
//
//	/**
//	 * 列出WebsiteMenuT
//	 */
//	@LogContent("查询WebsiteMenuT")
//	@RequestMapping(value = "/list",method=RequestMethod.GET)
//	public QueryResult<WeChatWebsiteMenuT> list(WeChatWebsiteMenuT weChatWebsiteMenuT ,QueryHandle queryAssistor)   {
//        return weChatWebsiteMenuTService.getData(weChatWebsiteMenuT,queryAssistor.configPage().addOrderBy("createDateTime",
//				"desc"));
//
//	}
		/**
	 * 列出WebsiteMenuT
	 */
	@LogContent("查询WebsiteMenuT")
	@RequestMapping(value = "/getList/{parentId}",method=RequestMethod.GET)
	public List<WeChatWebsiteMenuT> list(@PathVariable String parentId)   {
		List<WeChatWebsiteMenuT> list = weChatWebsiteMenuTService.findByParentId(parentId);
		return list;

	}


	/**
	 * 添加WebsiteMenuT
	 */
	@LogContent("添加WebsiteMenuT")
	@RequestMapping(method=RequestMethod.POST)
	public HttpResultEntity<?> add(@RequestBody WeChatWebsiteMenuT weChatWebsiteMenuT)   {
		weChatWebsiteMenuTService.updateBySort(weChatWebsiteMenuT.getParentId(), weChatWebsiteMenuT.getSort());
		weChatWebsiteMenuT.setSort(weChatWebsiteMenuT.getSort()+1);
		weChatWebsiteMenuTService.save(weChatWebsiteMenuT);

        return getSuccessResult(weChatWebsiteMenuT.getMenuId());
	}

	/**
	 * 编辑WebsiteMenuT
	 */
	@LogContent("编辑WebsiteMenuT")
	@RequestMapping(method=RequestMethod.PUT)
	public HttpResultEntity<?> edit(@RequestBody WeChatWebsiteMenuT weChatWebsiteMenuT)   {
		weChatWebsiteMenuTService.updateForSelective(weChatWebsiteMenuT);
        return getSuccessResult();
	}

	/**
	 * 删除单个WebsiteMenuT
	 */
	@LogContent("删除WebsiteMenuT")
	@RequestMapping(value = "/{menuId}",method=RequestMethod.DELETE)
	public HttpResultEntity<?> delete(@PathVariable String menuId)   {
		weChatWebsiteMenuTService.delete(menuId);
		return getSuccessResult();
	}

    /**
     * 查询单个WebsiteMenuT
	 */
	 @LogContent("查询单个WebsiteMenuT")
	 @RequestMapping(value = "/{menuId}",method= RequestMethod.GET)
	 public HttpResultEntity<?> get(@PathVariable String menuId)   {
		 return getSuccessResult(weChatWebsiteMenuTService.find(menuId));
	 }
	/**
	 * 插入二级菜单
	 */
	@LogContent("添加二级菜单")
	@RequestMapping(value ="/addTwoMenu/{parentId}",method=RequestMethod.POST)
	public HttpResultEntity<?> addWeb(@RequestBody WeChatWebsiteT weChatWebsiteT, @PathVariable String parentId)   {
		SecurityUserT usert = (SecurityUserT)session.getAttribute(SessionUtils.SESSION_MANAGER);

		List<WeChatWebsiteMenuT> list = weChatWebsiteT.getList();
		for (int i = 0; i < list.size(); i++) {
			WeChatWebsiteMenuT weChatWebsiteMenuT = new WeChatWebsiteMenuT();
			weChatWebsiteMenuT.setParentId(parentId);
			weChatWebsiteMenuT.setMenuImg(list.get(i).getMenuImg());
			weChatWebsiteMenuT.setMenuName(list.get(i).getMenuName());
			weChatWebsiteMenuT.setResume(list.get(i).getResume());
			weChatWebsiteMenuT.setUrl("#");
			weChatWebsiteMenuT.setSort(i);
			weChatWebsiteMenuTService.save(weChatWebsiteMenuT);
		}

		return getSuccessResult(parentId);
	}

}
