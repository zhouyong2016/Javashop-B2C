package com.enation.app.shop.component.bonus.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.framework.database.Page;

/**
 * 红包类型管理
 * @author kingapex
 *2013-8-13下午2:38:47
 */
public interface IBonusTypeManager {
	
	/**
	 * 添加一个红包
	 * @param bronusType
	 */
	public void add(BonusType bronusType);
	
	
	/**
	 * 修改一个红包
	 * @param bronusType
	 */
	public void update(BonusType bonusType);
	
	
	/**
	 * 批量删除优惠券
	 * @param bronusTypeId
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public void delete(Integer [] bonusTypeId);
	
	/**
	 * 删除一个优惠券
	 * add by DMRain 2016-5-30
	 * @param bonusTypeId 优惠券ID
	 */
	public void del(Integer bonusTypeId);
	
	/**
	 * 分页读取红包类型
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page list(int page, int pageSize);
	
	
	/**
	 * 获取一个红包类型
	 * @param typeid
	 * @return
	 */
	public BonusType get(int typeid);
	
	/**
	 * 检查优惠券是否已经发放
	 * add by DMRain 2016-5-30
	 * @param type_id 优惠券ID
	 * @return result 0：否，1：是
	 */
	public int checkIsSend(Integer type_id);
	
	/**
	 * 检查优惠券是否已经关联了促销活动
	 * add by DMRain 2016-5-30
	 * @param type_id 优惠券ID
	 * @return result 0：否，1：是
	 */
	public int checkForActivity(Integer type_id);
	
	/**
	 * 判断优惠券识别码是否重复
	 * add by DMRain 2016-5-30
	 * @param recognition 优惠券识别码
	 * @return result 0：否，1：是
	 */
	public int checkRecognition(String recognition, Integer type_id);
}
