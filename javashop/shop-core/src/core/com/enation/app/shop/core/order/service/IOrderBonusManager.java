package com.enation.app.shop.core.order.service;

import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.core.order.model.OrderBonus;

/**
 * 订单优惠券管理接口
 * @author DMRain
 * @date 2016年4月19日
 * @version v1.0
 * @since v1.0
 */
public interface IOrderBonusManager {

	/**
	 * 获取一条订单优惠券信息
	 * @param gift_id 赠品ID
	 * @param order_id 订单ID
	 * @return
	 */
	public OrderBonus getOrderBonus(Integer bonus_id, Integer order_id);
	
	/**
	 * 添加订单优惠券信息
	 * @param orderBonus 订单优惠券信息
	 */
	public void add(OrderBonus orderBonus);
	
	/**
	 * 给会员发放一张优惠券
	 * @param bonus 优惠券信息
	 */
	public void send_bonus(MemberBonus bonus);
	
	/**
	 * 修改优惠券已被会员领取的数量
	 * @param bonus_id 优惠券id
	 * @param type 修改类型 0：增加，1：减少
	 * @return
	 */
	public void updateBonusReceivedNum(Integer bonus_id, Integer type);
}
