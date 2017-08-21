package com.enation.app.shop.core.order.plugin.payment;

import com.enation.framework.context.spring.SpringContextHolder;


/**
 * 支付成功处理器工厂
 * @author kingapex
 *2013-9-24上午11:12:55
 */
public final  class PaySuccessProcessorFactory {
	
	
	public static IPaySuccessProcessor getProcessor(String ordertype){
		if("s".equals(ordertype)){
			
			return SpringContextHolder.getBean("standardOrderPaySuccessProcessor");
		}
		if("credit".equals(ordertype)){
			
			return SpringContextHolder.getBean("creditPaySuccessProcessor");
		}
		if("b".equals(ordertype)){
			
			return SpringContextHolder.getBean("b2b2cOrderPaySuccessProcessor");
		}
		return null;
	}
	
	
}
