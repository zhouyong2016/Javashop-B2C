package com.enation.app.shop.component.activity.plugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.plugin.order.IOrderCanelEvent;
import com.enation.app.shop.core.order.service.IOrderBonusManager;
import com.enation.app.shop.core.other.service.IActivityGiftManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 订单取消时增加订单赠品的可用库存插件
 * @author DMRain
 * @date 2016-6-12
 * @version 1.0
 */
@Component
public class OrderCancelGiftStorePlugin extends AutoRegisterPlugin implements IOrderCanelEvent{

	@Autowired
	private IActivityGiftManager activityGiftManager;
	
	@Autowired
	private IOrderBonusManager orderBonusManager;
	
	@Override
	public void canel(Order order) {
		
		//如果订单信息中的赠品id不等于0并且不为空
		if (order.getGift_id() != null && order.getGift_id() != 0) {
			this.activityGiftManager.updateGiftStore(order.getGift_id(), 1, 1);
		}
		
		//如果当前运行的程序为b2b2c项目
		if (EopSetting.PRODUCT.equals("b2b2c")) {
			
			//如果订单信息中的优惠券id不等于0并且不为空
			if (order.getBonus_id() != null && order.getBonus_id() != 0) {
				this.orderBonusManager.updateBonusReceivedNum(order.getBonus_id(), 1);
			}
		}
		
	}

}
