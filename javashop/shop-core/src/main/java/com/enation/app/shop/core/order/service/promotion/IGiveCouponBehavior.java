package com.enation.app.shop.core.order.service.promotion;

/**
 * 优惠行为-送优惠劵
 * @author kingapex
 *2010-4-15下午04:49:32
 */
public interface IGiveCouponBehavior  extends IPromotionBehavior{
	
	/**
	 * 送出优惠劵
	 */
	public void giveCoupon();
	
	
}
