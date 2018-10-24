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
    public HttpResultEntity<?> exceptionHandler(HttpServletRequest request, Exception ex) {
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
            return HttpResultHandle.getErrorResult(((BaseAccidentException) ex).getErrorCode().replace(Constant.ERROR_CODE_PREFIX,
                    StringUtils.EMPTY), ex.getMessage());
        }

        if (ex instanceof BaseErrorException) {
            log.error(operatorLogStr + ex.getMessage() + logTemplate, request.getServletPath(),
                    parameterValues, ex);
            return HttpResultHandle.getErrorResult(ex.getMessage());
        }

        if (ex instanceof MethodArgumentNotValidException) {
            String message = StringUtils.EMPTY;
            BindingResult bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                message = StringUtils.join(message, objectError.getDefaultMessage(), ",");
            }
            log.error(operatorLogStr + ex.getMessage() + logTemplate, request.getServletPath(),
                    parameterValues, ex);
            return new HttpResultEntity(HttpResultHandle.HttpResultEnum.ERROR.getErrorCode()
                    , StringUtils.stripEnd(message, ","));
        }

        log.error(operatorLogStr + "uncaught  exception," + logTemplate, request.getServletPath(),
                parameterValues, ex);
        return HttpResultHandle.getErrorResult();
    }


    public HttpResultEntity<?> getSuccessResult() {
        return HttpResultHandle.getSuccessResult();
    }


    public HttpResultEntity<?> getSuccessResult(Object result) {
        return HttpResultHandle.getSuccessResult(result);
    }


    public static HttpResultEntity<?> getErrorResult() {
        return HttpResultHandle.getErrorResult();
    }


    public static HttpResultEntity<?> getErrorResult(BaseAccidentException e) {
        if (null == e) {
            return getErrorResult();
        }
        log.warn(e.getMessage(), e.getCause());
        if (null == e.getExceptionBody()) {
            return getErrorResult();
        }
        return HttpResultHandle.getErrorResult(e.getErrorCode(), e.getExceptionBody().getMessage());
    }

    public static HttpResultEntity<?> getErrorResult(Object result) {
        return HttpResultHandle.getErrorResult(result);
    }

}
