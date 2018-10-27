package com.github.jyoghurt.core.controller;


import com.github.jyoghurt.core.constant.Constant;
import com.github.jyoghurt.core.exception.BaseAccidentException;
import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.po.BasePO;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.result.HttpResultHandle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    private static String EXCEPTION = "Exception";

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    protected HttpSession session;

    @ExceptionHandler
    public ResponseEntity<?> exceptionHandler(HttpServletRequest request, Exception ex) {
        request.setAttribute(EXCEPTION, ex);
        String logTemplate = "\n url:★{}★\n parameterValues : ★{}★";
        String parameterValues = com.github.jyoghurt.core.utils.WebUtils.getParameterValues(request);
        String serverName = request.getServerName();
        String operatorId = (String) WebUtils.getSessionAttribute(request, BasePO.OPERATOR_ID);
        String operatorName = (String) WebUtils.getSessionAttribute(request, BasePO.OPERATOR_NAME);
        String operatorLogStr = "\n ★{User-Agent:[" + request.getHeader("User-Agent") + "],serverName:[" + serverName + "]" +
                ",operatorId:[" + operatorId + "],operatorName:[" + operatorName + "]}★ \n";
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

        if (ex instanceof MethodArgumentNotValidException) {
            String message = StringUtils.EMPTY;
            BindingResult bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                message = StringUtils.join(message, objectError.getDefaultMessage(), ",");
            }
            log.error(operatorLogStr + ex.getMessage() + logTemplate, request.getServletPath(),
                    parameterValues, ex);
            return new ResponseEntity<>(HttpResultHandle.HttpResultEnum.ERROR.getErrorCode(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.error(operatorLogStr + "uncaught  exception," + logTemplate, request.getServletPath(),
                parameterValues, ex);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
