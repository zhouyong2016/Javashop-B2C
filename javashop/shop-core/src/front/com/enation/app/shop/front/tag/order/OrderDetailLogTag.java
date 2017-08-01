package com.enation.app.shop.front.tag.order;

import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 订单详细页，订单日志显示    whj 
 * @param orderid int型， 订单ID
 *2014-03-04下午3:54:32
 */
@Component
@Scope("prototype")
public class OrderDetailLogTag extends BaseFreeMarkerTag{

	@Autowired
	private IOrderManager orderManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {

		
		Integer orderId  = Integer.parseInt(params.get("orderid").toString());
		
		List logList = this.orderManager.listLogs(orderId);
		return  logList;
	}


}
