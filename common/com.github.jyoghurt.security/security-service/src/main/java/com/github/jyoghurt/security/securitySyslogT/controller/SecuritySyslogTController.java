package com.github.jyoghurt.security.securitySyslogT.controller;

import com.github.jyoghurt.security.exception.SecurityException;
import com.github.jyoghurt.security.securitySyslogT.domain.SecuritySyslogT;
import com.github.jyoghurt.security.securitySyslogT.service.SecuritySyslogTService;
import com.github.jyoghurt.core.annotations.LogContent;
import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.handle.QueryHandle;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统日志控制器
 */
@RestController
@LogContent(module = "系统管理")
@RequestMapping("/securitySyslogT")
public class SecuritySyslogTController extends BaseController {

    @Value("${devMode}")
    private String devMode;
    /**
     * 系统日志服务类
     */
    @Qualifier("aliyunLogService")
    @Autowired
    private SecuritySyslogTService securitySyslogTService;

    /**
     * 列出系统日志
     */
    @LogContent("查询系统日志")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public HttpResultEntity<?> list(SecuritySyslogT securitySyslogT, QueryHandle queryHandle) throws SecurityException {

//        if (Boolean.valueOf(devMode)) {
//            throw new SecurityException(ERROR_90807);
//        }
        return getSuccessResult(securitySyslogTService.getData(securitySyslogT, queryHandle.configPage().addOrderBy
                ("createDateTime", "desc")));

    }
}
