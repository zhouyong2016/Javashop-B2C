package com.enation.app.shop.core.order.plugin.payment;

import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PayEnable;

/**
 * 读取支付二维码时间
 * @author xulipeng
 *
 */
public interface IPaymentQrCodeEvent {

	/**
	 * 获取支付二维码
	 * @param payCfg
	 * @param order
	 * @return
	 */
	public String onPayQrCode(PayCfg  payCfg,PayEnable order);
	
	/**
	 * 读取订单支付状态
	 * @param payCfg
	 * @param order
	 * @return
	 */
	public String onPayStatus(PayCfg  payCfg,PayEnable order);
	
}
