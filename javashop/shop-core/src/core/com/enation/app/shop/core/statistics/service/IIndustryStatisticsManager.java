/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：销售统计接口
 *  修改人：liushuai
 *  修改时间：2015-09-24
 *  修改内容：制定初版
 *  
 */
package com.enation.app.shop.core.statistics.service;

import java.util.List; 
import java.util.Map;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.statistics.model.Collect; 

/**
 * 统计接口 
 */
public interface IIndustryStatisticsManager {

	/**
	 * 统计 销售额
	 * @param 查询类型 
	 * @param 查询年
	 * @param 查询月
	 * @return
	 */
	public List<Map> statistics_price(int type,int year,int month);
	/**
	 * 统计订单
	 * @param 查询类型 
	 * @param 查询年
	 * @param 查询月
	 * @return
	 */
	public List<Map> statistics_order(int type,int year,int month);
	/**
	 * 统计-商品数
	 * @param 查询类型 
	 * @param 查询年
	 * @param 查询月
	 * @return
	 */
	public List<Map> statistics_goods(int type,int year,int month);
	
	/**
	 * 获取所有的一级菜单
	 * @return 
	 */
	public List<Map> getAllRootMenu();
	
	
	/** 
	 * 销售总览
	 * @param 行业 id
	 * @param 二级分类集合 
	 * @return 
	 */
	public List<Collect> listCollect(int act_id,List<Cat> cats); 
}
