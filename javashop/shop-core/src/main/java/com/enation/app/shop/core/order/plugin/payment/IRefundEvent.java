package com.enation.app.shop.core.order.plugin.payment;

import com.enation.app.shop.core.order.model.PayEnable;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;

/**
 * 
 * 退款事件
 * @author zh
 * @version v6.5.0
 * @since v6.5.1
 * 2017年6月27日 下午10:10:24
 */
public interface IRefundEvent {
	
	/**
	 * PaymentLog paymentLog
	 * @param order			订单信息
	 * @param refund		退款单
	 * @param paymentLog	收款单
	 * @return
	 */
	public String onRefund(PayEnable order,Refund refund,PaymentLog paymentLog);
	
	
	
}	
