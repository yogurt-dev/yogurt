package com.github.jyoghurt.activiti.business.controller;

import com.github.jyoghurt.activiti.business.exception.WorkFlowException;
import com.github.jyoghurt.activiti.business.module.WorkItem;
import com.github.jyoghurt.activiti.business.service.BulidPageService;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pub.utils.SessionUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

/**
 * Created by dell on 2016/1/7.
 */
@RestController
@LogContent(module = "集合拼装activiti")
@RequestMapping("/buildPage")
public class BuildPageController extends BaseController {
    @Resource
    private BulidPageService bulidPageService;
    @LogContent("build")
    @RequestMapping(value = "/build", method = RequestMethod.POST)
    public HttpResultEntity build(String procInsId, @RequestBody WorkItem workItem) throws UnsupportedEncodingException, WorkFlowException {
        SecurityUserT securityUserT = (SecurityUserT) session.getAttribute(SessionUtils.SESSION_MANAGER);
        return getSuccessResult(bulidPageService.build(procInsId, workItem, securityUserT));
    }
}
