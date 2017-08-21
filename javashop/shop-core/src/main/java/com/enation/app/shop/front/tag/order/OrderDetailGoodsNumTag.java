package com.enation.app.shop.front.tag.order;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 通过订单ID，获得该订单下商品的个数
 * @author wanghongjun
 * 2015-04-15 15:05
 */

@Component
@Scope("prototype")
public class OrderDetailGoodsNumTag extends BaseFreeMarkerTag{
	@Autowired
	private IOrderManager orderManager;
	private Integer orderid;
	
	@Override
	public Object exec(Map params) throws TemplateModelException {
		Integer orderid=(Integer) params.get("orderid");
		int count = this.orderManager.getOrderGoodsNum(orderid);
		return count;
	}

	
	
	

}
