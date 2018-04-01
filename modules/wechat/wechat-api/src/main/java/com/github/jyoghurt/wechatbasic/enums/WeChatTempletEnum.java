package com.github.jyoghurt.wechatbasic.enums;

/**
 * user: dell
 * date: 2016/4/25.
 */
public enum WeChatTempletEnum {
    ORDER_PAYMENT_SUCCESS("d5UQFqf38z2vYn7bF4IhY10Fej18QuMDADxjKzpDA9E", "订单成功模板");
    /**
     * 模板Id
     */
    private String templetId;
    /**
     * 模板名称
     */
    private String templateName;

    WeChatTempletEnum(String templetId, String templateName) {
        this.templetId = templetId;
        this.templateName = templateName;
    }

    public String getTempletId() {
        return templetId;
    }

    public void setTempletId(String templetId) {
        this.templetId = templetId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
