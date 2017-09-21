package com.enation.app.shop.core.statistics.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.statistics.model.DayAmount;
import com.enation.app.shop.core.statistics.model.MonthAmount;
import com.enation.framework.database.Page;

/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：销售统计接口
 *  修改人：xulipeng
 *  修改时间：2015-09-24
 *  修改内容：制定初版
 *  
 */

public interface ISalesStatisticsManager {


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
	public List<Map> statisticsYear_Amount(Integer order_status, int year);
	
	/**
	 * 按月统计下单金额   默认当前月
	 * @author xulipeng
	 * 2015年05月14日15:45:03
	 * @return
	 */
	public List<Map> statisticsMonth_Amount(Integer order_status, int year,int month);
	
	/**
	 * 热卖商品排行—下单金额
	 * @return
	 */
	public List<Map> hotGoodsTop_Money();
	
	
	/**
	 * 热卖商品排行—下单量
	 * @return
	 */
	public List<Map> hotGoodsTop_Num();
	
	/**
	 * 销售收入统计列表
	 * @author xulipeng
	 * @param year		年
	 * @param month		月
	 * @param page		
	 * @param pageSize
	 * @param map		扩展参数
	 * @return	分页列表
	 */
	public Page getSalesIncome(int year,int month, int page,int pageSize,Map map);
	
	/**
	 * 收款金额
	 * @author xulipeng
	 * @param year		年
	 * @param month		月
	 * @param map		扩展参数
	 * @return	金额
	 */
	public Double getReceivables(int year,int month,Map map);
	
	/**
	 * 退款金额
	 * @author xulipeng
	 * @param year		年
	 * @param month		月
	 * @param map		扩展参数
	 * @return
	 */
	public Double getRefund(int year,int month,Map map);
	
	/**
	 * 实收金额
	 * @author xulipeng
	 * @param year		年
	 * @param month		月
	 * @param map		扩展参数
	 * @return
	 */
	public Double getPaid(int year,int month,Map map);


}
