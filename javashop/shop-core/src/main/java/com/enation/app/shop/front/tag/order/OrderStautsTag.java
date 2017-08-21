package com.enation.app.shop.front.tag.order;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 订单状态值标签
 * @author kingapex
 *2013-9-25下午9:51:40
 */
@Component
@Scope("prototype")
public class OrderStautsTag extends BaseFreeMarkerTag {

	/**
	 * @param 无
	 * @return 订单状态值，参见：{@link OrderStatus#getOrderStatusMap()} 
	 * 如,假设定义变量名为orderStatus，则orderStatus.ORDER_CHANGED 输出-7
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return  OrderStatus.getOrderStatusMap();
		
	}

}
