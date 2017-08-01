package com.enation.app.shop.component.spec.plugin.order;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Component;

import com.enation.app.shop.component.product.plugin.order.GenericOrderPlugin;
import com.enation.app.shop.core.order.model.OrderItem;
import com.enation.app.shop.core.order.plugin.order.IOrderItemFilter;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.util.StringUtil;

/**
 * 商品规格订单插件
 * 继承GenericOrderPlugin ，发货更新库存逻辑不同。
 * 同时实现了IOrderItemFilter，以在发货时显示出不同的规格
 * @author kingapex
 *2012-3-26下午3:55:12
 */
@Component
public class GoodsSpecOrderPlugin extends GenericOrderPlugin implements IOrderItemFilter {

	
	/** 
	 * 对订单项进行过滤
	 * 在每个订单项中显示具体的规格值
	 */
	@Override
	public void filter(Integer orderid, List<OrderItem> itemList) {
		
		for(OrderItem item:itemList){
			String addon = item.getAddon();
			if(!StringUtil.isEmpty(addon)){
				JSONArray specArray=	JSONArray.fromObject(addon);
				List<Map> specList = (List) JSONArray.toCollection(specArray,Map.class);
				FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
				freeMarkerPaser.setClz(this.getClass());
				freeMarkerPaser.putData("specList",specList);
				freeMarkerPaser.setPageName("order_item_spec");
				String html = freeMarkerPaser.proessPageContent(); 
				item.setOther(html);//扩展后台订单详细处
			}
		}

	}



}
