package com.enation.app.shop.front.tag.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.OrderMeta;
import com.enation.app.shop.core.order.service.IOrderMetaManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 订单meta信息获取标签
 * @author kingapex
 *2013-9-25下午9:14:54
 */
@Component
@Scope("prototype")
public class OrderMetaTag extends BaseFreeMarkerTag {
	@Autowired
	private IOrderMetaManager orderMetaManager;
	
	/**
	 * @param orderid:订单id
	 * @return 订单meta信息
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer orderid  = (Integer)params.get("orderid");
		if(orderid == null){
			throw new TemplateModelException("必须提供orderid参数");
		}
		List<OrderMeta> metaList = orderMetaManager.list(orderid);
		Map map  = new HashMap();
		
		for (OrderMeta orderMeta : metaList) {
			String key  = orderMeta.getMeta_key();
			String value = orderMeta.getMeta_value();
			map.put(key, value);
		}
		return map;
	}
	

}
