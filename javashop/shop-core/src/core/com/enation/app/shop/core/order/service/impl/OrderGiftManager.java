package com.enation.app.shop.core.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.order.model.OrderGift;
import com.enation.app.shop.core.order.service.IOrderGiftManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;

/**
 * 订单赠品管理接口实现类
 * @author DMRain
 * @date 2016年4月19日
 * @version v1.0
 * @since v1.0
 */
@Service("orderGiftManager")
public class OrderGiftManager implements IOrderGiftManager{

	@Autowired
	private IDaoSupport  daoSupport;
	
	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderGiftManager#getOrderGift(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public OrderGift getOrderGift(Integer gift_id, Integer order_id) {
		String sql = "select * from es_order_gift where gift_id = ? and order_id = ?";
		return this.daoSupport.queryForObject(sql, OrderGift.class, gift_id, order_id);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderGiftManager#addOrderGift(com.enation.app.shop.core.order.model.OrderGift, java.lang.Integer)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void addOrderGift(OrderGift orderGift, Integer gift_store) {
		String sql = "update es_activity_gift set enable_store = " + (gift_store - 1) + " where gift_id = ?";
		this.daoSupport.execute(sql, orderGift.getGift_id());
		
		this.daoSupport.insert("es_order_gift", orderGift);
		
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderGiftManager#updateGiftStatus(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Log(type=LogType.GOODS,detail="修改订单ID为${order_id}的赠品状态，赠品ID为${gift_id}")
	public void updateGiftStatus(Integer gift_id, Integer order_id, Integer state) {
		OrderGift orderGift = this.getOrderGift(gift_id, order_id);
		
		if(orderGift!=null ){
			//如果订单状态为已完成退货
			if (orderGift.getGift_status() == 2) {
				this.daoSupport.execute("update es_activity_gift set enable_store = (enable_store + 1),actual_store = (actual_store + 1) where gift_id = ?", gift_id);
			}
			
			String sql = "update es_order_gift set gift_status = ? where gift_id = ? and order_id = ?";
			this.daoSupport.execute(sql, state, gift_id, order_id);
		}
		
	}
}
