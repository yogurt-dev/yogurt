package com.github.jyoghurt.common.payment.business.alipay.enums;

/**
 * user:zjl
 * date: 2016/12/26.
 */
public enum AliRefundPaymentStateEnum {
   FAIL("Business Failed");

    private String value;

    AliRefundPaymentStateEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
