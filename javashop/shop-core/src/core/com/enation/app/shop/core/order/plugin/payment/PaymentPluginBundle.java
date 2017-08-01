package com.enation.app.shop.core.order.plugin.payment;

import java.util.List;

import org.springframework.stereotype.Service;

import com.enation.framework.plugin.AutoRegisterPluginsBundle;

/**
 * 支付插件桩
 * @author Sylow
 * @version v2.0,2016年2月18日
 * @since v6.0
 */
@Service("paymentPluginBundle")
public class PaymentPluginBundle extends AutoRegisterPluginsBundle {

	public String getName() {
		
		return "支付插件桩";
	}
	public List getPluginList(){
		
		return this.getPlugins();
	}
	
}
