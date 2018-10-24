package com.github.jyoghurt.core.interceptor;

import com.github.jyoghurt.core.exception.BaseErrorException;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.result.HttpResultHandle;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Created by jietianwu on 14-5-8. 用于记录操作日志及错误日志
 */

public class ValidateParamInterceptor {
    @Around(value = "@within(org.springframework.web.bind.annotation.RestController)||" +
            "@within(org.springframework.stereotype.Controller)")
    public HttpResultEntity controllerInterceptor(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return (HttpResultEntity) pjp.proceed();
        } catch (ConstraintViolationException e) {
            String message = StringUtils.EMPTY;
            for (ConstraintViolation violation : e.getConstraintViolations()) {
                throw new BaseErrorException(violation.getMessage());
            }
            return new HttpResultEntity(HttpResultHandle.HttpResultEnum.ERROR.getErrorCode()
                    , StringUtils.stripEnd(message, ","));
        }
    }

    @Around(value = "@within(org.springframework.stereotype.Service)")
    public Object serviceInterceptor(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        } catch (ConstraintViolationException e) {
            for (ConstraintViolation violation : e.getConstraintViolations()) {
                throw new BaseErrorException(violation.getMessage());
            }
            throw new BaseErrorException(e);
        }
    }
}
