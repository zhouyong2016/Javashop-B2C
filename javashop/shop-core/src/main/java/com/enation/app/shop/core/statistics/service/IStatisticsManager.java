package com.enation.app.shop.core.statistics.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.statistics.model.DayAmount;
import com.enation.app.shop.core.statistics.model.MonthAmount;

/**
 * 订单管理接口
 * 
 * @author lzf 2010-3-9 上午11:14:20 version 1.0
 */
public interface IStatisticsManager {


	/**
	 * 统计指定月份所在的年的 月-销售额
	 * 
	 * @param monthinput
	 * @return
	 */
	public List<MonthAmount> statisticsMonth_Amount(String monthinput);

	/**
	 * 统计当前月的天-销售额
	 * 
	 * @return
	 */
	public List<DayAmount> statisticsDay_Amount();

	/**
	 * 统计指定月份的天-销售额
	 * 
	 * @param monthinput
	 * @return
	 */
	public List<DayAmount> statisticsDay_Amount(String monthinput);

	
	/**
	 * 按支付方式统计订单 
	 * @return key:
	 * num - 订单数量
	 * amount -订单总额
	 * payment_name -支付方式名称
	 */
	public List<Map> orderStatByPayment();
	
	
	
	/**
	 * 按配送方式统计订单 
	 * @return
	 * key:
	 * num - 订单数量
	 * amount -订单总额
	 * shipping_type -配送方式名称
	 */
	public List<Map> orderStatByShip();
	
	
	/**
	 * 按年统计下单金额  默认当前年
	 * @author xulipeng
	 * 2015年05月14日15:45:00
	 * @return
	 */
	public List<Map> statisticsYear_Amount(Integer order_status, long start_time,long end_time);
	
	/**
	 * 按月统计下单金额   默认当前月
	 * @author xulipeng
	 * 2015年05月14日15:45:03
	 * @return
	 */
	public List<Map> statisticsMonth_Amount(Integer order_status, long start_time,long end_time);
	

}
