package com.enation.app.shop.core.order.service;

import java.util.List;

import com.enation.app.shop.core.order.model.PaymentLog;


/**
 * 收退款单管理
 * @author kingapex
 *2012-3-7下午5:09:19
 */
public interface IPaymentLogManager {
	
	
	/**
	 * 读取某个
	 * @param orderid
	 * @param type
	 * @param status
	 * @return
	 */
	public List<PaymentLog> list(int orderid,int type,int status);
	/**
	 * 查询某个收款单数据
	 * @param orderid
	 * @return
	 */
	public PaymentLog get(int orderid);
}
