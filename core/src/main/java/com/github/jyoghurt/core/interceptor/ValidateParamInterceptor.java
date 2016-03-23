package com.github.jyoghurt.core.interceptor;


import com.github.jyoghurt.core.controller.BaseController;
import com.github.jyoghurt.core.result.HttpResultEntity;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Created by jietianwu on 14-5-8. 用于记录操作日志及错误日志
 */
@Component
@Aspect
@Order(10)
public class ValidateParamInterceptor {
    @Around(value = "@within(org.springframework.web.bind.annotation.RestController)||" +
            "@within(org.springframework.stereotype.Controller)")
    public HttpResultEntity around(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return (HttpResultEntity) pjp.proceed();
        } catch (ConstraintViolationException e) {
            String message = StringUtils.EMPTY;
            for (ConstraintViolation violation : e.getConstraintViolations()) {
                message = StringUtils.join(message, violation.getMessage(), ",");
            }
            return BaseController.getErrorResult(message);
        }
    }
}
