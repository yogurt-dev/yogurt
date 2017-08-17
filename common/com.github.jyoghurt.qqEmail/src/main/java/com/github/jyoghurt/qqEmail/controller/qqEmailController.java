package com.github.jyoghurt.qqEmail.controller;


import com.github.jyoghurt.qqEmail.service.EmailVersionTService;
import com.github.jyoghurt.core.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 事故单管理控制器
 */
@RestController
@RequestMapping("/qqEmail")
public class qqEmailController extends BaseController {
    /**
     * 事故单管理服务类
     */
    @Resource
    private EmailVersionTService emailVersionTService;



}
