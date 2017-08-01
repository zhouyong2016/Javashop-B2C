package com.enation.app.shop.core.order.service.promotion;

import com.enation.app.shop.core.order.model.Promotion;


/**
 * 优惠减价行为接口
 * @author kingapex
 *2010-4-15下午04:40:57
 */
public interface IReducePriceBehavior  extends IPromotionBehavior{
	
	
	/**
	 * 减价方法
	 * @param orderPrice  原始金额
	 * @return 减价后的 金额
	 */
	public Double reducedPrice(Promotion pmt,Double price); 
	
	
}
