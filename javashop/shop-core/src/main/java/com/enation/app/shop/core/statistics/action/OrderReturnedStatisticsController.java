package com.enation.app.shop.core.statistics.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.statistics.service.IReturnedStatisticsManager;

/**
 * 退款统计
 * @author DMRain 2016年3月5日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/orderReturnedStatistics")
public class OrderReturnedStatisticsController {

	@Autowired
	private IReturnedStatisticsManager returnedStatisticsManager;
	
	/**
	 * 获取退款统计列表
	 * @author kanon
	 * @param cycle_type 周期模式
	 * @param year 年
	 * @param month 月
	 * @return 退款统计列表页面
	 */
	@RequestMapping(value="/returned-statistics")
	public ModelAndView returnedStatistics(Integer cycle_type, Integer year, Integer month){
		ModelAndView view = new ModelAndView();
		Map map  = new HashMap();
		map.put("cycle_type", cycle_type);
		map.put("year", year);
		map.put("month", month);
		
		view.addObject("map", map);
		view.setViewName("/shop/admin/statistics/sales/tuikuan");
		return view;
	}
	
	/**
	 * 获取退款统计列表JSON列表
	 * @author kanon
	 * @param cycle_type 周期模式 
	 * @param year 年
	 * @param month 月
	 * @return 退款统计列表JSON列表
	 */
	@ResponseBody
	@RequestMapping(value="/returned-statistics-json")
	public Object returnedStatisticsJson(Integer cycle_type, Integer year, Integer month){
		String message = "[";
		//如果月的周期模式 
		if(cycle_type.intValue()==1){
			List<Map> list = returnedStatisticsManager.statisticsMonth_Amount( year, month);
			message += getMessage(cycle_type, "t_money", list);
		}else{
		//如果年的周期模式
			List<Map> list = returnedStatisticsManager.statisticsYear_Amount( year);
			message += getMessage(cycle_type, "t_money", list);
		}
		message = message.substring(0, message.length()-1)+"]";
		return "{\"result\":1,\"message\":"+message+"}";
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
}
