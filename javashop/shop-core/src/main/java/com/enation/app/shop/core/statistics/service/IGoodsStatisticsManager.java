package com.enation.app.shop.core.statistics.service;

import java.util.List;
import java.util.Map;

import com.enation.framework.database.Page;

/**
 * 商品分析统计接口
 * @author xulipeng
 *	
 */

public interface IGoodsStatisticsManager {
	
	/**
	 * 获取价格销量列表
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @param catid		分类id
	 * @param list		价格区间数组
	 * @param map		扩展参数
	 * @return
	 */
	public List getPriceSalesList(long startTime,long endTime,Integer catid,List<Map> list,Map map);
	
	
	/**
	 * 获取热卖商品的top列表(下单金额)
	 * @param startTime
	 * @param endTime
	 * @param catid
	 * @param map
	 * @return
	 */
	public Page getHotGoodsMoney(long startTime,long endTime,int page,int pageSize,Integer catid,Map map);
	
	/**
	 * 获取热卖商品的top列表(下单量)
	 * @param startTime
	 * @param endTime
	 * @param catid
	 * @param map
	 * @return
	 */
	public Page getHotGoodsNum(long startTime,long endTime,int page,int pageSize,Integer catid,Map map);
	
	/**
	 * 获取商品销售明细的json
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param pageSize
	 * @param catid
	 * @param name
	 * @param map
	 * @return
	 */
	public Page getgoodsSalesDetail(long startTime,long endTime,int page,int pageSize,Integer catid,String name,Map map);
	
	
	/**
	 * 读取收藏统计列表
	 * 按照收藏量从高到低
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page getCollectList(int page,int pageSize);

}
