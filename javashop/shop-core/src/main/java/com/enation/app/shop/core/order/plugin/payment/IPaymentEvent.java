package com.enation.app.shop.core.order.plugin.payment;

import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PayEnable;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;

/**
 * 在线支付事件
 * @author kingapex
 *
 */
public interface IPaymentEvent {
	
	/**
	 * 生成跳转至第三方支付平台的html和脚本
	 * @param payCfg
	 * @param order 可支付的对象
	 * @return 跳转到第三方支付平台的html和脚本
	 */
	public String onPay(PayCfg  payCfg,PayEnable order);
	
	
	/**
	 * 支付成功后异步通知事件的
	 * @param ordertype 订单类型，standard 标准订单，credit:信用账户充值
	 * @return 返回第三方支付需要的信息
	 * 
	 */
	public String onCallBack(String ordertype);
	
	
	/**
	 * 支付成功后返回本站后激发此事件 
	 * @param ordertype 订单类型，standard 标准订单，credit:信用账户充值
	 * @return  要求返回订单sn
	 */
	public String onReturn(String ordertype);
	
	/**
	 * PaymentLog paymentLog
	 * @param order			订单信息
	 * @param refund		退款单
	 * @param paymentLog	收款单
	 * @return
	 */
	public String onRefund(PayEnable order,Refund refund,PaymentLog paymentLog);
	
	
	
}	
