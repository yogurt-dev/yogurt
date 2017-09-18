package com.github.jyoghurt.common.payment.business.tencent.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * user: dell
 * date:2016/3/14.
 */
public interface TencentCallBackService {
    /**
     * 解析微信支付返回
     * @param request
     * @param response
     */
    void parserTencentCallBack(HttpServletRequest request, HttpServletResponse response);
}
