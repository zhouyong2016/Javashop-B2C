package com.enation.app.shop.core.order.plugin.order;
 

/**
 * 确认收款事件 
 * @author Chopper
 * @version 1.0
 * 2015年10月28日10:46:32
 */
public interface IConfirmReceiptEvent {
	
	/**
	 * 确认结算
	 * @param orderprice
	 * @return
	 */
	public void confirm(Integer orderid,double price);
	
	
}
