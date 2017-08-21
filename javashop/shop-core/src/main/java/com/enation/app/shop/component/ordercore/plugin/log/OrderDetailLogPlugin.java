package com.enation.app.shop.component.ordercore.plugin.log;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.plugin.order.IOrderTabShowEvent;
import com.enation.app.shop.core.order.plugin.order.IShowOrderDetailHtmlEvent;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.plugin.IAjaxExecuteEnable;

/**
 * 订单详细页，订单日志显示插件
 * @author kingapex
 *2012-2-17上午11:05:16
 *@author LiFenLong 2014-4-14;4.0改版订单信息显示修改为不走异步
 */

@Component
public class OrderDetailLogPlugin extends AutoRegisterPlugin implements
IOrderTabShowEvent ,IShowOrderDetailHtmlEvent,IAjaxExecuteEnable{
	
	@Autowired
	private IOrderManager orderManager;
	
 
	
	
	@Override
	public boolean canBeExecute(Order order) {
		 
		return true;
	}
	 
	
	/**
	 * 异步走这里
	 */
	@Override
	public String execute() {

		return null;
	}

	@Override
	public String onShowOrderDetailHtml(Order order) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
	 
		List logList = this.orderManager.listLogs(order.getOrder_id());
		freeMarkerPaser.putData("logList",logList);
 
		
		freeMarkerPaser.setPageName("log_list");
		return  freeMarkerPaser.proessPageContent();
		
	}

	@Override
	public String getTabName(Order order) {
		
		return "订单日志";
	}

	@Override
	public int getOrder() {
		
		return 3;
	}

}
