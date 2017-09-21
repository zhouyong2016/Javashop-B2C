package com.enation.app.shop.component.payment.plugin.offline;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PayEnable;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.order.plugin.payment.IPaymentEvent;

/**
 * 线下支付插件
 * @author kingapex
 * 2010-5-26上午10:06:13
 */
@Component("offline")
public class OfflinePlugin extends AbstractPaymentPlugin implements
		IPaymentEvent {


	
	public String onCallBack(String ordertype) {
		return "";
	}

	
	public String onPay(PayCfg payCfg, PayEnable order) {
		
		return "";
	}



	@Override
	public String getId() {
		return "offline";
	}

	@Override
	public String getName() {
		return "线下支付";
	}





	@Override
	public String onReturn(String ordertype) {
		 
		return "";
	}

	@Override
	public String onRefund(PayEnable order, Refund refund, PaymentLog paymentLog) {
		// TODO Auto-generated method stub
		return null;
	}
}
