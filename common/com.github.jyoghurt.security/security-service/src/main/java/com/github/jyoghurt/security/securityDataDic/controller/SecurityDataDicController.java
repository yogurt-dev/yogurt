package com.github.jyoghurt.security.securityDataDic.controller;

import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.security.securityDataDic.service.SecurityDataDicService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 字典管理控制器
 *
 */
@RestController
@LogContent(module = "安全字典控制器")
@RequestMapping("/securityDataDic")
public class SecurityDataDicController extends BaseController {


	/**
	 * 字典管理服务类
	 */
	@Resource
	private SecurityDataDicService securityDataDicService;

	@LogContent("查询业务所在地字典数据")
	@RequestMapping("/queryBusinessAreaDic")
	public HttpResultEntity<?> queryBusinessAreaDic()  {
		return getSuccessResult(securityDataDicService.queryBusinessAreaDic());
	}

//	/**
//	 * 列出字典管理
//	 */
//	@LogContent("查询字典管理")
//	@RequestMapping("/list")
//	public QueryResult<SecurityDataDic> list(SecurityDataDic securityDataDic ,QueryHandle queryAssistor)   {
//        return securityDataDicService.getData(securityDataDic,queryAssistor.configPage().addOrderBy("createDateTime","desc"));
//
//	}
//
//
//	/**
//	 * 添加字典管理
//	 */
//	@LogContent("添加字典管理")
//	@RequestMapping("/add")
//	public HttpResultEntity<?> add(SecurityDataDic securityDataDic)   {
//		securityDataDicService.save(securityDataDic);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 编辑字典管理
//	 */
//  @LogContent("编辑字典管理")
//	@RequestMapping("/edit")
//	public HttpResultEntity<?> edit(SecurityDataDic securityDataDic)   {
//		securityDataDicService.updateForSelective(securityDataDic);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 删除单个字典管理
//	 */
//	@LogContent("删除字典管理")
//	@RequestMapping("/delete")
//	public HttpResultEntity<?> delete(String id)   {
//		securityDataDicService.delete(id);
//		return getSuccessResult();
//	}

//    /**
//     * 查询单个字典管理          
//	   */
//  @LogContent("查询单个字典管理")
//  @RequestMapping("/find")
//  public HttpResultEntity<?> find(String id)   {
//      return getSuccessResult(securityDataDicService.find(id));
//  }
}
