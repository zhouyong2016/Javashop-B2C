package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

public class Measurement {
	
	/**
	 * Value this measurement represents.
	 */
	private String value;
	
	/**
	 * Unit in which the value is represented.
	 */
	private String unit;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
