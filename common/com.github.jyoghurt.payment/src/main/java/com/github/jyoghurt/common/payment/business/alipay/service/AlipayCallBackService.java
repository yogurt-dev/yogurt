package com.github.jyoghurt.common.payment.business.alipay.service;

import com.github.jyoghurt.core.exception.UtilException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * user: dell
 * date:2016/3/14.
 */
public interface AlipayCallBackService {
    /**
     * 解析微信支付返回
     * @param request
     * @param response
     */
    void parserAliPayCallBack(HttpServletRequest request, HttpServletResponse response) throws  UtilException;
}
