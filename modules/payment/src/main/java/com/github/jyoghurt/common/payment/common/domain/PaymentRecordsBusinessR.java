package com.github.jyoghurt.common.payment.common.domain;

import com.github.jyoghurt.common.payment.common.enums.PaymentBusinessTypeEnum;
import com.github.jyoghurt.core.domain.BaseEntity;


@javax.persistence.Table(name = "PaymentRecordsBusinessR")
public class PaymentRecordsBusinessR extends BaseEntity<PaymentRecordsBusinessR>{
	
	/** 
	 * 业务类型  
	 */
	private PaymentBusinessTypeEnum businessType;
	/** 
	 * 业务Id  
	 */
	private String businessId;
	/** 
	 * 支付记录Id  
	 */
	private String paymentId;
	/** 
	 * 关联主键  
	 */
	@javax.persistence.Id
	private String relateId;
	
	public PaymentBusinessTypeEnum getBusinessType() {
	    return this.businessType;
	}
	
	public PaymentRecordsBusinessR setBusinessType(PaymentBusinessTypeEnum businessType) {
		this.businessType = businessType;
		return this;
	}
	
	public String getBusinessId() {
	    return this.businessId;
	}
	
	public PaymentRecordsBusinessR setBusinessId(String businessId) {
		this.businessId = businessId;
		return this;
	}
	
	public String getPaymentId() {
	    return this.paymentId;
	}
	
	public PaymentRecordsBusinessR setPaymentId(String paymentId) {
		this.paymentId = paymentId;
		return this;
	}
	
	public String getRelateId() {
	    return this.relateId;
	}
	
	public PaymentRecordsBusinessR setRelateId(String relateId) {
		this.relateId = relateId;
		return this;
	}
}
