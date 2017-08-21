package com.enation.app.shop.core.order.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.order.model.OrderGift;

/**
 * 订单赠品管理接口
 * @author DMRain
 * @date 2016年4月19日
 * @version v1.0
 * @since v1.0
 */
public interface IOrderGiftManager {

	/**
	 * 获取一条订单赠品信息
	 * @param gift_id 赠品ID
	 * @param order_id 订单ID
	 * @return
	 */
	public OrderGift getOrderGift(Integer gift_id, Integer order_id);
	
	/**
	 * 添加订单赠品信息，同时减少赠品的可用库存
	 * @param orderGift 订单赠品信息
	 * @param gift_store 赠品的可用库存
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void addOrderGift(OrderGift orderGift, Integer gift_store);
	
	/**
	 * 修改订单赠品状态
	 * @param gift_id 赠品id
	 * @param order_id 订单id
	 * @param state 状态 0：正常，1：申请退货，2：退货完成，3：入库完成
	 */
	public void updateGiftStatus(Integer gift_id, Integer order_id, Integer state);
}
