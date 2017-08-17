package com.github.jyoghurt.UIFramework.controller;

import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.utils.ChainMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dengguangyi on 2015/12/17.
 */
@RestController
@LogContent(module = "系统应用配置")
@RequestMapping("/appconfig")
public class ApplicationConfigConroller  extends BaseController{
    @Value("${devMode}")
    private String devMode;
    /**
     * 获取配置信息
     */
    @LogContent("获取系统开发模式配置信息")
    @RequestMapping(value = "/getDevMode", method = RequestMethod.GET)
    public HttpResultEntity<?> getDevMode()   {
        return getSuccessResult(new ChainMap<>().chainPut("devMode",devMode));
    }
}
