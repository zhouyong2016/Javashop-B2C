package com.enation.app.shop.core.other.service;

import java.util.List;

import com.enation.app.shop.core.other.model.ActivityDetail;
import com.enation.app.shop.core.other.model.SecondHalfActivityDetail;
import com.enation.app.shop.core.other.model.SingleReductionActivityDetail;

/**
 * 促销活动优惠详细管理接口
 * 2016-5-27
 * @author DMRain
 * @version 1.0
 */
public interface IActivityDetailManager {

	/**
	 * 添加满减促销活动优惠详细
	 * @param detail 促销活动优惠详细
	 */
	public void add(ActivityDetail detail);
	
	/**
	 * 编辑满减促销活动优惠详细
	 * @param detail 促销活动优惠详细信息
	 */
	public void edit(ActivityDetail detail);
	
	/**
	 * 根据促销活动优惠详细ID获取促销活动优惠详细
	 * @param detail_id 促销活动优惠详细ID
	 * @return
	 */
	public ActivityDetail get(Integer detail_id);
	
	/**
	 * 根据促销活动id获取促销活动优惠详细
	 * @param activity_id 促销活动id
	 * @return
	 */
	public ActivityDetail getDetail(Integer activity_id);
	
	/**
	 * 根据促销活动ID获取促销活动优惠详细
	 * @param activity_id 促销活动ID
	 * @return
	 */
	public List listDetail(Integer activity_id);
	/**
	 * 根据促销活动ID获取满优惠促销活动优惠详细,超过领取次数的优惠券则不显示赠送优惠券信息
	 * @param activity_id 促销活动ID
	 * @return
	 */
	public List listActivityDetail(Integer activity_id);
	/**
	 * 添加单品立减促销活动优惠详细
	 * @param detail 促销活动优惠详细
	 */
	public void add(SingleReductionActivityDetail detail);
	/**
	 * 添加第二件半价促销活动优惠详细
	 * @param detail 促销活动优惠详细
	 */
	public void add(SecondHalfActivityDetail detail);
	/**
	 * 编辑单品立减促销活动优惠详细
	 * @param detail 促销活动优惠详细信息
	 */
	public void edit(SingleReductionActivityDetail detail);/**
	 * 编辑第二件半价促销活动优惠详细
	 * @param detail 促销活动优惠详细信息
	 */
	public void edit(SecondHalfActivityDetail detail);
	/**
	 * 根据促销活动ID获取单品立减促销活动优惠详细
	 * @param activity_id 促销活动ID
	 * @return
	 */
	public List listSingleReductionDetail(Integer activity_id);
	/**
	 * 根据促销活动ID获取第二件半价促销活动优惠详细
	 * @param activity_id 促销活动ID
	 * @return
	 */
	public List listSecondHalfDetail(Integer activity_id);
}
