package com.github.jyoghurt.common.payment.common.module;

import com.github.jyoghurt.common.payment.common.enums.PaymentBusinessTypeEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * user: dell
 * date:2016/3/18.
 */
public class PaymentRequest {
    /**
     * 支付金额
     */
    private BigDecimal totalFee;
    /**
     * 支付详情
     */
    private String paymentDetail;
    /**
     * 模块名称
     */
    private PaymentBusinessTypeEnum moduleName;
    /**
     * 业务数目
     */
    private int orderNum;
    /**
     * 支付业务Id集合
     */
    private List<String> businessIds;
    /**
     * 支付业务类型
     */
    private PaymentBusinessTypeEnum paymentBusinessType;
    /**
     * 支付相关数据区
     */
    private Map<String, Object> dataArea;


    public List<String> getBusinessIds() {
        return businessIds;
    }

    public void setBusinessIds(List<String> businessIds) {
        this.businessIds = businessIds;
    }

    public PaymentBusinessTypeEnum getPaymentBusinessType() {
        return paymentBusinessType;
    }

    public void setPaymentBusinessType(PaymentBusinessTypeEnum paymentBusinessType) {
        this.paymentBusinessType = paymentBusinessType;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public String getPaymentDetail() {
        return paymentDetail;
    }

    public void setPaymentDetail(String paymentDetail) {
        this.paymentDetail = paymentDetail;
    }

    public PaymentBusinessTypeEnum getModuleName() {
        return moduleName;
    }

    public void setModuleName(PaymentBusinessTypeEnum moduleName) {
        this.moduleName = moduleName;
    }

    public Map<String, Object> getDataArea() {
        return dataArea;
    }

    public void setDataArea(Map<String, Object> dataArea) {
        if (null == dataArea) {
            return;
        }
        if (null != this.dataArea) {
            this.dataArea.putAll(dataArea);
            return;
        }
        this.dataArea = dataArea;
    }

    public void setDataArea(String key, Object object) {
        this.dataArea.put(key, object);
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "totalFee=" + totalFee +
                ", paymentDetail='" + paymentDetail + '\'' +
                ", moduleName=" + moduleName +
                ", orderNum=" + orderNum +
                ", businessIds=" + businessIds +
                ", paymentBusinessType=" + paymentBusinessType +
                ", dataArea=" + dataArea +
                '}';
    }
}
