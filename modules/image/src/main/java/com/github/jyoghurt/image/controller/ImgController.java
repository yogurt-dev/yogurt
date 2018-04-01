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
}
