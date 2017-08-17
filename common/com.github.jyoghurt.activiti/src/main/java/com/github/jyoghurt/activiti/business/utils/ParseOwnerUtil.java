package com.github.jyoghurt.activiti.business.utils;

import com.github.jyoghurt.activiti.business.enums.Operator;
import com.github.jyoghurt.security.securityUserT.domain.SecurityUserT;
import com.github.jyoghurt.core.exception.BaseErrorException;
import org.activiti.engine.delegate.DelegateTask;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * user:zjl
 * date: 2016/12/9.
 */
public class ParseOwnerUtil {

    /**
     * 获得运算符
     *
     * @param var 传递的参数
     * @return 运算符枚举
     */
    public static Operator getOperator(String var) {
        return Operator.valueOf(RegularUtil.getDoubleBracketsContent(var).get(0));
    }

    /**
     * 获得角色或部门配置的值
     *
     * @param var 参数
     * @return 值
     */
    public static String getValue(String var, DelegateTask var1) {
        if (StringUtils.isEmpty(var)) {
            return null;
        }
        if (!var.contains("{{")) {
            return var;
        }
        return parseValue(RegularUtil.getDoubleContent(var).get(0), var1);
    }

    /**
     * @param users 用户集合
     * @return 用户Id集合
     */
    public static List<String> getUserIds(List<SecurityUserT> users) {
        List<String> userIds = new ArrayList<>();
        for (SecurityUserT userT : users) {
            userIds.add(userT.getUserId());
        }
        return userIds;
    }

    private static String parseValue(String var, DelegateTask var1) {
        if (var.startsWith("cookie.")) {
            var = var.replace("cookie.", "");
            HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Cookie[] cookies = httpServletRequest.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(var)) {
                        return cookie.getValue();
                    }
                }
            }
            throw new BaseErrorException("解析指定流程待办人时并未获取到cookie中key为:{}的元素", var);
        }
        if (var.startsWith("variable.")) {
            var = var.replace("variable.", "");
            if (null == var1.getVariable(var)) {
                throw new BaseErrorException("解析指定流程待办人时并未获取到variable中key为:{}的元素", var);
            }
            return var1.getVariable(var).toString();
        }
        return var;
    }
}
