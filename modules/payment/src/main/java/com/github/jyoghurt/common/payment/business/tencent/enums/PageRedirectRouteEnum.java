package com.github.jyoghurt.common.payment.business.tencent.enums;

/**
 * user:dell
 * data:2016/4/27.
 */
public enum PageRedirectRouteEnum {
    ORDER_PAGE("订单页"),
    ORDER_DETAIL_PAGE("订单详情展示页"),
    PAYMENT_PAGE("支付页"),//遗留旧逻辑
    CASH_PAYMENT_PAGE("现金支付页"),
    CARD_PAYMENT_PAGE("刷卡支付页"),
    TENCENT_PAYMENT_PAGE("微信支付页"),
    ALIPAY_PAYMENT_PAGE("支付宝支付页"),
    PAYMENT_SUCCESS_PAGE("支付成功页"),
    ERROR_PAGE("错误页");

    PageRedirectRouteEnum(String pageName) {
        this.pageName = pageName;
    }

    private String pageName;


    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }
}
