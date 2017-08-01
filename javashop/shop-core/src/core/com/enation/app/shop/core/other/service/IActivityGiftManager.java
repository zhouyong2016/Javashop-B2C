package com.enation.app.shop.core.other.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.other.model.ActivityGift;
import com.enation.framework.database.Page;

/**
 * 促销活动管理接口
 * 2016-5-24
 * @author DMRain
 * @version 1.0
 */
public interface IActivityGiftManager {

	/**
	 * 获取促销活动赠品分页列表
	 * @param keyword 搜索关键字
	 * @param page 页数
	 * @param pageSize 每页记录数
	 * @return
	 */
	public Page list(String keyword, int page, int pageSize);
	
	/**
	 * 添加赠品信息
	 * @param gift 赠品信息
	 */
	public void add(ActivityGift gift);
	
	/**
	 * 修改赠品信息
	 * @param gift 赠品信息
	 */
	public void edit(ActivityGift gift);
	
	/**
	 * 根据赠品ID获取赠品信息
	 * @param gift_id 赠品ID
	 * @return
	 */
	public ActivityGift get(Integer gift_id);
	
	/**
	 * 删除赠品信息
	 * @param gift_id 赠品ID
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer gift_id);
	
	/**
	 * 根据赠品ID判断赠品是否参与了促销活动(不包括已经结束的促销活动)
	 * @param gift_id 赠品ID
	 * @return result 0:未参与，1：已参与
	 */
	public int checkGiftInAct(Integer gift_id);
	
	/**
	 * 获取当前所有有效的赠品信息集合
	 * @return
	 */
	public List getGiftList();
	
	/**
	 * 获取当前所有有效的优惠券信息集合
	 * @return
	 */
	public List getBonusList();
	
	/**
	 * 修改赠品的库存
	 * @param gift_id 赠品ID
	 * @param type 修改类型：0：减少可用库存，1：增加可用库存，2：减少实际库存，3：增加实际库存
	 * @param num 减少或增加的库存数量
	 */
	public void updateGiftStore(Integer gift_id, Integer type, Integer num);
}
