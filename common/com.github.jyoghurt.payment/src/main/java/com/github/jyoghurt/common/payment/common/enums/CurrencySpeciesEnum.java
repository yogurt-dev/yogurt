package com.github.jyoghurt.common.payment.common.enums;

public enum CurrencySpeciesEnum {
	/**
	 * 156 人民币
	 */
	CNY(156),
	/**
	 * 840 美元
	 */
	USD(840),
	/**
	 * 978 欧元
	 */
	EUR(978),
	/**
	 * 392 日元
	 */
	JPY(392),
	/**
	 * 756 瑞士法郎
	 */
	CHF(756),
	/**
	 * 036 澳大利亚元
	 */
	AUD(036),
	/**
	 * 124 加拿大元
	 */
	CAD(124),
	/**
	 * 344 港币
	 */
	HKD(344),
	/**
	 * 826 英镑
	 */
	GBP(826),
	/**
	 * 446 澳门元
	 */
	MOD(446),
	/**
	 * 702 新加坡元
	 */
	SGD(702),
	/**
	 * 901 新台币
	 */
	TWD(901),
	/**
	 * 360 印尼盾
	 */
	IDR(360);

	public final int unionPayCode;

	CurrencySpeciesEnum(int unionPayCode) {
		this.unionPayCode = unionPayCode;
	}
}
