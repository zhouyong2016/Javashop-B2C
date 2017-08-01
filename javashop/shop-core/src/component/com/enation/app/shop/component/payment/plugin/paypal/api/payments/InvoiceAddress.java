package com.enation.app.shop.component.payment.plugin.paypal.api.payments;

public class InvoiceAddress extends BaseAddress {
	
	private Phone phone;
	
	public InvoiceAddress setPhone(Phone phone) {
		this.phone = phone;
		return this;
	}
	
	public Phone getPhone() {
		return this.phone;
	}
}
