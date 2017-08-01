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
 *  订单详细发退货日志显示插件
 * @author kingapex
 *2012-2-17上午10:25:22
 *@author LiFenLong 2014-4-14;4.0改版订单信息显示修改为不走异步
 */

@Component
public class OrderDetailShipLogPlugin extends AutoRegisterPlugin implements
IOrderTabShowEvent ,IShowOrderDetailHtmlEvent,IAjaxExecuteEnable{
	
	@Autowired
	private IOrderReportManager orderReportManager;
	@Override
	public boolean canBeExecute(Order order) {
		 
		return true;
	}
	/**
	 * 异步请求走这里
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
		int orderId  =  StringUtil.toInt(request.getParameter("orderid"),true);
		
		List shipLogList = orderReportManager.listDelivery(orderId, 1);
		List reshipLogList = orderReportManager.listDelivery(orderId, 2);
		List chshipLogList = orderReportManager.listDelivery(orderId, 3);
		
		freeMarkerPaser.putData("shipLogList",shipLogList);
		freeMarkerPaser.putData("reshipLogList",reshipLogList);
		freeMarkerPaser.putData("chshipLogList",chshipLogList);
		
		freeMarkerPaser.setPageName("shiplog_list");
		return  freeMarkerPaser.proessPageContent();
		
	}

	@Override
	public String getTabName(Order order) {
		 
		return "发退货记录";
	}

	@Override
	public int getOrder() {
		 
		return 7;
	}

}
