package com.enation.app.shop.component.statistics.plugin.order;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.plugin.order.IOrderStatisDetailHtmlEvent;
import com.enation.app.shop.core.order.plugin.order.IOrderStatisTabShowEvent;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPlugin;


/**
 * 订单统计—下单量插件
 * @author xulipeng
 * 2015年05月13日19:22:19
 */

@Component
public class OrderNumStatisticsPlugin extends AutoRegisterPlugin implements IOrderStatisTabShowEvent,IOrderStatisDetailHtmlEvent {

	@Override
	public String onShowOrderDetailHtml(Map map) {
		
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setPageName("order_num");
		//freeMarkerPaser.putData("typeid",12);
		
		return freeMarkerPaser.proessPageContent();
	}

	@Override
	public String getTabName() {
		return "下单量";
	}

	@Override
	public int getOrder() {
		return 2;
	}

}
