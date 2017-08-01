package com.enation.app.shop.component.ordercore.plugin.log;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.plugin.order.IOrderTabShowEvent;
import com.enation.app.shop.core.order.plugin.order.IShowOrderDetailHtmlEvent;
import com.enation.app.shop.core.order.service.IOrderReportManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.plugin.IAjaxExecuteEnable;
import com.enation.framework.util.StringUtil;

/**
 * 订单详细页支付日志显示插件
 * 采用异步加载方式
 * @author kingapex
 *2012-2-16下午7:20:20
 *@author LiFenLong 2014-4-14;4.0改版订单信息显示修改为不走异步
 */
@Component
public class OrderDetailPayLogPlugin extends AutoRegisterPlugin implements
		IOrderTabShowEvent ,IShowOrderDetailHtmlEvent,IAjaxExecuteEnable{
	
	@Autowired
	private IOrderReportManager orderReportManager;

	@Override
	public boolean canBeExecute(Order order) {
		 
		return true;
	}
	 

	@Override
	public String getTabName(Order order) {
		 
		return "收退款记录";
	}

	@Override
	public String onShowOrderDetailHtml(Order order) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		int orderId  =  StringUtil.toInt(request.getParameter("orderid"),true);
		
		List payLogList = this.orderReportManager.listPayLogs(orderId);  //收款单
		List refundList = this.orderReportManager.listRefundLogs(orderId);
		
		freeMarkerPaser.putData("payLogList",payLogList);
		freeMarkerPaser.putData("refundList",refundList);
		
		freeMarkerPaser.setPageName("paylog_list");
		return  freeMarkerPaser.proessPageContent();
		
//		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
//		int orderId =order.getOrder_id();
//		freeMarkerPaser.putData("orderid",orderId);
//		freeMarkerPaser.setPageName("paylog");
//		return freeMarkerPaser.proessPageContent();
 
	}
	
	/**
	 * 异步请求走这里
	 */
	@Override
	public String execute() {
		return null;
	}
	
	//private void 
	
	@Override
	public int getOrder() {
	 
		return 5;
	}



}
