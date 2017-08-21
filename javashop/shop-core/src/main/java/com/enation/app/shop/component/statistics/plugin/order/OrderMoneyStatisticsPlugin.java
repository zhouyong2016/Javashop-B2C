package com.enation.app.shop.component.statistics.plugin.order;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.plugin.order.IOrderStatisDetailHtmlEvent;
import com.enation.app.shop.core.order.plugin.order.IOrderStatisTabShowEvent;
import com.enation.app.shop.core.statistics.service.ISalesStatisticsManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 订单统计—下单金额插件
 * @author xulipeng
 * 2015年05月13日19:22:25
 */

@Component
public class OrderMoneyStatisticsPlugin extends AutoRegisterPlugin implements IOrderStatisTabShowEvent,IOrderStatisDetailHtmlEvent {

	private ISalesStatisticsManager salesStatisticsManager ;
	
	@Override
	public String onShowOrderDetailHtml(Map map) {
		
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setPageName("order_money");
		
		return freeMarkerPaser.proessPageContent();
	}

	@Override
	public String getTabName() {
		return "下单金额";
	}

	@Override
	public int getOrder() {
		return 1;
	}
	
	public static int getDaysByYearMonth(int year, int month) {  
        Calendar a = Calendar.getInstance();  
        a.set(Calendar.YEAR, year);  
        a.set(Calendar.MONTH, month - 1);  
        a.set(Calendar.DATE, 1);  
        a.roll(Calendar.DATE, -1);  
        int maxDate = a.get(Calendar.DATE);  
        return maxDate;  
    }
	
	
	public static void main(String[] args) {
		int year =2015;
		int day=04;
	}
	
	public static String getYear(){
		
		return null;
	}

	public ISalesStatisticsManager getSalesStatisticsManager() {
		return salesStatisticsManager;
	}

	public void setSalesStatisticsManager(
			ISalesStatisticsManager salesStatisticsManager) {
		this.salesStatisticsManager = salesStatisticsManager;
	}

}
