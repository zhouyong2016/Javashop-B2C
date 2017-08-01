package com.enation.app.shop.core.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.core.order.model.OrderBonus;
import com.enation.app.shop.core.order.service.IOrderBonusManager;
import com.enation.framework.database.IDaoSupport;

/**
 * 订单优惠券管理接口实现类
 * @author DMRain
 * @date 2016年4月19日
 * @version v1.0
 * @since v1.0
 */
@Service("orderBonusManager")
public class OrderBonusManager implements IOrderBonusManager{

	@Autowired
	private IDaoSupport daoSupport;
	
	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderBonusManager#getOrderBonus(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public OrderBonus getOrderBonus(Integer bonus_id, Integer order_id) {
		String sql = "select * from es_order_bonus where bonus_id = ? and order_id = ?";
		return this.daoSupport.queryForObject(sql, OrderBonus.class, bonus_id, order_id);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderBonusManager#add(com.enation.app.shop.core.order.model.OrderBonus)
	 */
	@Override
	public void add(OrderBonus orderBonus) {
		this.daoSupport.insert("es_order_bonus", orderBonus);
		
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderBonusManager#send_bonus(com.enation.app.shop.component.bonus.model.MemberBonus)
	 */
	@Override
	public void send_bonus(MemberBonus bonus) {
		this.daoSupport.insert("es_member_bonus", bonus);
		
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderBonusManager#updateBonusReceivedNum(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void updateBonusReceivedNum(Integer bonus_id, Integer type) {
		String sql = "";
		
		//如果修改类型等于0，也就是增加被领取的数量
		if (type == 0) {
			sql = "update es_bonus_type set received_num = (received_num + 1) where type_id = ?";
		} else {
			sql = "update es_bonus_type set received_num = (received_num - 1) where type_id = ?";
		}
		
		this.daoSupport.execute(sql, bonus_id);
	}

}
