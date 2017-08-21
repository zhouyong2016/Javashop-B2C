/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：会员统计接口
 *  修改人：Sylow
 *  修改时间：2015-09-23
 *  修改内容：制定初版
 */
package com.enation.app.shop.core.statistics.service;

import java.util.List;
import java.util.Map;

/**
 * 会员统计接口
 * @author Sylow
 * @version v1.0,2015-09-23
 * @since v4.0
 */
public interface IMemberStatisticsManager {
	
	/**
	 * 获取买家下单数量排行
	 * @param topNum 名次 默认为15
	 * @param startDate 开始时间戳
	 * @param endDate 结束时间戳
	 * @return List<Map> 买家排名（包含订单总数）
	 */
	public List<Map<String, Object>> getOrderNumTop(int topNum, String startDate, String endDate);
	
	/**
	 * 获取买家下单商品数量排行
	 * @param topNum 名次 默认为15
	 * @param startDate 开始时间戳
	 * @param endDate 结束时间戳
	 * @return List<Map> 买家排名（包含订单商品总数）
	 */
	public List<Map<String, Object>> getGoodsNumTop(int topNum, String startDate, String endDate);
	
	/**
	 * 获取买家下单总金额排行
	 * @param topNum 名次 默认为15
	 * @param startDate 开始时间戳
	 * @param endDate 结束时间戳
	 * @return 买家排名（包含订单总价）
	 */
	public List<Map<String, Object>> getOrderPriceTop(int topNum, String startDate, String endDate);
	
	/**
	 * 获取客单价分布数据
	 * @param sections 区间List  格式：0 100 200 
	 * @param startDate 开始时间戳
	 * @param endDate 结束时间戳
	 * @return 客单价分布List
	 */
	public List<Map<String, Object>> getOrderPriceDis(List<Integer> sections, String startDate, String endDate);
	
	/**
	 * 获取购买频次数据
	 * @param startDate 开始时间戳
	 * @param endDate 结束时间戳
	 * @return 购买频次数据List
	 */
	public List<Map<String, Object>> getBuyFre(String startDate, String endDate);
	
	/**
	 * 获取购买时段分布数据
	 * @param startDate 开始时间戳
	 * @param endDate 结束时间戳
	 * @return 购买时段分布数据 List
	 */
	public List<Map<String, Object>> getBuyTimeDis(String startDate, String endDate);
	

	/**
	 * 获得本月新增会员
	 * @param startDate 本月开始时间戳
	 * @param endDate   本月结束时间戳
	 * @return member_num 新增会员数量
	 */
	public List<Map<String, Object>> getAddMemberNum(String startDate, String endDate);
	
	/**
	 * 获得上月新增会员
	 * @param lastStartDate 本月开始时间戳
	 * @param lastEndDate   本月结束时间戳
	 * @return member_num 新增会员数量
	 */
	public List<Map<String, Object>> getLastAddMemberNum(String lastStartDate, String lastEndDate);
	
	
	/**
	 * 获得本年度新增会员
	 * @param startDate 本月开始时间戳
	 * @param endDate   本月结束时间戳
	 * @return member_num 新增会员数量
	 */
	public List<Map<String, Object>> getAddYearMemberNum(String startDate, String endDate);
	
	/**
	 * 获得上年度新增会员
	 * @param lastStartDate 本月开始时间戳
	 * @param lastEndDate   本月结束时间戳
	 * @return member_num 新增会员数量
	 */
	public List<Map<String, Object>> getLastAddYearMemberNum(String lastStartDate, String lastEndDate);
	
}
