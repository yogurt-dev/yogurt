package com.github.yogurt.core.interceptor;

import com.github.yogurt.core.exception.BaseErrorException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Created by jietianwu on 14-5-8. 用于记录操作日志及错误日志
 */

public class ValidateParamInterceptor {
    @Around(value = "@within(org.springframework.web.bind.annotation.RestController)||" +
            "@within(org.springframework.stereotype.Controller)")
    public ResponseEntity controllerInterceptor(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return (ResponseEntity) pjp.proceed();
        } catch (ConstraintViolationException e) {
            for (ConstraintViolation violation : e.getConstraintViolations()) {
                throw new BaseErrorException(violation.getMessage());
            }
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
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
