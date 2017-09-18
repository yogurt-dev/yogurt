package com.github.jyoghurt.serial.enums;

/**
 * @Project: 驴鱼社区-车险帮
 * @Package: com.df.community.base.serial
 * @Description: 列举了使用业务主键的业务模块列表
 * @author: baoxiaobing@lvyushequ.com
 * @date: 2016-03-15 15:07
 */
public enum Module {

    WASH("洗衣模块", "WASH"),
    MEMBER("订单模块", "MEMBER"),
    MEMBER_BILL("会员账单流水","BILL"),
    SHOP_PURCHASE("采购单","SHOP-C"),
    SHOP_RETURN("返货单","SHOP-F"),
    STOCK_RETURN("退货单","SHOP-T"),
    SHOP_RETURN_EXCHANGE("退换货单","SHOP-TH"),
    SHOP_RECEIPT("入库单","SHOP-R"),
    SHOP_DISTRIBUTE("送货单","SHOP-S"),
    SHOP_REFUND("退款单","SHOP-K"),
    SHOP_GOODS("商品", "GOODS"),
    PERFORMANCE_RECORD("绩效审批记录","PERFORM_R"),
    COMM("公用模块","COMM"),
    NORMAL("默认","NORMAL"),
    ADVANCED("高级","ADVANCED");

    private String name;
    private String code;

    Module(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
