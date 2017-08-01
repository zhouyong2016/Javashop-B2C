/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：会员统计接口
 *  修改人：Kanon
 *  修改时间：2015-09-23
 *  修改内容：制定初版
 */
package com.enation.app.shop.core.statistics.service;

import java.util.List;

/**
 * 区域分析
 * @author Kanon
 * @version v1.0,2015-09-23
 * @since v4.0
 */
public interface IRegionStatisticsManager {

	/**
	 * 获取区域分析JSON
	 * @param type 1.下单会员数、2.下单量、3.下单金额
	 * @param cycle_type 周期模式 1为月，反之则为年
	 * @param year 年
	 * @param month 月
	 * @return 区域分析JSON
	 */
	public String getRegionStatistics(Integer type,Integer cycle_type,Integer year,Integer month);
	
	
	/**
	 * 获取区域分析列表
	 * @param type 1.下单会员数、2.下单量、3.下单金额
	 * @param sort 正序、倒序
	 * @param cycle_type 周期模式
	 * @param year  年
	 * @param month 月
	 * @return 区域分析列表
	 */
	public List regionStatisticsList(Integer type,String sort,Integer cycle_type,Integer year,Integer month);
}
