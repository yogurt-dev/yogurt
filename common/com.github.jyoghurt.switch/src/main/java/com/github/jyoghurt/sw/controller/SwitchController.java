package com.github.jyoghurt.sw.controller;

import com.github.jyoghurt.sw.convert.SwitchConvert;
import com.github.jyoghurt.sw.domain.SwitchT;
import com.github.jyoghurt.sw.service.SwitchService;
import com.github.jyoghurt.sw.utils.SwitchUtils;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * 开关控制器
 */
@RestController
@LogContent(module = "开关")
@RequestMapping("/switch")
public class SwitchController extends BaseController {

    /**
     * 开关服务类
     */
    @Resource
    private SwitchService switchService;

    /**
     * 查询可用开关列表
     */
    @LogContent("查询开关")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public HttpResultEntity<?> list(SwitchT switchT, QueryHandle queryHandle) throws Exception {
        //根据当前环境和当前时间查询出可用的开关集合
        return getSuccessResult(SwitchConvert.convert(switchService.findAll(switchT,
                queryHandle.addWhereSql("switchStatus>=#{data.switchStatus1} and availableTime<=now()")
                        .addExpandData("switchStatus1", SwitchUtils.getEnvCode()))));
    }

    /**
     * 清除开关缓存
     */
    @LogContent("清除开关缓存")
    @RequestMapping(value = "/cacheEvict", method = RequestMethod.GET)
    public HttpResultEntity<?> cacheEvict() throws Exception {
        switchService.cacheEvict();
        return getSuccessResult();

    }
}
