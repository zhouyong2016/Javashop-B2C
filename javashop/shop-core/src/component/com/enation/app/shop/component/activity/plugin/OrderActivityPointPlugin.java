package com.enation.app.shop.component.activity.plugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.member.service.IMemberPointManger;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.plugin.order.IOrderRogconfirmEvent;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 确认收货会员增加促销积分插件
 * @author DMRain
 * @date 2016-7-21
 * @since v61
 * @version 1.0
 */
@Component
public class OrderActivityPointPlugin extends AutoRegisterPlugin implements IOrderRogconfirmEvent{

	@Autowired
	private IMemberManager memberManager;
	
	@Autowired
	private IMemberPointManger memberPointManger;
	
	@Override
	public void rogConfirm(Order order) {
		//如果订单中的促销活动赠送的积分不等于0
		if (order.getActivity_point() != 0) {
			//添加会员积分
			this.memberPointManger.add(this.memberManager.get(order.getMember_id()), 0, "促销活动获得积分", order.getMember_id(), order.getActivity_point(),1);
			
			//删除会员冻结积分
			this.memberPointManger.delFreezePoint(order.getMember_id(), order.getOrder_id());
		}
		
	}

}
