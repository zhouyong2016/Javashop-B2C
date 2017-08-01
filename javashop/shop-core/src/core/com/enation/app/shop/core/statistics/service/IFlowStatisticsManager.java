/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述： 流量统计接口
 *  修改人：Sylow
 *  修改时间：2015-10-05
 *  修改内容：制定初版
 */
package com.enation.app.shop.core.statistics.service;

import java.util.List;
import java.util.Map;

/**
 * 流量统计接口
 * @author Sylow
 * @version v1.0,2015-10-05
 * @since v4.0
 */
public interface IFlowStatisticsManager {
	
	/**
	 * 增加访问日志
	 * @param goodsId 商品id
	 */
	public void addFlowLog(int goodsId,int store_id);
	
	/**
	 * 获得流量统计数据 按时间分组
	 * @param statisticsType 统计类型  0=按月查询 1=按年查询
	 * @param startDate 开始时间戳
	 * @param endDate 结束时间戳
	 * @return 流量统计数据List
	 */
	public List<Map<String, Object>> getFlowStatistics(String statisticsType,String startDate, String endDate);
	
	/**
	 * 获取商品访问量统计 按照商品分组
	 * @param topNum  排名数
	 * @param startDate 开始时间戳
	 * @param endDate 结束时间戳
	 * @return 商品流量统计数据List
	 */
	public List<Map<String, Object>> getGoodsFlowStatistics(int topNum, String startDate, String endDate);
	
}
