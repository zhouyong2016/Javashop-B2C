package com.enation.app.shop.core.goods.service;

import java.util.List;

import com.enation.framework.database.Page;

/**
 * 仓库监控
 * @author xiaokx
 *
 */
public interface IDepotMonitorManager {
	/**
	 * 获取所有仓库 未维护任务统计
	 * @return
	 */
	public List getTaskList();
	
	/**
	 *  获取所有仓库 未发货任务统计
	 * @return
	 */
	public List getSendList();
	
	/**
	 * 根据订单状态获取订单数量
	 * @return
	 */
	public int getTotalByStatus(int status);
	
	/**
	 * 某商品所有仓库库存明细
	 * @param goodsid 商品标识
	 * @param catid   商品分类
	 * @return
	 */
	public List depotidDepotByGoodsid(int goodsid,int catid);
	
	/**
	 * 按天统计销售额
	 * @param startDate 开始日期时间戳
	 * @param endDate	结束日期时间戳
	 * @return
	 */
	public List searchOrderSalesAmout(long startDate,long endDate);
	
	/**
	 * 按天统计销售量
	 * @param startDate 开始日期时间戳
	 * @param endDate 结束日期时间戳
	 * @param catid 商品类别
	 * @return
	 */
	public List searchOrderSaleNumber(long startDate,long endDate,int catid);
	
	/**
	 * 库存日志分页列表
	 * @param startDate 开始日期时间戳
	 * @param endDate   结束日期时间戳
	 * @param depotid	仓库标识
	 * @param depotType 仓库类型 0网站1实体店
	 * @param opType	操作类型 0进货1实体店出货2.网店订单发货
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page searchStoreLog(long startDate,long endDate,int depotid,int depotType,int opType,int page,int pageSize);
	
}
