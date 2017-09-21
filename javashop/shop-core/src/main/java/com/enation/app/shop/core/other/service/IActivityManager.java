package com.enation.app.shop.core.other.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.other.model.Activity;
import com.enation.app.shop.core.other.model.ActivityDetail;
import com.enation.framework.database.Page;

/**
 * 促销活动管理接口
 * 2016-5-23
 * @author DMRain
 * @version 1.0
 */
public interface IActivityManager {

	/**
	 * 获取促销活动分页列表
	 * @param map 搜索参数集合
	 * @param page 页数
	 * @param pageSize 每页记录数
	 * @return
	 */
	public Page list(Map actMap, int page, int pageSize);
	
	/**
	 * 获取所有有效的商品分页列表
	 * @param keyword 搜索关键字
	 * @param page 页数 
	 * @param pageSize 每页记录数
	 * @return
	 */
	public Page listGoods(String keyword, Integer activity_id, int page, int pageSize);
	
	/**
	 * 新增促销活动
	 * @param activity 促销活动信息
	 * @param detail 促销活动详细信息
	 * @param goods_ids 参与促销活动的商品ID组
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(Activity activity, ActivityDetail detail, Integer[] goods_ids);
	
	/**
	 * 修改促销活动
	 * @param activity 促销活动信息
	 * @param detail 促销活动详细信息
	 * @param goods_ids 参与促销活动的商品ID组
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void edit(Activity activity, ActivityDetail detail, Integer[] goods_ids);
	
	/**
	 * 删除促销活动
	 * @param activity_id 促销活动ID
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer activity_id);
	
	/**
	 * 根据促销活动ID获取促销活动信息
	 * @param activity_id 促销活动ID
	 * @return
	 */
	public Activity get(Integer activity_id);
	
	/**
	 * 判断促销活动在同一时间段是否重复添加
	 * @param startTime 促销活动开始时间
	 * @param endTime 促销活动结束时间
	 * @param activity_id 促销活动ID
	 * @return result 0:不重复，1:重复
	 */
	public int checkActByDate(Long startTime, Long endTime, Integer activity_id);
	
	/**
	 * 获取当前正在进行的促销活动信息
	 * @return
	 */
	public Activity getCurrentAct();
	
	/**
	 * 判断商品是否参加了促销活动
	 * @param goods_id 商品ID
	 * @param activity_id 促销活动ID
	 * @return num 0：未参加，1：已参加
	 */
	public int checkGoodsAct(Integer goods_id, Integer activity_id);
	
	/**
	 * 根据促销活动ID获取一个促销活动详细map集合
	 * @param activity_id 促销活动ID
	 * @return
	 */
	public Map getActMap(Integer activity_id);
	
	/**
	 * 获取当前正在进行的促销活动详细
	 * @return
	 */
	public List<Map> getCurrActDetail();
	
	/**
	 * 判断促销活动是否结束或是否已删除
	 * @param activity_id 促销活动id
	 * @return result 0：有效，1：无效
	 */
	public int checkActivity(Integer activity_id);
	/**
	 * 获取所有有效的商品列表
	 * @param keyword 搜索关键字
	 * @param page 页数 
	 * @param pageSize 每页记录数
	 * @return
	 */
	public  List<Map> listGoods(String keyword, Integer activity_id);
}
