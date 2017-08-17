package com.github.jyoghurt.security.interceptor;


import com.github.jyoghurt.security.annotations.IgnoreAuthentication;
import com.github.jyoghurt.webscoket.common.constants.ScoketConstants;
import com.github.jyoghurt.core.result.HttpResultEntity;
import com.github.jyoghurt.core.result.HttpResultHandle;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pub.utils.SessionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
    @Value("${devMode}")
    private Boolean devMode;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (null != request.getHeader("cosDfCid")) {
            //若有收银台信息则获取收银台信息
            request.getSession().setAttribute(ScoketConstants.CASHIERID, request.getHeader("cosDfCid"));
        }
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        if (null != method.getAnnotation(IgnoreAuthentication.class)) {
            return true;
        }
//        if (null != method.getAnnotation(DevModeIgnoreAuthentication.class)&&devMode){
//            return true;
//        }
        if (null == SessionUtils.getManager(request)) {
            if (!(request.getHeader("accept").contains("application/json") || (request
                    .getHeader("X-Requested-With") != null && request
                    .getHeader("X-Requested-With").contains("XMLHttpRequest")))) {
                response.sendRedirect(StringUtils.join(request.getContextPath(), "/login.html"));
            } else {
                response.getWriter().write(JSONObject.fromObject(new HttpResultEntity<>(HttpResultHandle
                        .HttpResultEnum.NOT_LOGGED)).toString());
            }
            return false;
        }
        return true;
    }
}
