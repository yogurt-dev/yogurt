package com.github.jyoghurt.image.controller;

import com.github.jyoghurt.image.domain.ImgT;
import com.github.jyoghurt.image.service.ImgService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


/**
 * 图片控制器
 *
 */
@RestController
@LogContent(module = "图片")
@RequestMapping("/img")
public class ImgController extends BaseController {


	/**
	 * 图片服务类
	 */
	@Resource
	private ImgService imgService;

	/**
	 * 根据图片ID查询url
	 */
	@LogContent("查询图片")
	@RequestMapping(value = "/urlById/{imgId}",method= RequestMethod.GET)
	public HttpResultEntity<?> urlById(@PathVariable("imgId") String imgId)  {
		ImgT imgT = imgService.find(imgId);
		if(null!=imgT){
			return getSuccessResult(imgT.getPath());
		}
		return getSuccessResult(0);
	}

	/**
	 * 根据图片ID查询url
	 */
	@LogContent("查询图片")
	@RequestMapping(value = "/imgsByBussinessId/{businessId}",method= RequestMethod.GET)
	public HttpResultEntity<?> imgsByBussinessId(@PathVariable("businessId") String businessId)  {
		List<ImgT> imgTs = imgService.findAll(new ImgT().setNaturalkey(businessId), new QueryHandle().addOrderBy("createDateTime", "asc"));
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//定义返回
		JSONObject result = new JSONObject();
		for(ImgT imgT:imgTs){
			jsonObject = JSONObject.fromObject(imgT);
			jsonArray.add(jsonObject);
		}
		result.put("imgInfo",jsonArray);
		return getSuccessResult(result);
	}

//	/**
//	 * 列出图片
//	 */
//	@LogContent("查询图片")
//	@RequestMapping(value = "/list",method=RequestMethod.GET)
//	public HttpResultEntity<?> list(ImgT imgT ,QueryHandle queryHandle)   {
//        return getSuccessResult(imgService.getData(imgT.setDeleteFlag(false),queryHandle.configPage().addOrderBy("createDateTime",
//				"desc")));
//
//	}
//
//
//	/**
//	 * 添加图片
//	 */
//	@LogContent("添加图片")
//	@RequestMapping(method=RequestMethod.POST)
//	public HttpResultEntity<?> add(@RequestBody ImgT imgT)   {
//		imgService.save(imgT);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 编辑图片
//	 */
//	@LogContent("编辑图片")
//	@RequestMapping(method=RequestMethod.PUT)
//	public HttpResultEntity<?> edit(@RequestBody ImgT imgT)   {
//		imgService.updateForSelective(imgT);
//        return getSuccessResult();
//	}
//
//	/**
//	 * 删除单个图片
//	 */
//	@LogContent("删除图片")
//	@RequestMapping(value = "/{imgId}",method=RequestMethod.DELETE)
//	public HttpResultEntity<?> logicDelete(@PathVariable String imgId)   {
//		imgService.logicDelete(imgId);
//		return getSuccessResult();
//	}
//
//    /**
//     * 查询单个图片
//	 */
//	 @LogContent("查询单个图片")
//	 @RequestMapping(value = "/{imgId}",method=RequestMethod.GET)
//	 public HttpResultEntity<?> get(@PathVariable String imgId)   {
//		 return getSuccessResult(imgService.find(imgId));
//	 }
}
