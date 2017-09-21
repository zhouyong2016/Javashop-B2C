package com.enation.app.shop.front.tag.order;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.PaymentResult;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 扫码支付读取支付结果标签
 * 
 * @author [xulipeng]
 * @version [v60,2016年07月19日]
 * @since []
 */
@Component
public class PaymentResultForQrTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IPaymentManager paymentManager;
	@Autowired
	private IOrderManager orderManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		PaymentResult paymentResult = new PaymentResult();
		
		//支付状态 1:支付成功 {内网支付不能更改订单付款状态 (仅支付宝扫码支付方式)}，0:支付出现问题，
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Integer orderId = Integer.parseInt(request.getParameter("orderId"));
		
		Order order = this.orderManager.get(orderId);
		
		String pluginId = request.getParameter("pluginId");
		String trade_status = request.getParameter("trade_status");
		
		//判断 pluginId不是null && pluginId 是支付宝支付 && trade_status支付状态不是null && （支付状态为TRADE_FINISHED 或者 TRADE_SUCCESS 都是支付成功 ）
		if(pluginId!=null && pluginId.equals("alipayDirectPlugin")&& trade_status!=null && (trade_status.equals("TRADE_FINISHED")|| trade_status.equals("TRADE_SUCCESS"))){
			paymentResult.setResult(1);
			paymentResult.setOrdersn(order.getSn());
			paymentResult.setOrderType(order.getOrderType());
			
		// 判断 pluginId不是null && pluginId 是支付宝支付 && trade_status支付状态不是null && 支付状态为 SUCCESS 是支付成功
		}else if(pluginId!=null && pluginId.equals("weixinPayPlugin")&& trade_status!=null && trade_status.equals("SUCCESS")){
			paymentResult.setResult(1);
			paymentResult.setOrdersn(order.getSn());
			paymentResult.setOrderType(order.getOrderType());
			
		}else {
			paymentResult.setResult(0);
			paymentResult.setError("支付出现问题");
		}
		
		return paymentResult;
	}

}
