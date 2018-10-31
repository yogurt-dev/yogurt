package com.github.yogurt.core.controller;


import com.github.yogurt.core.Configuration;
import com.github.yogurt.core.constant.Constant;
import com.github.yogurt.core.exception.BaseAccidentException;
import com.github.yogurt.core.exception.BaseErrorException;
import com.github.yogurt.core.po.BasePO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 控制器基类
 *
 * @author jtwu
 */
@Slf4j
public class BaseController {

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    protected HttpSession session;
    @Autowired
    private Configuration configuration;

    @ExceptionHandler
    public ResponseEntity<?> exceptionHandler(HttpServletRequest request, Exception ex) {
        String logTemplate = "\n url:★{}★\n parameterValues : ★{}★";
        String parameterValues = com.github.yogurt.core.utils.WebUtils.getParameterValues(request);
        String serverName = request.getServerName();
        String userId = (String) WebUtils.getSessionAttribute(request, configuration.getUserId());
        String userName = (String) WebUtils.getSessionAttribute(request, configuration.getUserName());
        String operatorLogStr = "\n ★{User-Agent:[" + request.getHeader("User-Agent") + "],serverName:[" + serverName + "]" +
                ",userId:[" + userId + "],userName:[" + userName + "]}★ \n";
        if (ex instanceof BaseAccidentException) {
            if (((BaseAccidentException) ex).getLogFlag()) {
                log.error(operatorLogStr + ex.getMessage() + logTemplate, request.getServletPath(),
                        parameterValues, ex);
            } else {
                log.warn(operatorLogStr + ex.getMessage() + logTemplate, request.getServletPath(),
                        parameterValues, ex);
            }
            return new ResponseEntity<>(((BaseAccidentException) ex).getErrorCode().replace(Constant.ERROR_CODE_PREFIX,
                    StringUtils.EMPTY),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (ex instanceof BaseErrorException) {
            log.error(operatorLogStr + ex.getMessage() + logTemplate, request.getServletPath(),
                    parameterValues, ex);
            return new ResponseEntity<>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.error(operatorLogStr + "uncaught  exception," + logTemplate, request.getServletPath(),
                parameterValues, ex);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
