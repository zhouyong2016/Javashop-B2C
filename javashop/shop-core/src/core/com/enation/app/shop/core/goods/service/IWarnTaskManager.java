package com.enation.app.shop.core.goods.service;

import java.util.List;
import java.util.Map;

import com.enation.framework.database.Page;


public interface IWarnTaskManager {

	/**
	 * 根据商品获取颜色
	 * @param goodsId
	 * @return
	 */
	public List<Map> listColor(Integer goodsId);
	
	/**
	 * 根据仓库获取紧急任务的商品
	 * @param depotId
	 * @param name
	 * @param sn
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page listdepot(Integer depotId,String name,String sn,int page,int pageSize);
	
	/**
	 * 获取紧急任务信息
	 * @param goodsId
	 * @return
	 */
	public Map listTask(Integer taskId);
	
	/**
	 * 保存维护任务
	 * @param map
	 * map中变量 depotval;//仓库串 sphereval;//度数串 cylinderval;//散光串 productval;//产品串 产品和颜色是一一对应的关系
	 */
	public void saveTask(Map map);
	/**
	 * 分页查询
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page listAll(Integer page,Integer pageSize);

	/**
	 * 根据商品Id获取货品Id
	 * @param goodsid 商品Id
	 * @return
	 */
	public Integer getProductId(Integer goodsid);
	
	/**
	 * 跟新紧急任务状态
	 * @param taskId
	 * @return
	 */
	public void updateTask(Integer taskId);

}
