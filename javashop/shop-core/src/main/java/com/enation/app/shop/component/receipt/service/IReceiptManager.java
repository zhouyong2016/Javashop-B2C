package com.enation.app.shop.component.receipt.service;

import com.enation.app.shop.component.receipt.Receipt;

public interface IReceiptManager {

	public void add(Receipt invoice);
	
	public Receipt getById(Integer id);
	
	public Receipt getByOrderid(Integer orderid);
}
