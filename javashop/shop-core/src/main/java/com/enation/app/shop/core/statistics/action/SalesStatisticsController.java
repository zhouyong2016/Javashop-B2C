package com.enation.app.shop.core.statistics.action;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.app.shop.core.statistics.service.ISalesStatisticsManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.database.Page;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonResultUtil;

import net.sf.json.JSONArray;

/**
 * 销售统计 
 * @author DMRain 2016年3月5日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/salesStatis")
public class SalesStatisticsController extends GridController{

	@Autowired
	private ISalesStatisticsManager salesStatisticsManager;
	
	/**
	 * 跳转到订单统计页面
	 * @param cycle_type 周期模式	1为月，反之则为年
	 * @param order_status 订单状态
	 * @param year 年
	 * @param month 月
	 * @param statusMap 订单状态的集合
	 * @param status_Json 订单状态的json
	 * @param order_statis_type 下单统计类型，1为下单金额，反之为为下单量
	 * @return
	 */
	@RequestMapping(value="/order-statis")
	public ModelAndView orderStatis(Integer cycle_type, Integer order_status, Integer year, Integer month,
			Map statusMap, String status_Json, Integer order_statis_type){
		ModelAndView view = new ModelAndView();
		
		if(cycle_type==null){
			cycle_type=1;
		}
		view.addObject("cycle_type", cycle_type);
		
		if(order_status==null){
			order_status=99;
		}
		view.addObject("order_status", order_status);
		
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		view.addObject("year", year);
		
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}
		view.addObject("month", month);
		
		if(statusMap.size() == 0){
			statusMap = new HashMap();
			statusMap = getStatusJson();
			String p = JSONArray.fromObject(statusMap).toString();
			status_Json = p.replace("[", "").replace("]", "");
		}
		view.addObject("statusMap", statusMap);
		view.addObject("status_Json", status_Json);
		
		if(order_statis_type==null){
			order_statis_type=1;
		}
		view.addObject("order_statis_type", order_statis_type);
		
		view.addObject("pageSize", this.getPageSize());
		
		if(order_statis_type.intValue()==1){
			view.setViewName("/shop/admin/statistics/sales/order_money");
		}else{
			view.setViewName("/shop/admin/statistics/sales/order_num");
		}
		
		return view;
	}
	
	/**
	 * 获取销售量统计的甘特图json
	 * @param cycle_type 周期模式	1为月，反之则为年
	 * @param order_status 订单状态
	 * @param year 年
	 * @param month 月
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-sale-num-json")
	public Object getSaleNumJson(Integer cycle_type, Integer order_status, Integer year, Integer month){
		String message ="[";
		if(cycle_type.intValue()==1){
			List<Map> list= salesStatisticsManager.statisticsMonth_Amount(order_status, year, month);
			message += getMessage(cycle_type, "t_num", list);
		}else{
			List<Map> list= salesStatisticsManager.statisticsYear_Amount(order_status, year);
			message += getMessage(cycle_type, "t_num", list);
		}
		message=message.substring(0, message.length()-1)+"]";
		
		return "{\"result\":1,\"message\":"+message+"}";
	}
	
	/**
	 * 获取销售金额统计的甘特图json
	 * @param cycle_type 周期模式	1为月，反之则为年
	 * @param order_status 订单状态
	 * @param year 年
	 * @param month 月
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-sale-money-json")
	public Object getSaleMoneyJson(Integer cycle_type, Integer order_status, Integer year, Integer month){
		
		String message ="[";
		if(cycle_type.intValue()==1){
			List<Map> list= salesStatisticsManager.statisticsMonth_Amount(order_status, year, month);
			message += getMessage(cycle_type, "t_money", list);
		}else{
			List<Map> list= salesStatisticsManager.statisticsYear_Amount(order_status, year);
			message += getMessage(cycle_type, "t_money", list);
		}
		message=message.substring(0, message.length()-1)+"]";
		
		return "{\"result\":1,\"message\":"+message+"}";
	}
	
	/**
	 * 销售收入统计
	 * @param year 年
	 * @param month 月
	 * @param receivables 收款金额
	 * @param refund 退款金额
	 * @param paid 实收金额
	 * @return
	 */
	@RequestMapping(value="/sale-income")
	public ModelAndView saleIncome(Integer year, Integer month, Double receivables, Double refund, Double paid){
		ModelAndView view = new ModelAndView();
		Calendar now = Calendar.getInstance();  
		if(year==null||year==0){
			year=now.get(Calendar.YEAR);
		}
		if(month==null||month==0){
			month=Integer.parseInt(now.get(Calendar.MONTH) + 1 + "");
		}
		receivables =  this.salesStatisticsManager.getReceivables(year, month, null);
		view.addObject("receivables", receivables);
		
		refund = this.salesStatisticsManager.getRefund(year, month, null);
		view.addObject("refund", refund);
		
		paid = this.salesStatisticsManager.getPaid(year, month, null);
		view.addObject("paid", paid);
		
		view.addObject("pageSize", this.getPageSize());
		view.setViewName("/shop/admin/statistics/sales/sales_list");
		return view;
	}
	
	/**
	 * 销售收入统计json数据
	 * @param year 年
	 * @param month 月
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/sale-income-json")
	public GridJsonResult saleIncomeJson(Integer year, Integer month){
		
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}
		Page list = this.salesStatisticsManager.getSalesIncome(year, month, this.getPage(), this.getPageSize(), null);
		return JsonResultUtil.getGridJson(list);
	}
	
	/**
	 * 销售收入统计总览json
	 * @param year 年
	 * @param month 月
	 * @param receivables 收款金额
	 * @param refund 退款金额
	 * @param paid 实收金额
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/sale-income-totle-json")
	public Object saleIncomeTotleJson(Integer year, Integer month, Double receivables, Double refund, Double paid){
		receivables =  this.salesStatisticsManager.getReceivables(year, month, null);
		refund = this.salesStatisticsManager.getRefund(year, month, null);
		paid = this.salesStatisticsManager.getPaid(year, month, null);
		
		Map map = new HashMap();
		map.put("receivables", receivables);
		map.put("refund", refund);
		map.put("paid", paid);
		
		return JsonMessageUtil.getObjectJson(map);
	}
	
	/**
	 * 判断周期模式（按年或者按月），并返回相应的字串
	 * @author xulipeng
	 * @param cycle_type 	周期模式
	 * @param param		t_num：总订单数，t_money：总金额
	 * @param list	数据集合
	 * @return
	 */
	private String getMessage(int cycle_type,String param,List<Map> list){
		int num = 0;
		if(cycle_type==1){
			num=31;
		}else{
			num=12;
		}

		String message = "";
		for (int i = 1; i <= num; i++) {
			boolean flag = true;
			for (int j =0;j<list.size();j++) {
				Map map = list.get(j);
				if(!map.get("month").toString().equals("0") && i==Integer.parseInt(map.get("month").toString())){
					message = message+map.get(param).toString()+",";
					flag = false;
				}
			}
			if(flag){
				message = message+"0,";
			}
		}
		return message;
	}
	
	/**
	 * 订单状态集合
	 * @author xulipeng
	 * @return 
	 */
	private Map getStatusJson(){
		Map orderStatus = new  HashMap();
		
		orderStatus.put(""+OrderStatus.ORDER_NOT_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_PAY));
		orderStatus.put(""+OrderStatus.ORDER_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CONFIRM));
		orderStatus.put(""+OrderStatus.ORDER_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY));
		
		orderStatus.put(""+OrderStatus.ORDER_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SHIP));
		orderStatus.put(""+OrderStatus.ORDER_ROG, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ROG));
		orderStatus.put(""+OrderStatus.ORDER_COMPLETE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_COMPLETE));
		orderStatus.put(""+OrderStatus.ORDER_CANCELLATION, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCELLATION));
		orderStatus.put(""+OrderStatus.ORDER_MAINTENANCE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_MAINTENANCE));
		
		//暂停使用的订单状态
		//orderStatus.put(""+OrderStatus.ORDER_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY));
		//orderStatus.put(""+OrderStatus.ORDER_NOT_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_CONFIRM)); 
		//orderStatus.put(""+OrderStatus.ORDER_ALLOCATION_YES, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ALLOCATION_YES));
		return orderStatus;
	}
}
