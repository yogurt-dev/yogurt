package com.github.yogurt.core.utils;


import javax.servlet.http.HttpServletRequest;

/**
 * @author jtwu
 */
public class WebUtils extends org.springframework.web.util.WebUtils {

    /**
     * 获取url和request参数
     */
    public static String getParameterValues(HttpServletRequest request) {
        Object pathVariables = request.getAttribute("org.springframework.web.servlet.View.pathVariables");
        String parameterValues = org.springframework.web.util.WebUtils.getParametersStartingWith(request, null).toString();
        if (pathVariables == null) {
            pathVariables = "{}";
        }
        return "parameterValues:" + parameterValues + ",pathVariables:" + pathVariables;
    }

}
